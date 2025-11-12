package com.patina.codebloom.leetcode.client.queries;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GetPotd {
    public static final String QUERY = """
                    #graphql
                    query questionOfToday {
                        activeDailyCodingChallengeQuestion {
                            question {
                                titleSlug
                                title
                                difficulty
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
