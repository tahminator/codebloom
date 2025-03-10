package com.patina.codebloom.website.leaderboard.model;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class LeaderboardWithUsers extends Leaderboard {
    private ArrayList<UserWithScore> users;

    public LeaderboardWithUsers(final String name, final LocalDateTime createdAt, final ArrayList<UserWithScore> users) {
        super(name, createdAt);
        this.users = users;
    }

    public LeaderboardWithUsers(final String id, final String name, final LocalDateTime createdAt, final LocalDateTime deletedAt,
                    final ArrayList<UserWithScore> users) {
        super(id, name, createdAt, deletedAt);
        this.users = users;
    }

    public ArrayList<UserWithScore> getUsers() {
        return users;
    }

    public void setUsers(final ArrayList<UserWithScore> users) {
        this.users = users;
    }

    public void addUser(final UserWithScore user) {
        this.users.add(user);
    }
}
