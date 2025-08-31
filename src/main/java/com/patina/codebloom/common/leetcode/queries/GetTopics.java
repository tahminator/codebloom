package com.patina.codebloom.common.leetcode.queries;

public class GetTopics {
    public static final String QUERY = """
                    #graphql
                    query questionTopicTags {
                      questionTopicTags {
                        edges {
                          node {
                            name
                            slug
                          }
                        }
                      }
                    }
                                    """;
}
