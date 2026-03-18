package org.patinanetwork.codebloom.utilities.generator.complex;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.annotations.VisibleForTesting;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.patinanetwork.codebloom.common.db.models.question.topic.TopicMetadataList;
import org.patinanetwork.codebloom.shared.tag.ParentTags;
import org.patinanetwork.codebloom.shared.tag.TagMetadataList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("dev | ci")
@Component
@Slf4j
public class ComplexJSTypesGenerator implements CommandLineRunner {
    private static final String EXPORT_TYPE_PREFIX = "export type ";
    private static final String TYPE_BODY_OPEN = " = {\n";
    private static final String CLOSE_BLOCK = "};\n\n";
    private static final String RECORD_BODY_OPEN = "> = {\n";

    @Autowired
    private ApplicationContext applicationContext;

    private final Set<String> imports = new HashSet<>();
    private final StringBuilder tsContent = new StringBuilder();

    @Override
    public void run(final String... args) throws Exception {
        log.info("Type generation command starting...");
        try {
            generateAll();
            writeToFile();
            log.info("Type generation command completed successfully.");
        } catch (Exception e) {
            log.error("Type generation command failed", e);
            int springExitCode = SpringApplication.exit(applicationContext, () -> -1);
            System.exit(springExitCode);
        }
    }

