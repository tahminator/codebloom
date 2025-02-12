package com.patina.codebloom.common.leetcode.queries;

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
}
