package com.patina.codebloom.common.db.models;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class Session {
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String id;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String userId;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime expiresAt;

    // public Session(final String userId, final LocalDateTime expiresAt) {
    // this.userId = userId;
    // this.expiresAt = expiresAt;
    // }
}
