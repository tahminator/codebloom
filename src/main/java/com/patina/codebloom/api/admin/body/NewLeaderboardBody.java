package com.patina.codebloom.api.admin.body;

import jakarta.validation.constraints.NotBlank;

public class NewLeaderboardBody {
    @NotBlank
    private String name;

    public NewLeaderboardBody(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }
}
