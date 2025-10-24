package com.patina.codebloom.common.db.models.leaderboard;

import java.util.ArrayList;

import com.patina.codebloom.common.db.models.user.UserWithScore;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import com.patina.codebloom.common.db.helper.annotations.NotNullColumn;

@Getter
@Setter
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class LeaderboardWithUsers extends Leaderboard {
    @NotNullColumn
    private ArrayList<UserWithScore> users;

    public void addUser(final UserWithScore user) {
        this.users.add(user);
    }
}
