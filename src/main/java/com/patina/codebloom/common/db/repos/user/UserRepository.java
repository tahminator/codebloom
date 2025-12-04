package com.patina.codebloom.common.db.repos.user;

import com.patina.codebloom.common.db.models.user.User;
import com.patina.codebloom.common.db.models.user.UserWithScore;
import com.patina.codebloom.common.db.repos.user.options.UserFilterOptions;
import java.util.ArrayList;

public interface UserRepository {
    /**
     * @note - The provided object's methods will be overridden with any returned data from the database.
     * @param user - required fields:
     *     <ul>
     *       <li>discordId
     *       <li>discordName
     *     </ul>
     */
    void createUser(User user);

    /**
     * @note - The provided object's methods will be overridden with any returned data from the database.
     * @param user - overridden fields:
     *     <ul>
     *       <li>discordName
     *       <li>discordId
     *       <li>leetcodeUsername
     *       <li>nickname
     *       <li>admin
     *       <li>profileUrl
     *       <li>schoolEmail
     *     </ul>
     */
    boolean updateUser(User user);

    User getUserById(String id);

    User getUserByLeetcodeUsername(String leetcodeUsername);

    UserWithScore getUserWithScoreByIdAndLeaderboardId(String userId, String leaderboardId, UserFilterOptions options);

    UserWithScore getUserWithScoreByLeetcodeUsernameAndLeaderboardId(String userLeetcodeUsername, String leaderboardId);

    User getUserByDiscordId(String discordId);

    int getUserCount();

    int getUserCount(String query);

    ArrayList<User> getAllUsers();

    ArrayList<User> getAllUsers(int page, int pageSize, String query);

    boolean userExistsByLeetcodeUsername(String leetcodeUsername);

    boolean deleteUserById(String id);
}
