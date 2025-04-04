package com.patina.codebloom.common.db.models.user;

import java.util.ArrayList;

import com.patina.codebloom.common.db.models.usertag.UserTag;

/**
 * This should only ever be used to do a verification on a user's Leetcode
 * profile. Do NOT leak the key.
 */
public class PrivateUser extends User {

    private final String verifyKey;

    public PrivateUser(final String id, final String discordId, final String discordName, final String leetcodeUsername, final String nickname, final boolean admin, final String verifyKey,
                    final ArrayList<UserTag> tags) {
        super(id, discordId, discordName, leetcodeUsername, nickname, admin, tags);
        this.verifyKey = verifyKey;
    }

    public String getVerifyKey() {
        return verifyKey;
    }
}
