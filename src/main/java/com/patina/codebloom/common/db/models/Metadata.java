package com.patina.codebloom.common.db.models;

import java.time.LocalDateTime;

public class Metadata {
    private String id;
    private String userId;
    private String leaderboardId;
    private LocalDateTime createdAt;
    private int totalScore;

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(final String userId) {
        this.userId = userId;
    }

    public String getLeaderboardId() {
        return leaderboardId;
    }

    public void setLeaderboardId(final String leaderboardId) {
        this.leaderboardId = leaderboardId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(final LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(final int totalScore) {
        this.totalScore = totalScore;
    }

}
