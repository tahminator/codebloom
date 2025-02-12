package com.patina.codebloom.common.leetcode.queries;

public class SelectProblemQuery {
    public static final String QUERY = """
            #graphql
            query selectProblem($titleSlug: String!) {
                question(titleSlug: $titleSlug) {
                    questionId
                    title
                    titleSlug
                    content
                    difficulty
                    stats
                }
            }
            """;
}
