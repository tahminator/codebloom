package com.patina.codebloom.common.db.models.user;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.patina.codebloom.common.db.models.usertag.UserTag;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
public class User {
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String id;

    // Even though discord IDs are integers, they are very large so we just use
    // String instead.
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

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String profileUrl;

    /**
     * If you want to update tags in the database, you have to use the
     * {@link com.patina.codebloom.common.db.repos.usertag.UserTagRepository}
     */
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private ArrayList<UserTag> tags;

    @JsonCreator
    public User(
                    final String id,
                    final String discordId,
                    final String discordName,
                    final String leetcodeUsername,
                    final String nickname,
                    final boolean admin,
                    final String profileUrl,
                    final ArrayList<UserTag> tags) {
        this.id = id;
        this.discordId = discordId;
        this.discordName = discordName;
        this.leetcodeUsername = leetcodeUsername;
        this.nickname = nickname;
        this.admin = admin;
        this.profileUrl = profileUrl;
        this.tags = tags;
    }

    /**
     * A new user should not have any tags to begin with.
     */
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

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(final Boolean admin) {
        this.admin = admin;
    }

    public void setProfileUrl(final String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public ArrayList<UserTag> getTags() {
        return tags;
    }

    /**
     * This operation is permitted, but the tag will not be used in update
     * operations in the UserRepository. Instead call this method with the parameter
     * being the add method from
     * {@link com.patina.codebloom.common.db.repos.usertag.UserTagRepository}
     *
     * Essentially, this operation should be used to keep the User model up-to-date
     * with any Tag operations without needlessly querying the database for the full
     * User object.
     */
    public void addTag(final UserTag tag) {
        if (tag == null) {
            return;
        }

        tags.add(tag);
    }
}
