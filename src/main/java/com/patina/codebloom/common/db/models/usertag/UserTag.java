package com.patina.codebloom.common.db.models.usertag;

import java.time.LocalDateTime;

public class UserTag {
    private String id;
    private LocalDateTime createdAt;
    private String userId;
    private Tag tag;

    public UserTag(final String id, final LocalDateTime createdAt, final String userId, final Tag tag) {
        this.id = id;
        this.createdAt = createdAt;
        this.userId = userId;
        this.tag = tag;
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(final LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(final String userId) {
        this.userId = userId;
    }

    public Tag getTag() {
        return tag;
    }

    public void setTag(final Tag tag) {
        this.tag = tag;
    }

}
