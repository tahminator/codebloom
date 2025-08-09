package com.patina.codebloom.common.db.models.achievements;

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
@ToString
@EqualsAndHashCode
public class Achievement {
    
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private @Setter String id;

    @NotNull
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String userId;

    @NotNull
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String iconUrl;

    @NotNull
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
    private String description;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean isActive;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private @Setter OffsetDateTime createdAt;

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
    private OffsetDateTime deletedAt;
}