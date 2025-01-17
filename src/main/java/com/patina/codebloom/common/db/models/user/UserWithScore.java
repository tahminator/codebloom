package com.patina.codebloom.common.db.models.user;

public class UserWithScore extends User {
    private int totalScore;

    public UserWithScore(String id, String discordId, String discordName, String leetcodeUsername, int totalScore) {
        super(id, discordId, discordName, leetcodeUsername);
        this.totalScore = totalScore;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }
}
