package com.patina.codebloom.common.db.models.announcement;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Builder
@Getter
@ToString
@EqualsAndHashCode
public class Announcement {
    // @Setter on some of the properties
    // so we can override id property when new object
    // created in database.
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private @Setter String id;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private @Setter LocalDateTime createdAt;

    @NotNull
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime expiresAt;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, nullable = true)
    private boolean showTimer;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String message;
}
