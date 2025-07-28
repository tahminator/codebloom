package com.patina.codebloom.common.security;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import com.patina.codebloom.common.db.models.Session;
import com.patina.codebloom.common.db.models.user.PrivateUser;
import com.patina.codebloom.common.db.repos.session.SessionRepository;
import com.patina.codebloom.common.db.repos.user.UserRepository;
import com.patina.codebloom.common.leetcode.LeetcodeApiHandler;
import com.patina.codebloom.common.leetcode.models.UserProfile;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Protector is used to validate whether or not the user is logged in or not.
 * 
 * @see <a href=
 * "https://github.com/0pengu/codebloom/tree/main/docs/auth.md">Authentication
 * Documentation</a>
 */
@Component
public class Protector {

    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final LeetcodeApiHandler leetcodeApiHandler;

    public Protector(final SessionRepository sessionRepository,
                    final UserRepository userRepository,
                    final LeetcodeApiHandler leetcodeApiHandler) {
        this.sessionRepository = sessionRepository;
        this.userRepository = userRepository;
        this.leetcodeApiHandler = leetcodeApiHandler;
    }

    public AuthenticationObject validateSession(final HttpServletRequest request) {
        if (request.getCookies() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        for (Cookie cookie : request.getCookies()) {
            if ("session_token".equals(cookie.getName()) && !cookie.getValue().isEmpty()) {
                String sessionToken = cookie.getValue();

                Session session = sessionRepository.getSessionById(sessionToken);

                if (session == null) {
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
                }

                PrivateUser user = userRepository.getPrivateUserById(session.getUserId());
                if (user == null) {
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
                }
                if (user.getLeetcodeUsername() != null) {
                    UserProfile leetcodeUserProfile = leetcodeApiHandler.getUserProfile(user.getLeetcodeUsername());
                    user.setProfileUrl(leetcodeUserProfile.getUserAvatar());
                }
                return new AuthenticationObject(user, session);
            }
        }

        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
    }

    public AuthenticationObject validateAdminSession(final HttpServletRequest request) {
        AuthenticationObject admin = validateSession(request);
        if (!admin.getUser().isAdmin()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        return admin;
    }
}
