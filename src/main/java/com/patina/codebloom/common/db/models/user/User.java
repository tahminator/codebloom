package com.patina.codebloom.common.db.models.user;

public class User {
    private String id;

    // Even though discord IDs are integers, they are very large so we just use
    // String instead.
    private String discordId;
    private String discordName;
    private String leetcodeUsername;
    private String nickname;

    public User(final String id, final String discordId, final String discordName, final String leetcodeUsername, final String nickname) {
        this.id = id;
        this.discordId = discordId;
        this.discordName = discordName;
        this.leetcodeUsername = leetcodeUsername;
        this.nickname = nickname;
    }

    public User(final String discordId, final String discordName) {
        this.discordId = discordId;
        this.discordName = discordName;
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getDiscordId() {
        return discordId;
    }

    public void setDiscordId(final String discordId) {
        this.discordId = discordId;
    }

    public String getDiscordName() {
        return discordName;
    }

    public void setDiscordName(final String discordName) {
        this.discordName = discordName;
    }

    public String getLeetcodeUsername() {
        return leetcodeUsername;
    }

    public void setLeetcodeUsername(final String leetcodeUsername) {
        this.leetcodeUsername = leetcodeUsername;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(final String nickname) {
        this.nickname = nickname;
    }
}
