package com.patina.codebloom.common.dto.achievement;

import com.patina.codebloom.common.db.models.achievements.Achievement;
import com.patina.codebloom.common.db.models.achievements.AchievementPlaceEnum;
import com.patina.codebloom.common.db.models.usertag.Tag;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

@Builder
@Getter
@Jacksonized
@ToString
@EqualsAndHashCode
public class AchievementDto {

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String id;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String userId;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private AchievementPlaceEnum place;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, nullable = true)
    private Tag leaderboard;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
    private String description;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean isActive;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private OffsetDateTime createdAt;

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
    private OffsetDateTime deletedAt;

    public static AchievementDto fromAchievement(final Achievement achievement) {
        return AchievementDto.builder()
                .id(achievement.getId())
                .userId(achievement.getUserId())
                .leaderboard(achievement.getLeaderboard())
                .place(achievement.getPlace())
                .title(achievement.getTitle())
                .description(achievement.getDescription())
                .isActive(achievement.isActive())
                .createdAt(achievement.getCreatedAt())
                .deletedAt(achievement.getDeletedAt())
                .build();
    }
}
