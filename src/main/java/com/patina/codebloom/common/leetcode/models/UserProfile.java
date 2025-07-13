package com.patina.codebloom.common.leetcode.models;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Getter
@Builder
@Jacksonized
public class UserProfile {
    private final String username;
    private final String ranking;
    private final String userAvatar;
    private final String realName;
    private final String aboutMe;
}
