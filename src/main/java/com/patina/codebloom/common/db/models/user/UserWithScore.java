package com.patina.codebloom.common.db.models.user;

import java.util.ArrayList;

import com.patina.codebloom.common.db.models.usertag.UserTag;

import io.swagger.v3.oas.annotations.media.Schema;

public class UserWithScore extends User {
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private int totalScore;

    public UserWithScore(final String id, final String discordId, final String discordName, final String leetcodeUsername, final String nickname, final Boolean admin, final String profileUrl,
                    final int totalScore,
                    final ArrayList<UserTag> tags) {
        super(id, discordId, discordName, leetcodeUsername, nickname, admin, profileUrl, tags);
        this.totalScore = totalScore;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(final int totalScore) {
        this.totalScore = totalScore;
    }
}
