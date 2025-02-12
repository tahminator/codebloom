package com.patina.codebloom.common.db.models.leaderboard;

import java.time.LocalDateTime;

public class Leaderboard {
    private String id;
    private String name;
    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;

    public Leaderboard(final String name, final LocalDateTime createdAt) {
        this.name = name;
        this.createdAt = createdAt;
    }

    public Leaderboard(final String id, final String name, final LocalDateTime createdAt, final LocalDateTime deletedAt) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
        this.deletedAt = deletedAt;
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

}
