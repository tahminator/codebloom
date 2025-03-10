package com.patina.codebloom.website.leaderboard.model;

import com.patina.codebloom.website.auth.model.User;

public class UserWithScore extends User {
    private int totalScore;

    public UserWithScore(final String id, final String discordId, final String discordName, final String leetcodeUsername,
                    final String nickname, final int totalScore) {
        super(id, discordId, discordName, leetcodeUsername, nickname);
        this.totalScore = totalScore;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(final int totalScore) {
        this.totalScore = totalScore;
    }
}
