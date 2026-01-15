package org.patinanetwork.codebloom.common.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/** Output a pretty JSON string. */
public class JsonPrinter {

    /** Output a pretty JSON string. */
    public static String prettify(final String jsonString) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            return mapper.readTree(jsonString).toPrettyString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to prettify JSON string", e);
        }
    }
}
