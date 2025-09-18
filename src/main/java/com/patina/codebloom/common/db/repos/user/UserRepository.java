package com.patina.codebloom.common.db.repos.user;

import java.util.ArrayList;

import com.patina.codebloom.common.db.models.user.PrivateUser;
import com.patina.codebloom.common.db.models.user.User;
import com.patina.codebloom.common.db.models.user.UserWithScore;

public interface UserRepository {
    User createNewUser(User user);

    User getUserById(String id);

    User getUserByLeetcodeUsername(String leetcodeUsername);

    UserWithScore getUserWithScoreById(String userId, String leaderboardId);

    User getUserByDiscordId(String discordId);

    int getUserCount();

    int getUserCount(String query);

    User updateUser(User user);

    ArrayList<User> getAllUsers();

    ArrayList<User> getAllUsers(int page, int pageSize, String query);

    PrivateUser getPrivateUserById(String id);

    boolean userExistsByLeetcodeUsername(String leetcodeUsername);

    boolean deleteUserById(String id);

    UserWithScore getUserWithScoreByLeetcodeUsername(String userLeetcodeUsername, String leaderboardId);
}
