package org.patinanetwork.codebloom.common.db.models.api.access;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.patinanetwork.codebloom.common.db.models.api.ApiKeyAccessEnum;

@Builder
@Setter
@Getter
@ToString
@EqualsAndHashCode
public class ApiKeyAccess {

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String id;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String apiKeyId;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private ApiKeyAccessEnum access;
}
