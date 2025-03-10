package com.patina.codebloom.website.leetcode.client.queries;

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
