package com.patina.codebloom.common.dto.session;

import com.patina.codebloom.common.db.models.Session;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

@Getter
@Builder
@Jacksonized
@EqualsAndHashCode
@ToString
public class SessionDto {

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String id;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String userId;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime expiresAt;

    public static SessionDto fromSession(final Session session) {
        return SessionDto.builder()
                .id(session.getId())
                .userId(session.getUserId())
                .expiresAt(session.getExpiresAt())
                .build();
    }
}
