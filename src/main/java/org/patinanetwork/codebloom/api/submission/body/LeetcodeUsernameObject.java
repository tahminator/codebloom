package org.patinanetwork.codebloom.api.submission.body;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class LeetcodeUsernameObject {

    @NotNull
    @NotBlank
    private String leetcodeUsername;

    public LeetcodeUsernameObject(final String leetcodeUsername) {
        this.leetcodeUsername = leetcodeUsername;
    }

    public String getLeetcodeUsername() {
        return leetcodeUsername;
    }

    public void setLeetcodeUsername(final String leetcodeUsername) {
        this.leetcodeUsername = leetcodeUsername;
    }
}
