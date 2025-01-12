package com.patina.codebloom.common.db.models;

public class User {
    private String id;

    // Even though discord IDs are integers, they are very large so we just use
    // String instead.
    private String discordId;
    private String discordName;
    private String leetcodeUsername;

    public User(String id, String discordId, String discordName, String leetcodeUsername) {
        this.id = id;
        this.discordId = discordId;
        this.discordName = discordName;
        this.leetcodeUsername = leetcodeUsername;
    }

    public User(String discordId, String discordName, String leetcodeUsername) {
        this.discordId = discordId;
        this.discordName = discordName;
        this.leetcodeUsername = leetcodeUsername;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDiscordId() {
        return discordId;
    }

    public void setDiscordId(String discordId) {
        this.discordId = discordId;
    }

    public String getDiscordName() {
        return discordName;
    }

    public void setDiscordName(String discordName) {
        this.discordName = discordName;
    }

    public String getLeetcodeUsername() {
        return leetcodeUsername;
    }

    public void setLeetcodeUsername(String leetcodeUsername) {
        this.leetcodeUsername = leetcodeUsername;
    }
}
