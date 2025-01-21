package com.patina.codebloom.api.submission.body;

import groovyjarjarantlr4.v4.runtime.misc.NotNull;
import jakarta.validation.constraints.NotBlank;

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
