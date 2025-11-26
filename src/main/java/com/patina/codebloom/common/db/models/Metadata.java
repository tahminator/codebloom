package com.patina.codebloom.common.db.models;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

public class Metadata {

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String id;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String userId;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String leaderboardId;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createdAt;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
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
