package com.patina.codebloom.website.leetcode.models;

import java.time.LocalDateTime;

public class Auth {
    private String id;
    private String token;
    private LocalDateTime createdAt;

    public Auth(final String id, final String token, final LocalDateTime createdAt) {
        this.id = id;
        this.token = token;
        this.createdAt = createdAt;
    }

    public Auth(final String token) {
        this.token = token;
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(final String token) {
        this.token = token;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(final LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
