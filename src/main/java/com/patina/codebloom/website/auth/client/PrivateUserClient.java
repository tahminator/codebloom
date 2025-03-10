package com.patina.codebloom.website.auth.client;

import org.springframework.stereotype.Component;

import com.patina.codebloom.website.auth.model.PrivateUser;
import com.patina.codebloom.website.auth.repo.UserRepository;

/**
 * Use this repository if you need to access the private
 * user and gain access to information that is not normally
 * exposed.
 */
@Component
public class PrivateUserClient {
    private final UserRepository userRepository;

    public PrivateUserClient(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public PrivateUser getPrivateUserById(final String userId) {
        return userRepository._getPrivateUserById(userId);
    }
}
