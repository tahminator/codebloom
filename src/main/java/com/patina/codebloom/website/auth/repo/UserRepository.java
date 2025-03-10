package com.patina.codebloom.website.auth.repo;

import java.util.ArrayList;

import com.patina.codebloom.website.auth.model.PrivateUser;
import com.patina.codebloom.website.auth.model.User;

public interface UserRepository {
    User createNewUser(User user);

    User getUserById(String id);

    User getUserByDiscordId(String discordId);

    int getUserCount();

    User updateUser(User user);

    ArrayList<User> getAllUsers();

    /**
     * This should not be exposed directly. Call this method
     * inside of a client object that can then be exposed to
     * call this function.
     *
     * @code @deprecated This is not deprecated; it is used to
     * warn you to not use this code across boundaries. Instead,
     * use PrivateUserClient.
     */
    @Deprecated
    PrivateUser _getPrivateUserById(String id);

    boolean userExistsByLeetcodeUsername(String leetcodeUsername);
}
