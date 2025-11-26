package com.patina.codebloom.common.dto.leaderboard;

import com.patina.codebloom.common.db.models.leaderboard.Leaderboard;
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
@ToString
@EqualsAndHashCode
public class LeaderboardDto {

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String id;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createdAt;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, nullable = true)
    private LocalDateTime deletedAt;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, nullable = true)
    private LocalDateTime shouldExpireBy;

    public static LeaderboardDto fromLeaderboard(
        final Leaderboard leaderboard
    ) {
        return LeaderboardDto.builder()
            .id(leaderboard.getId())
            .name(leaderboard.getName())
            .createdAt(leaderboard.getCreatedAt())
            .deletedAt(leaderboard.getDeletedAt())
            .shouldExpireBy(leaderboard.getShouldExpireBy())
            .build();
    }
}
