package com.patina.codebloom.website.auth.client;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import com.patina.codebloom.website.auth.model.Session;
import com.patina.codebloom.website.auth.model.User;
import com.patina.codebloom.website.auth.repo.SessionRepository;
import com.patina.codebloom.website.auth.repo.UserRepository;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Protector is used to validate whether or not the user is
 * logged in or not.
 * 
 * @see <a href=
 * "https://github.com/0pengu/codebloom/tree/main/docs/backend/auth.md">Authentication
 * Documentation</a>
 */
@Component
public class Protector {

    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;

    public Protector(final SessionRepository sessionRepository, final UserRepository userRepository) {
        this.sessionRepository = sessionRepository;
        this.userRepository = userRepository;
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

                User user = userRepository.getUserById(session.getUserId());
                /**
                 * This shouldn't be happening. TODO - Decide if this should
                 * throw a runtime exception instead like in the
                 * /auth/validate endpoint.
                 */
                if (user == null) {
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
                }
                return new AuthenticationObject(user, session);
            }
        }

        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
    }
}
