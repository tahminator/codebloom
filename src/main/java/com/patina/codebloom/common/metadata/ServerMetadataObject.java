package com.patina.codebloom.common.metadata;

import java.util.ArrayList;
import java.util.Arrays;

public class ServerMetadataObject {
    private String name;
    private String version;
    private String description;
    private ArrayList<String> authors;

    public ServerMetadataObject() {
        this.name = "Codebloom";
        this.version = "v1.0.0";
        this.description = "LeetCode leaderboard for Patina Network members to track progress and motivate each other.";
        this.authors = new ArrayList<>(Arrays.asList("Alisha Zaman", "Alfardil Alam", "Michael Nunez", "Tahmid Ahmed"));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<String> getAuthors() {
        return authors;
    }

    public void setAuthors(ArrayList<String> authors) {
        this.authors = authors;
    }

}
