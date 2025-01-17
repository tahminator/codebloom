package com.patina.codebloom.common.leetcode.queries;

public class SelectProblemQuery {
    public static String query = "#graphql\n" +
            "    query selectProblem($titleSlug: String!) {\n" +
            "        question(titleSlug: $titleSlug) {\n" +
            "            questionId\n" +
            "            title\n" +
            "            titleSlug\n" +
            "            content\n" +
            "            difficulty\n" +
            "            stats\n" +
            "        }\n" +
            "    }";
}
