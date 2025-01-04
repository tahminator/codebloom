package com.patina.codebloom.common.db.models;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Session {
    private String id;
    private String userId;

    private LocalDateTime expiresAt;

    public Session(String id, String userId, LocalDateTime expiresAt) {
        this.id = id;
        this.userId = userId;
        this.expiresAt = expiresAt;
    }

    public Session(String id, String userId, String expiresAt) {
        this.id = id;
        this.userId = userId;
        this.expiresAt = LocalDateTime.parse(expiresAt);
    }

    public Session(String userId, LocalDateTime expiresAt) {
        this.userId = userId;
        this.expiresAt = expiresAt;
    }

    public Session(String userId, String expiresAt) {
        this.userId = userId;
        this.expiresAt = LocalDateTime.parse(expiresAt);
    }
}
