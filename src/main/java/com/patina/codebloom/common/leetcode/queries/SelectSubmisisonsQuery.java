package com.patina.codebloom.common.leetcode.queries;

public class SelectSubmisisonsQuery {
    public static String query = "#graphql\n" +
            "    query getRecentSubmissions($username: String!, $limit: Int) {\n" +
            "        recentSubmissionList(username: $username, limit: $limit) {\n" +
            "            title\n" +
            "            titleSlug\n" +
            "            timestamp\n" +
            "            statusDisplay\n" +
            "            lang\n" +
            "        }\n" +
            "    }";
};
