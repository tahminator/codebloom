package com.patina.codebloom.common.dto.leaderboard;

import java.time.LocalDateTime;
import java.util.List;

import com.patina.codebloom.common.db.models.leaderboard.LeaderboardWithUsers;
import com.patina.codebloom.common.db.models.user.UserWithScore;

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
public class LeaderboardWithUsersDto {
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
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private List<UserWithScore> users;

    public static LeaderboardWithUsersDto fromLeaderboardWithUsers(final LeaderboardWithUsers leaderboardWithUsers) {
        return LeaderboardWithUsersDto.builder()
                        .id(leaderboardWithUsers.getId())
                        .name(leaderboardWithUsers.getName())
                        .createdAt(leaderboardWithUsers.getCreatedAt())
                        .deletedAt(leaderboardWithUsers.getDeletedAt())
                        .shouldExpireBy(leaderboardWithUsers.getShouldExpireBy())
                        .users(leaderboardWithUsers.getUsers())
                        .build();
    }
}
