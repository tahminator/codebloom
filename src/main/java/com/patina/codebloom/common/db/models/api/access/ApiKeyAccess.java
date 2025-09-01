package com.patina.codebloom.common.db.models.api.access;

import java.util.Set;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Builder
@Getter
@ToString
@EqualsAndHashCode
public class ApiKeyAccess {
    // @Setter on some of the properties
    // so we can override id property when new object
    // created in database.
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private @Setter String id;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private @Setter String apiKeyId;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private @Setter Set<String> access;
}
