package com.patina.codebloom.common.db.models.leaderboard;

import java.util.ArrayList;

import com.patina.codebloom.common.db.models.user.UserWithScore;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

@Getter
@Setter
@Jacksonized
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class LeaderboardWithUsers extends Leaderboard {
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private ArrayList<UserWithScore> users;

    // public LeaderboardWithUsers(final String name, final LocalDateTime createdAt,
    // final ArrayList<UserWithScore> users, final LocalDateTime shouldExpireBy) {
    // super(name, createdAt, shouldExpireBy);
    // this.users = users;
    // }
    //
    // public LeaderboardWithUsers(final String id, final String name, final
    // LocalDateTime createdAt, final LocalDateTime deletedAt,
    // final ArrayList<UserWithScore> users, final LocalDateTime shouldExpireBy) {
    // super(id, name, createdAt, deletedAt, shouldExpireBy);
    // this.users = users;
    // }

    public void addUser(final UserWithScore user) {
        this.users.add(user);
    }
}
