package org.patinanetwork.codebloom.utilities.generator.complex;

import static org.junit.jupiter.api.Assertions.*;

import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.patinanetwork.codebloom.common.db.models.question.topic.LeetcodeTopicEnum;
import org.patinanetwork.codebloom.common.db.models.usertag.Tag;
import org.patinanetwork.codebloom.shared.tag.TagMetadataList;
import org.patinanetwork.codebloom.shared.topic.TopicMetadataList;

class ComplexJSTypesGeneratorTest {

    @Test
    void tagMetadataListContainsAllTags() {
        assertEquals(Tag.values().length, TagMetadataList.ENUM_TO_TAG_METADATA.size());
    }

    @Test
    void tagMetadataListHasShortNameField() {
        for (var metadata : TagMetadataList.ENUM_TO_TAG_METADATA.values()) {
            assertNotNull(metadata.getShortName());
        }
    }

    @Test
    void tagMetadataListHasNameField() {
        for (var metadata : TagMetadataList.ENUM_TO_TAG_METADATA.values()) {
            assertNotNull(metadata.getName());
        }
    }

    @Test
    void tagMetadataListHasApiKeyField() {
        for (var metadata : TagMetadataList.ENUM_TO_TAG_METADATA.values()) {
            assertNotNull(metadata.getApiKey());
        }
    }

    @Test
    void tagMetadataListHasAltField() {
        for (var metadata : TagMetadataList.ENUM_TO_TAG_METADATA.values()) {
            assertNotNull(metadata.getAlt());
        }
    }

    @Test
    void tagMetadataListAllFieldsNonEmpty() {
        for (var metadata : TagMetadataList.ENUM_TO_TAG_METADATA.values()) {
            assertFalse(metadata.getShortName().isBlank());
            assertFalse(metadata.getName().isBlank());
            assertFalse(metadata.getApiKey().isBlank());
            assertFalse(metadata.getAlt().isBlank());
        }
    }

    @Test
    void generateEnumToObjectRunsWithoutExceptionForTagMetadata() {
        var generator = new ComplexJSTypesGenerator();
        assertDoesNotThrow(() -> generator.generateEnumToObject(Generator.builder()
                .name("TAG_METADATA_LIST")
                .data(TagMetadataList.ENUM_TO_TAG_METADATA)
                .dataShape(DataShape.ENUM_TO_OBJECT)
                .build()));
    }

    @Test
    void generateEnumToObjectEmptyMapDoesNotThrow() {
        var generator = new ComplexJSTypesGenerator();
        assertDoesNotThrow(() -> generator.generateEnumToObject(Generator.builder()
                .name("TAG_METADATA_LIST")
                .data(new LinkedHashMap<>())
                .dataShape(DataShape.ENUM_TO_OBJECT)
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
                        .build()));
    }

    @Test
    void generateEnumToObjectRunsWithoutExceptionForTopicMetadata() {
        var generator = new ComplexJSTypesGenerator();
        assertDoesNotThrow(() -> generator.generateEnumToObject(Generator.builder()
                .name("TOPIC_METADATA_LIST")
                .data(TopicMetadataList.ENUM_TO_TOPIC_METADATA)
                .dataShape(DataShape.ENUM_TO_OBJECT)
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
