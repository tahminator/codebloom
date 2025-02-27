package com.patina.codebloom.common.db.models.user;

/**
 * This should only ever be used to do a verification on a user's Leetcode profile. Do NOT leak the key.
 */
public class PrivateUser extends User {

    private final String verifyKey;

    public PrivateUser(final String id, final String discordId, final String discordName, final String leetcodeUsername, final String nickname, final String verifyKey) {
        super(id, discordId, discordName, leetcodeUsername, nickname);
        this.verifyKey = verifyKey;
    }

    public String getVerifyKey() {
        return verifyKey;
    }
}
