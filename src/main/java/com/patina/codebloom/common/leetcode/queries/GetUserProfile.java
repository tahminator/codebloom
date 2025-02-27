package com.patina.codebloom.common.leetcode.queries;

public class GetUserProfile {
    public static final String QUERY = """
            #graphql
            query userPublicProfile($username: String!) {
              matchedUser(username: $username) {
                username
                profile {
                  ranking
                  userAvatar
                  realName
                  aboutMe
                }
              }
            }
            """;
}
