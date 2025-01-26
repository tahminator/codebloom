package com.patina.codebloom.common.leetcode.queries;

public class GetSubmissionDetails {
    public static String query = """
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
