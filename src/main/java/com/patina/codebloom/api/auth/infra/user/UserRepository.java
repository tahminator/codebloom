package com.patina.codebloom.api.auth.infra.user;

import com.patina.codebloom.common.db.models.User;

public interface UserRepository {
    User createNewUser(User user);

    User getUserById(String id);

    User getUserByDiscordId(String discordId);

    int getUserCount();

    User updateUserById(User user);
}
