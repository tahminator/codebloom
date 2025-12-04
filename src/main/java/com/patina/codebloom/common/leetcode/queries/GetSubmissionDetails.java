package com.patina.codebloom.common.leetcode.queries;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;

public class GetSubmissionDetails {

    public static final String QUERY = """
        #graphql
        query submissionDetails($submissionId: Int!) {
            submissionDetails(submissionId: $submissionId) {
              runtime
              runtimeDisplay
              runtimePercentile
              runtimeDistribution
              memory
              memoryDisplay
              memoryPercentile
              code
              timestamp
              lang {
                name
                verboseName
              }
            }
          }
                """;

    public static String body(final int submissionId) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        Map<String, Object> requestBodyMap = new HashMap<>();
        requestBodyMap.put("query", QUERY);

        Map<String, Integer> variables = new HashMap<>();
        variables.put("submissionId", submissionId);
        requestBodyMap.put("variables", variables);

        return objectMapper.writeValueAsString(requestBodyMap);
    }
}
