package com.patina.codebloom.common.leetcode.queries;

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
};
