package com.patina.codebloom.leetcode.client.queries;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SelectAcceptedSubmisisonsQuery {
    public static final String QUERY = """
                    #graphql
                    query recentAcSubmissions($username: String!, $limit: Int) {
                        recentAcSubmissionList(username: $username, limit: $limit) {
                            id
                            title
                            titleSlug
                            timestamp
                            statusDisplay
                            lang
                        }
                    }
                    """;

    public static String body(final String username) throws JsonProcessingException {
        // API doesn't let you get more than this amount.
        int limit = 20;

        ObjectMapper objectMapper = new ObjectMapper();

        Map<String, Object> requestBodyMap = new HashMap<>();
        requestBodyMap.put("query", QUERY);

        Map<String, Object> variables = new HashMap<>();
        variables.put("username", username);
        variables.put("limit", limit);
        requestBodyMap.put("variables", variables);

        return objectMapper.writeValueAsString(requestBodyMap);
    }
};
