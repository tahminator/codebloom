package org.patinanetwork.codebloom.utilities.generator.complex;

import static org.junit.jupiter.api.Assertions.*;

import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.patinanetwork.codebloom.common.db.models.question.topic.LeetcodeTopicEnum;
import org.patinanetwork.codebloom.common.db.models.question.topic.TopicMetadataList;
import org.patinanetwork.codebloom.common.db.models.usertag.Tag;
import org.patinanetwork.codebloom.shared.tag.TagMetadataList;

class ComplexJSTypesGeneratorTest {

    @Test
    void tagMetadataListContainsAllTags() {
        assertEquals(Tag.values().length, TagMetadataList.ENUM_TO_STRING_VALUE_MAP.size());
    }

    @Test
    void tagMetadataListHasShortNameField() {
        for (var metadata : TagMetadataList.ENUM_TO_STRING_VALUE_MAP.values()) {
            assertNotNull(metadata.get("shortName"));
        }
    }

    @Test
    void tagMetadataListHasNameField() {
        for (var metadata : TagMetadataList.ENUM_TO_STRING_VALUE_MAP.values()) {
            assertNotNull(metadata.get("name"));
        }
    }

    @Test
    void tagMetadataListHasApiKeyField() {
        for (var metadata : TagMetadataList.ENUM_TO_STRING_VALUE_MAP.values()) {
            assertNotNull(metadata.get("apiKey"));
        }
    }

    @Test
    void tagMetadataListHasAltField() {
        for (var metadata : TagMetadataList.ENUM_TO_STRING_VALUE_MAP.values()) {
            assertNotNull(metadata.get("alt"));
        }
    }

    @Test
    void tagMetadataListAllFieldsNonEmpty() {
        for (var metadata : TagMetadataList.ENUM_TO_STRING_VALUE_MAP.values()) {
            assertFalse(metadata.get("shortName").isBlank());
            assertFalse(metadata.get("name").isBlank());
            assertFalse(metadata.get("apiKey").isBlank());
            assertFalse(metadata.get("alt").isBlank());
        }
    }

    @Test
    void generateEnumToStringValueMapRunsWithoutException() {
        var generator = new ComplexJSTypesGenerator();
        assertDoesNotThrow(() -> generator.generateEnumToStringValueMap(Generator.builder()
                .name("TEST_MAP")
                .data(TagMetadataList.ENUM_TO_STRING_VALUE_MAP)
                .dataShape(DataShape.ENUM_TO_STRING_VALUE_MAP)
                .build()));
    }

    @Test
    void generateEnumToStringValueMapWithTypeNameRunsWithoutException() {
        var generator = new ComplexJSTypesGenerator();
        assertDoesNotThrow(() -> generator.generateEnumToStringValueMap(Generator.builder()
                .name("TEST_MAP")
                .data(TagMetadataList.ENUM_TO_STRING_VALUE_MAP)
                .dataShape(DataShape.ENUM_TO_STRING_VALUE_MAP)
                .typeName("TestType")
                .build()));
    }

    @Test
    void generateEnumToStringValueMapEmptyMapDoesNotThrow() {
        var generator = new ComplexJSTypesGenerator();
        assertDoesNotThrow(() -> generator.generateEnumToStringValueMap(Generator.builder()
                .name("TEST_MAP")
                .data(new LinkedHashMap<>())
                .dataShape(DataShape.ENUM_TO_STRING_VALUE_MAP)
                .build()));
    }

    @Test
    void generateEnumToStringValueMapThrowsOnNonEnumKey() {
        var invalidData = new LinkedHashMap<String, Map<String, String>>();
        invalidData.put("not-an-enum", Map.of("key", "value"));
        var generator = new ComplexJSTypesGenerator();
        assertThrows(
                IllegalArgumentException.class,
                () -> generator.generateEnumToStringValueMap(Generator.builder()
                        .name("TEST")
                        .data(invalidData)
                        .dataShape(DataShape.ENUM_TO_STRING_VALUE_MAP)
                        .build()));
    }

    @Test
    void generateEnumToTagMetadataRunsWithoutException() {
        var generator = new ComplexJSTypesGenerator();
        assertDoesNotThrow(() -> generator.generateEnumToTagMetadata(Generator.builder()
                .name("TAG_METADATA_LIST")
                .data(TagMetadataList.ENUM_TO_STRING_VALUE_MAP)
                .dataShape(DataShape.ENUM_TO_TAG_METADATA)
                .build()));
    }

    @Test
    void generateEnumToTagMetadataEmptyMapDoesNotThrow() {
        var generator = new ComplexJSTypesGenerator();
        assertDoesNotThrow(() -> generator.generateEnumToTagMetadata(Generator.builder()
                .name("TAG_METADATA_LIST")
                .data(new LinkedHashMap<>())
                .dataShape(DataShape.ENUM_TO_TAG_METADATA)
                .build()));
    }

    @Test
    void generateEnumToTagMetadataThrowsOnNonEnumKey() {
        var invalidData = new LinkedHashMap<String, Map<String, String>>();
        invalidData.put("not-an-enum", Map.of());
        var generator = new ComplexJSTypesGenerator();
        assertThrows(
                IllegalArgumentException.class,
                () -> generator.generateEnumToTagMetadata(Generator.builder()
                        .name("TEST")
                        .data(invalidData)
                        .dataShape(DataShape.ENUM_TO_TAG_METADATA)
                        .build()));
    }

    @Test
    void generateEnumToObjectRunsWithoutException() {
        var generator = new ComplexJSTypesGenerator();
        assertDoesNotThrow(() -> generator.generateEnumToObject(Generator.builder()
                .name("TOPIC_METADATA_LIST")
                .data(TopicMetadataList.ENUM_TO_TOPIC_METADATA)
                .dataShape(DataShape.ENUM_TO_OBJECT)
                .objectClass("TopicMetadataObject")
                .build()));
    }

    @Test
    void generateEnumToObjectEmptyMapDoesNotThrow() {
        var generator = new ComplexJSTypesGenerator();
        assertDoesNotThrow(() -> generator.generateEnumToObject(Generator.builder()
                .name("TOPIC_METADATA_LIST")
                .data(new LinkedHashMap<>())
                .dataShape(DataShape.ENUM_TO_OBJECT)
                .objectClass("TopicMetadataObject")
                .build()));
    }

    @Test
    void generateEnumToObjectThrowsOnNonEnumKey() {
        var invalidData = new LinkedHashMap<String, Object>();
        invalidData.put("not-an-enum", Map.of());
        var generator = new ComplexJSTypesGenerator();
        assertThrows(
                IllegalArgumentException.class,
                () -> generator.generateEnumToObject(Generator.builder()
                        .name("TEST")
                        .data(invalidData)
                        .dataShape(DataShape.ENUM_TO_OBJECT)
                        .objectClass("TopicMetadataObject")
                        .build()));
    }

    @Test
    void topicMetadataListContainsAllTopics() {
        assertEquals(LeetcodeTopicEnum.values().length, TopicMetadataList.ENUM_TO_TOPIC_METADATA.size());
    }

    @Test
    void topicMetadataListAllHaveNonEmptyName() {
        for (var metadata : TopicMetadataList.ENUM_TO_TOPIC_METADATA.values()) {
            assertNotNull(metadata.getName());
            assertFalse(metadata.getName().isBlank());
        }
    }
}
