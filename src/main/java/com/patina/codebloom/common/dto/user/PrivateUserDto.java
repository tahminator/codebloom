package com.patina.codebloom.common.dto.user;

import java.util.List;

import com.patina.codebloom.common.db.models.user.User;
import com.patina.codebloom.common.db.models.usertag.UserTag;
import com.patina.codebloom.common.dto.achievement.AchievementDto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

/**
 * This should only ever be used to do a verification on a user's Leetcode
 * profile. Do NOT leak the key.
 */
@Getter
@Builder
@Jacksonized
@ToString
@EqualsAndHashCode
public class PrivateUserDto {
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String id;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String discordId;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String discordName;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, nullable = true)
    private String leetcodeUsername;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, nullable = true)
    private String nickname;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean admin;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, nullable = true)
    private String profileUrl;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private List<UserTag> tags;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String verifyKey;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, nullable = true)
    private String schoolEmail;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private List<AchievementDto> achievements;

    public static PrivateUserDto fromUser(final User user) {
        return PrivateUserDto.builder()
                        .id(user.getId())
                        .discordId(user.getDiscordId())
                        .discordName(user.getDiscordName())
                        .leetcodeUsername(user.getLeetcodeUsername())
                        .nickname(user.getNickname())
                        .admin(user.isAdmin())
                        .profileUrl(user.getProfileUrl())
                        .tags(user.getTags())
                        .verifyKey(user.getVerifyKey())
                        .schoolEmail(user.getSchoolEmail())
                        .achievements(user.getAchievements().stream().map(AchievementDto::fromAchievement).toList())
                        .build();
    }
}
