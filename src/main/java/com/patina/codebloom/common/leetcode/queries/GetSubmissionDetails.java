package com.patina.codebloom.common.leetcode.queries;

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
}
