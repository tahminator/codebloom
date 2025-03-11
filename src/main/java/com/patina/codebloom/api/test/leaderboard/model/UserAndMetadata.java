package com.patina.codebloom.api.test.leaderboard.model;

import java.util.ArrayList;

import com.patina.codebloom.common.db.models.user.User;

public class UserAndMetadata {
    private String id;
    private String name;
    private String createdAt;
    private String deletedAt;
    private ArrayList<User> users;

    public UserAndMetadata(final String id, final String name, final String createdAt, final String deletedAt, final ArrayList<User> users) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
        this.deletedAt = deletedAt;
        this.users = users;
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(final String createdAt) {
        this.createdAt = createdAt;
    }

    public String getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(final String deletedAt) {
        this.deletedAt = deletedAt;
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public void setUsers(final ArrayList<User> users) {
        this.users = users;
    }

}
