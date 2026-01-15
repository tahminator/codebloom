package org.patinanetwork.codebloom.common.dto.session;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;
import org.patinanetwork.codebloom.common.db.models.Session;

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
