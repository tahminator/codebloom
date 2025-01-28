package com.patina.codebloom.common.leetcode.queries;

public class SelectAcceptedSubmisisonsQuery {
    public static String query = "#graphql\n" +
            "    query recentAcSubmissions($username: String!, $limit: Int) {\n" +
            "        recentAcSubmissionList(username: $username, limit: $limit) {\n" +
            "            id\n" +
            "            title\n" +
            "            titleSlug\n" +
            "            timestamp\n" +
            "            statusDisplay\n" +
            "            lang\n" +
            "        }\n" +
            "    }";
};
