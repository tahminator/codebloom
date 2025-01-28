package com.patina.codebloom.common.db.models.auth;

import java.time.LocalDateTime;

public class Auth {
    private String id;
    private String token;
    private LocalDateTime createdAt;

    public Auth(String id, String token, LocalDateTime createdAt) {
        this.id = id;
        this.token = token;
        this.createdAt = createdAt;
    }

    public Auth(String token) {
        this.token = token;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
