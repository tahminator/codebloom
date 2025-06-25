package com.patina.codebloom.common.db.models.auth;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class Auth {
    private String id;
    private String token;
    private LocalDateTime createdAt;
}
