package com.patina.codebloom.common.leetcode.queries;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GetAllProblems {
    public static final String QUERY = """
        query problemsetQuestionListV2($filters: QuestionFilterInput, $limit: Int, $skip: Int) {
            problemsetQuestionListV2(
                filters: $filters
                limit: $limit
                skip: $skip
            ) {
                questions {
                id
                titleSlug
                title
                translatedTitle
                questionFrontendId
                paidOnly
                difficulty
                topicTags {
                    name
                    slug
                    nameTranslated
                }
                acRate
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
