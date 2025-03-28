package com.patina.codebloom.common.db.repos.user;

import java.util.ArrayList;

import com.patina.codebloom.common.db.models.user.PrivateUser;
import com.patina.codebloom.common.db.models.user.User;
import com.patina.codebloom.common.db.models.user.UserWithScore;

public interface UserRepository {
    User createNewUser(User user);

    User getUserById(String id);

    UserWithScore getUserWithScoreById(String userId, String leaderboardId);

    User getUserByDiscordId(String discordId);

    int getUserCount();

    User updateUser(User user);

    ArrayList<User> getAllUsers();

    PrivateUser getPrivateUserById(String id);

    boolean userExistsByLeetcodeUsername(String leetcodeUsername);
}
