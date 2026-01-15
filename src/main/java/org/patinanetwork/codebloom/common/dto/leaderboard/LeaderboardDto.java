package org.patinanetwork.codebloom.common.dto.leaderboard;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;
import org.patinanetwork.codebloom.common.db.models.leaderboard.Leaderboard;

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

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, nullable = true)
    private String syntaxHighlightingLanguage;

    public static LeaderboardDto fromLeaderboard(final Leaderboard leaderboard) {
        return LeaderboardDto.builder()
                .id(leaderboard.getId())
                .name(leaderboard.getName())
                .createdAt(leaderboard.getCreatedAt())
                .deletedAt(leaderboard.getDeletedAt())
                .shouldExpireBy(leaderboard.getShouldExpireBy())
                .syntaxHighlightingLanguage(leaderboard.getSyntaxHighlightingLanguage())
                .build();
    }
}