    private final ObjectMapper objectMapper =
            new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);

    private void generateAll() throws Exception {
        generateTypes();
        generate(Generator.builder()
                .name("PARENT_TAGS_TO_CHILD_TAGS")
                .data(ParentTags.ENUM_TO_ENUM_LIST)
                .dataShape(DataShape.ENUM_TO_ENUM_LIST)
                .build());
        generate(Generator.builder()
                .name("TAG_METADATA_LIST")
                .data(TagMetadataList.ENUM_TO_STRING_VALUE_MAP)
                .dataShape(DataShape.ENUM_TO_TAG_METADATA)
                .build());
        generate(Generator.builder()
                .name("TOPIC_METADATA_LIST")
                .data(TopicMetadataList.ENUM_TO_TOPIC_METADATA)
                .dataShape(DataShape.ENUM_TO_OBJECT)
                .objectClass("TopicMetadataObject")
                .build());
    }

    private void generateTypes() {
        generateTagMetadataObjectType();
        generateTopicMetadataObjectType();
    }

    private void generateTagMetadataObjectType() {
        String typeName = "TagMetadataObject";
        tsContent.append(EXPORT_TYPE_PREFIX).append(typeName).append(TYPE_BODY_OPEN);
        tsContent.append("  shortName: string;\n");
        tsContent.append("  name: string;\n");
        tsContent.append("  apiKey: string;\n");
        tsContent.append("  alt: string;\n");
        tsContent.append(CLOSE_BLOCK);
        log.info("Generated type: {}", typeName);
    }

    private void generateTopicMetadataObjectType() {
        String typeName = "TopicMetadataObject";
        tsContent.append(EXPORT_TYPE_PREFIX).append(typeName).append(TYPE_BODY_OPEN);
        tsContent.append("  name: string;\n");
        tsContent.append("  aliases?: string[];\n");
        tsContent.append(CLOSE_BLOCK);
        log.info("Generated type: {}", typeName);
    }

    private void generate(Generator generator) throws Exception {
        if (generator.getDataShape() == DataShape.ENUM_TO_ENUM_LIST) {
            generateEnumToListOfEnums(generator);
        } else if (generator.getDataShape() == DataShape.ENUM_TO_STRING_VALUE_MAP) {
            generateEnumToStringValueMap(generator);
        } else if (generator.getDataShape() == DataShape.ENUM_TO_TAG_METADATA) {
            generateEnumToTagMetadata(generator);
        } else if (generator.getDataShape() == DataShape.ENUM_TO_OBJECT) {
            generateEnumToObject(generator);
        }
    }

    private void generateEnumToListOfEnums(Generator generator) throws IOException {
        Map<?, List<?>> data = (Map<?, List<?>>) generator.getData();

        if (data.isEmpty()) {
            log.warn("Empty map provided for enum generation");
            return;
        }

        Object firstKey = data.keySet().iterator().next();
        if (!(firstKey instanceof Enum)) {
            throw new IllegalArgumentException("Expected Enum key, got: " + firstKey.getClass());
        }

        Enum<?> enumKey = (Enum<?>) firstKey;
        String enumClassName = enumKey.getDeclaringClass().getSimpleName();

        imports.add(enumClassName);

        tsContent
                .append("export const ")
                .append(generator.getName())
                .append(": Record<")
                .append(enumClassName)
                .append(", ")
                .append(enumClassName)
                .append("[]> = {\n");

        for (Map.Entry<?, List<?>> entry : data.entrySet()) {
            Enum<?> key = (Enum<?>) entry.getKey();
            List<?> values = entry.getValue();

            tsContent
                    .append("  [")
                    .append(enumClassName)
                    .append(".")
                    .append(key.name())
                    .append("]: [");

            String children = values.stream()
                    .map(v -> {
                        if (v instanceof Enum) {
                            return enumClassName + "." + ((Enum<?>) v).name();
                        }
                        throw new IllegalArgumentException("Expected Enum value in list");
                    })
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("");

            tsContent.append(children);
            tsContent.append("],\n");
        }

        tsContent.append("} as const;\n\n");

        log.info("Generated constant: {}", generator.getName());
    }

    @VisibleForTesting
    @SuppressWarnings("unchecked")
    void generateEnumToStringValueMap(Generator generator) {
        Map<?, Map<String, ?>> data = (Map<?, Map<String, ?>>) generator.getData();

        if (data.isEmpty()) {
            log.warn("Empty map provided for string value map generation");
            return;
        }

        Object firstKey = data.keySet().iterator().next();
        if (!(firstKey instanceof Enum)) {
            throw new IllegalArgumentException("Expected Enum key, got: " + firstKey.getClass());
        }

        Enum<?> enumKey = (Enum<?>) firstKey;
        String enumClassName = enumKey.getDeclaringClass().getSimpleName();
        imports.add(enumClassName);

        Set<String> fieldNames = data.values().iterator().next().keySet();
        String typeName = generator.getTypeName();
        boolean hasTypeName = typeName != null && !typeName.isEmpty();

        if (hasTypeName) {
            tsContent.append(EXPORT_TYPE_PREFIX).append(typeName).append(TYPE_BODY_OPEN);
            for (String fieldName : fieldNames) {
                tsContent.append("  ").append(fieldName).append(": string;\n");
            }
            tsContent.append(CLOSE_BLOCK);
        }

        String valueType = hasTypeName ? typeName : "{ [key: string]: string }";

        tsContent
                .append("export const ")
                .append(generator.getName())
                .append(": Record<")
                .append(enumClassName)
                .append(", ")
                .append(valueType)
                .append(RECORD_BODY_OPEN);

        for (Map.Entry<?, Map<String, ?>> entry : data.entrySet()) {
            Enum<?> key = (Enum<?>) entry.getKey();
            Map<String, ?> values = entry.getValue();

            tsContent
                    .append("  [")
                    .append(enumClassName)
                    .append(".")
                    .append(key.name())
                    .append("]: {\n");

            for (Map.Entry<String, ?> field : values.entrySet()) {
                tsContent
                        .append("    ")
                        .append(field.getKey())
                        .append(": \"")
                        .append(field.getValue())
                        .append("\",\n");
            }

            tsContent.append("  },\n");
        }

        tsContent.append("} as const;\n\n");

        log.info("Generated constant: {}", generator.getName());
    }

    @VisibleForTesting
    @SuppressWarnings("unchecked")
    void generateEnumToTagMetadata(Generator generator) {
        Map<?, Map<String, ?>> data = (Map<?, Map<String, ?>>) generator.getData();

        if (data.isEmpty()) {
            log.warn("Empty map provided for tag metadata generation");
            return;
        }

        Object firstKey = data.keySet().iterator().next();
        if (!(firstKey instanceof Enum)) {
            throw new IllegalArgumentException("Expected Enum key, got: " + firstKey.getClass());
        }

        Enum<?> enumKey = (Enum<?>) firstKey;
        String enumClassName = enumKey.getDeclaringClass().getSimpleName();
        imports.add(enumClassName);

        String typeName = "TagMetadataObject";

        tsContent
                .append("export const ")
                .append(generator.getName())
                .append(": Record<")
                .append(enumClassName)
                .append(", ")
                .append(typeName)
                .append(RECORD_BODY_OPEN);

        for (Map.Entry<?, Map<String, ?>> entry : data.entrySet()) {
            Enum<?> key = (Enum<?>) entry.getKey();
            Map<String, ?> values = entry.getValue();

            tsContent
                    .append("  [")
                    .append(enumClassName)
                    .append(".")
                    .append(key.name())
                    .append("]: {\n");

            for (Map.Entry<String, ?> field : values.entrySet()) {
                tsContent
                        .append("    ")
                        .append(field.getKey())
                        .append(": \"")
                        .append(field.getValue())
                        .append("\",\n");
            }

            tsContent.append("  },\n");
        }

        tsContent.append("} as const;\n\n");

        log.info("Generated constant: {}", generator.getName());
    }

    @VisibleForTesting
    void generateEnumToObject(Generator generator) throws Exception {
        Map<?, ?> data = (Map<?, ?>) generator.getData();

        if (data.isEmpty()) {
            log.warn("Empty map provided for object generation");
            return;
        }

        Object firstKey = data.keySet().iterator().next();
        if (!(firstKey instanceof Enum)) {
            throw new IllegalArgumentException("Expected Enum key, got: " + firstKey.getClass());
        }

        Enum<?> enumKey = (Enum<?>) firstKey;
        String enumClassName = enumKey.getDeclaringClass().getSimpleName();
        imports.add(enumClassName);

        String objectClass = generator.getObjectClass();

        tsContent
                .append("export const ")
                .append(generator.getName())
                .append(": Record<")
                .append(enumClassName)
                .append(", ")
                .append(objectClass)
                .append(RECORD_BODY_OPEN);

        for (Map.Entry<?, ?> entry : data.entrySet()) {
            Enum<?> key = (Enum<?>) entry.getKey();
            String jsonValue = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(entry.getValue());
            String tsValue = jsonToTypeScript(jsonValue);

            String[] lines = tsValue.split("\n");
            tsContent.append("  ").append(key.name()).append(": ").append(lines[0]);

            for (int i = 1; i < lines.length; i++) {
                tsContent.append("\n  ").append(lines[i]);
            }

            tsContent.append(",\n");
        }

        tsContent.append(CLOSE_BLOCK);

        log.info("Generated constant: {}", generator.getName());
    }

    /**
     * Converts Jackson pretty-printed JSON to TypeScript object literal style (unquoted keys, no space before colon).
     */
    private String jsonToTypeScript(String json) {
        return json.replaceAll("\"(\\w+)\" : ", "$1: ");
    }

    private void writeToFile() throws IOException {
        StringBuilder finalOutput = new StringBuilder();

        finalOutput.append("""
/**
 * This file was generated by the Codebloom backend.
 * DO NOT EDIT THIS FILE MANUALLY!!!
 */
            """);

        if (!imports.isEmpty()) {
            finalOutput.append("import { ");
            finalOutput.append(String.join(", ", imports));
            finalOutput.append(" } from \"@/lib/api/types/schema\";\n\n");
        }

        finalOutput.append(tsContent);

        Path outputPath = Paths.get("js/src/lib/api/types/complex.ts");
        Files.createDirectories(outputPath.getParent());
        Files.writeString(outputPath, finalOutput.toString());

        log.info("Generated TypeScript file: {} with {} import(s)", outputPath, imports.size());
    }
}
