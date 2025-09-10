package com.patina.codebloom.common.leetcode.queries;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SelectProblemQuery {
    public static final String QUERY = """
                    #graphql
                    query selectProblem($titleSlug: String!) {
                        question(titleSlug: $titleSlug) {
                            questionId
                            title
                            titleSlug
                            content
                            difficulty
                            stats
                            topicTags {
                                id
                                name
                                slug
                            }
                        }
                    }
                    """;

    public static String body(final String slug) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        Map<String, Object> requestBodyMap = new HashMap<>();
        requestBodyMap.put("query", QUERY);

        Map<String, Object> variables = new HashMap<>();
        variables.put("titleSlug", slug);
        requestBodyMap.put("variables", variables);

        return objectMapper.writeValueAsString(requestBodyMap);
    }
}
