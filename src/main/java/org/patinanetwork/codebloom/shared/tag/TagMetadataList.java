package org.patinanetwork.codebloom.shared.tag;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.patinanetwork.codebloom.common.db.models.usertag.Tag;

public class TagMetadataList {
    public static final Map<Tag, Map<String, String>> ENUM_TO_STRING_VALUE_MAP = generate();

    private static Map<Tag, Map<String, String>> generate() {
        return Arrays.stream(Tag.values())
                .collect(Collectors.toMap(tag -> tag, TagMetadataList::buildMetadata, (a, b) -> a, LinkedHashMap::new));
    }

    private static Map<String, String> buildMetadata(Tag tag) {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("shortName", tag.getShortName());
        map.put("name", tag.getName());
        map.put("apiKey", tag.getApiKey());
        map.put("alt", tag.getAlt());
        return map;
    }
}
