package com.patina.codebloom.api.admin.body;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class NewLeaderboardBodyObject {
    @NotNull
    @NotBlank
    private String leaderboardBodyName;

    public NewLeaderboardBodyObject(final String leaderboardBodyName) {
        this.leaderboardBodyName = leaderboardBodyName;
    }

    public String getLeaderboardBodyName() {
        return leaderboardBodyName;
    }

    public void setLeaderboardBodyName(final String leaderboardBodyName) {
        this.leaderboardBodyName = leaderboardBodyName;
    }
}
