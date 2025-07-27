package com.patina.codebloom.common.db.models.user;

import java.util.ArrayList;

import com.patina.codebloom.common.db.models.usertag.UserTag;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * This should only ever be used to do a verification on a user's Leetcode
 * profile. Do NOT leak the key.
 */
public class PrivateUser extends User {
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private final String verifyKey;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String schoolEmail;

    public PrivateUser(
                    final String id,
                    final String discordId,
                    final String discordName,
                    final String leetcodeUsername,
                    final String nickname,
                    final boolean admin,
                    final String profileUrl,
                    final String schoolEmail,
                    final String verifyKey,
                    final ArrayList<UserTag> tags) {
        super(id, discordId, discordName, leetcodeUsername, nickname, admin, profileUrl, tags);
        this.verifyKey = verifyKey;
        this.schoolEmail = schoolEmail;
    }

    public String getVerifyKey() {
        return verifyKey;
    }

    public String getSchoolEmail() {
        return schoolEmail;
    }

    public void setSchoolEmail(final String schoolEmail) {
        this.schoolEmail = schoolEmail;
    }
}
