package com.patina.codebloom.api.submission.body;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class LeetcodeUsernameObject {
    @NotNull
    @NotBlank
    private String leetcodeUsername;

    public LeetcodeUsernameObject(String leetcodeUsername) {
        this.leetcodeUsername = leetcodeUsername;
    }

    public String getLeetcodeUsername() {
        return leetcodeUsername;
    }

    public void setLeetcodeUsername(String leetcodeUsername) {
        this.leetcodeUsername = leetcodeUsername;
    }

}
