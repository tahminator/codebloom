package com.patina.codebloom.common.db.repos.user;

import com.patina.codebloom.common.db.models.User;

public interface UserRepository {
    User createNewUser(User user);

    User getUserById(String id);

    User getUserByDiscordId(String discordId);

    int getUserCount();

    User updateUserById(User user);
}
