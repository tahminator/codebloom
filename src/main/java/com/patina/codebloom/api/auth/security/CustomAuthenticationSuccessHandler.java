package com.patina.codebloom.api.auth.security;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.patina.codebloom.common.db.models.Session;
import com.patina.codebloom.common.db.models.leaderboard.Leaderboard;
import com.patina.codebloom.common.db.models.user.User;
import com.patina.codebloom.common.db.repos.leaderboard.LeaderboardRepository;
import com.patina.codebloom.common.db.repos.session.SessionRepository;
import com.patina.codebloom.common.db.repos.user.UserRepository;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Handles successful OAuth2 login by either creating a new user or referencing an old user and generating a cookie storing the session ID.
 * 
 * @see <a href= "https://github.com/0pengu/codebloom/tree/main/docs/auth.md">Authentication Documentation</a>
 */
@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    // 30 days expiration
    private final int maxAgeSeconds = 60 * 60 * 24 * 30;

    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final LeaderboardRepository leaderboardRepository;

    public CustomAuthenticationSuccessHandler(final UserRepository userRepository, final SessionRepository sessionRepository, final LeaderboardRepository leaderboardRepository) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.leaderboardRepository = leaderboardRepository;
    }

    @Override
    public void onAuthenticationSuccess(final HttpServletRequest request, final HttpServletResponse response, final Authentication authentication) throws IOException, ServletException {
        Object principal = authentication.getPrincipal();
        String discordId = null;
        String discordName = null;

        if (principal instanceof OAuth2User) {
            OAuth2User oAuth2User = (OAuth2User) principal;
            Map<String, Object> attributes = oAuth2User.getAttributes();
            discordId = attributes.get("id").toString();
            discordName = attributes.get("username").toString();

            User existingUser = userRepository.getUserByDiscordId(discordId);

            if (existingUser != null) {
                existingUser.setDiscordName(discordName);
                userRepository.updateUser(existingUser);
            } else {
                User newUser = new User(discordId, discordName);
                existingUser = userRepository.createNewUser(newUser);
                Leaderboard leaderboard = leaderboardRepository.getRecentLeaderboardShallow();
                leaderboardRepository.addUserToLeaderboard(existingUser.getId(), leaderboard.getId());
            }

            LocalDateTime expirationTime = LocalDateTime.now().plusSeconds(maxAgeSeconds);

            Session session = new Session(existingUser.getId(), expirationTime);
            session = sessionRepository.createSession(session);

            if (session == null) {
                response.sendRedirect("/login?success=false&message=Failed to log in.");
                throw new RuntimeException("Failed to create new session.");
            }

            Cookie cookie = new Cookie("session_token", session.getId());
            cookie.setMaxAge(maxAgeSeconds);

            cookie.setHttpOnly(true);
            cookie.setSecure(true);
            cookie.setPath("/");

            response.addCookie(cookie);
            response.sendRedirect("/dashboard");
        } else {
            response.sendRedirect("/login?success=false&message=Failed to log in.");
            throw new UsernameNotFoundException("User not found");
        }
    }
}
