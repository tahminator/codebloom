package com.patina.codebloom.website.leetcode.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class LeetcodeUsernameResponse {
    @NotNull
    @NotBlank
    private String leetcodeUsername;

    public LeetcodeUsernameResponse(final String leetcodeUsername) {
        this.leetcodeUsername = leetcodeUsername;
    }

    public String getLeetcodeUsername() {
        return leetcodeUsername;
    }

    public void setLeetcodeUsername(final String leetcodeUsername) {
        this.leetcodeUsername = leetcodeUsername;
    }

}
