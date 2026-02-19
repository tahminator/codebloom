package org.patinanetwork.codebloom.common.dto.refresh;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

@Getter
@Builder
@Jacksonized
@ToString
@EqualsAndHashCode
public class RefreshResultDto {

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private int score;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private int globalRank;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private int clubRank;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String leaderboardName;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String clubName;
}
