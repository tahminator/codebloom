package com.patina.codebloom.common.test.models;

public class UserAndMetadata {
    private String discordName;
    private String leetcodeUsername;
    private int totalScore;

    public UserAndMetadata(String discordName, String leetcodeUsername, int totalScore) {
        this.discordName = discordName;
        this.leetcodeUsername = leetcodeUsername;
        this.totalScore = totalScore;
    }

    public String getDiscordName() {
        return discordName;
    }

    public void setDiscordName(String discordName) {
        this.discordName = discordName;
    }

    public String getLeetcodeUsername() {
        return leetcodeUsername;
    }

    public void setLeetcodeUsername(String leetcodeUsername) {
        this.leetcodeUsername = leetcodeUsername;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }
}
