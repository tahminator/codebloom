package com.patina.codebloom.common.db.models.leaderboard;

import java.time.LocalDateTime;
import java.util.ArrayList;

import com.patina.codebloom.common.db.models.user.UserWithScore;

public class LeaderboardWithUsers extends Leaderboard {
    private ArrayList<UserWithScore> users;

    public LeaderboardWithUsers(String name, LocalDateTime createdAt, ArrayList<UserWithScore> users) {
        super(name, createdAt);
        this.users = users;
    }

    public LeaderboardWithUsers(String id, String name, LocalDateTime createdAt, LocalDateTime deletedAt,
            ArrayList<UserWithScore> users) {
        super(id, name, createdAt, deletedAt);
        this.users = users;
    }

    public ArrayList<UserWithScore> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<UserWithScore> users) {
        this.users = users;
    }

    public void addUser(UserWithScore user) {
        this.users.add(user);
    }
}
