package com.patina.codebloom.common.db.models.announcement;

import java.time.OffsetDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Builder
@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Announcement {

    @EqualsAndHashCode.Include
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String id;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private OffsetDateTime createdAt;

    @NotNull
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private OffsetDateTime expiresAt;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, nullable = true)
    private boolean showTimer;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String message;
}
