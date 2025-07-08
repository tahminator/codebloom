package com.patina.codebloom.common.db.models.leaderboard;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

public class Leaderboard {
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String id;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createdAt;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, nullable = true)
    private LocalDateTime deletedAt;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, nullable = true)
    private LocalDateTime shouldExpireBy;

    public Leaderboard(final String name, final LocalDateTime createdAt, final LocalDateTime shouldExpireBy) {
        this.name = name;
        this.createdAt = createdAt;
        this.shouldExpireBy = shouldExpireBy;
    }

    public Leaderboard(final String id, final String name, final LocalDateTime createdAt, final LocalDateTime deletedAt, final LocalDateTime shouldExpireBy) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
        this.deletedAt = deletedAt;
        this.shouldExpireBy = shouldExpireBy;
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(final LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(final LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public LocalDateTime getShouldExpireBy() {
        return shouldExpireBy;
    }

    public void setShouldExpireBy(final LocalDateTime shouldExpireBy) {
        this.shouldExpireBy = shouldExpireBy;
    }
}
