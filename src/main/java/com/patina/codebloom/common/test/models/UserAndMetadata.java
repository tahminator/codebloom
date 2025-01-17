package com.patina.codebloom.common.test.models;

import java.util.ArrayList;

import com.patina.codebloom.common.db.models.user.User;

public class UserAndMetadata {
    private String id;
    private String name;
    private String createdAt;
    private String deletedAt;
    private ArrayList<User> users;

    public UserAndMetadata(String id, String name, String createdAt, String deletedAt, ArrayList<User> users) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
        this.deletedAt = deletedAt;
        this.users = users;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(String deletedAt) {
        this.deletedAt = deletedAt;
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }

}
