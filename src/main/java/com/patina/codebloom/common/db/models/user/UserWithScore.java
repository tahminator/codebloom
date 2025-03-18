package com.patina.codebloom.common.db.models.user;

import java.util.ArrayList;

import com.patina.codebloom.common.db.models.usertag.UserTag;

public class UserWithScore extends User {
    private int totalScore;

    public UserWithScore(final String id, final String discordId, final String discordName, final String leetcodeUsername, final String nickname, final int totalScore, final ArrayList<UserTag> tags) {
        super(id, discordId, discordName, leetcodeUsername, nickname, tags);
        this.totalScore = totalScore;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(final int totalScore) {
        this.totalScore = totalScore;
    }
}
