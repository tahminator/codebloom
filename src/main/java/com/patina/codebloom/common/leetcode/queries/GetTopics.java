package com.patina.codebloom.common.leetcode.queries;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;

public class GetTopics {

    public static final String QUERY = """
        #graphql
        query questionTopicTags {
          questionTopicTags {
            edges {
              node {
                name
                slug
              }
            }
          }
        }
                        """;

    public static String body() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        Map<String, Object> requestBodyMap = new HashMap<>();
        requestBodyMap.put("query", QUERY);

        return objectMapper.writeValueAsString(requestBodyMap);
    }
}
