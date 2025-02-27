package com.patina.codebloom.common.db.repos.user;

import java.util.ArrayList;

import com.patina.codebloom.common.db.models.user.PrivateUser;
import com.patina.codebloom.common.db.models.user.User;

public interface UserRepository {
    User createNewUser(User user);

    User getUserById(String id);

    User getUserByDiscordId(String discordId);

    int getUserCount();

    User updateUser(User user);

    ArrayList<User> getAllUsers();

    PrivateUser getPrivateUserById(String id);
}
