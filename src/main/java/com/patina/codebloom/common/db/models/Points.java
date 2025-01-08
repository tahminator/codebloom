package com.patina.codebloom.common.db.models;

import java.time.LocalDateTime;

public class Points {
    private String id;
    private String userId;
    private int totalScore;

    private LocalDateTime createdAt;

    public Points(String id, String userId, int totalScore, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.totalScore = totalScore;
        this.createdAt = createdAt;
    }

    public Points(String id, String userId, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.createdAt = createdAt;
    };

    public Points(String userId, LocalDateTime createdAt) {
        this.userId = userId;
        this.createdAt = createdAt;
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}