package com.patina.codebloom.common.leetcode.queries;

public class GetPotd {
    public static String query = """
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
