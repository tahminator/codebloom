package com.patina.codebloom.common.db.models.api;

import java.time.LocalDateTime;
import java.util.Set;

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
public class ApiKey {
    // @Setter on some of the properties
    // so we can override id property when new object
    // created in database.
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private @Setter String id;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private @Setter String apiKey;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private @Setter Set<String> access;

    @NotNull
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private @Setter LocalDateTime expiresAt;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private @Setter LocalDateTime createdAt;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private @Setter LocalDateTime updatedAt;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private @Setter String updatedBy;
}
