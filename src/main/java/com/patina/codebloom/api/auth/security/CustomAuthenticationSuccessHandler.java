package com.patina.codebloom.api.auth.security;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.patina.codebloom.common.db.models.Session;
import com.patina.codebloom.common.db.models.leaderboard.Leaderboard;
import com.patina.codebloom.common.db.models.user.User;
import com.patina.codebloom.common.db.models.usertag.Tag;
import com.patina.codebloom.common.db.models.usertag.UserTag;
import com.patina.codebloom.common.db.repos.leaderboard.LeaderboardRepository;
import com.patina.codebloom.common.db.repos.session.SessionRepository;
import com.patina.codebloom.common.db.repos.user.UserRepository;
import com.patina.codebloom.common.db.repos.usertag.UserTagRepository;
import com.patina.codebloom.common.leetcode.LeetcodeClient;
import com.patina.codebloom.common.leetcode.models.UserProfile;
import com.patina.codebloom.common.leetcode.throttled.ThrottledLeetcodeClient;
import com.patina.codebloom.common.time.StandardizedLocalDateTime;
import com.patina.codebloom.jda.client.JDAClient;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

/**
 * Handles successful OAuth2 login by either creating a new user or referencing
 * an old user and generating a cookie storing the session ID.
 * 
 * @see <a href=
 * "https://github.com/tahminator/codebloom/tree/main/docs/auth.md">Authentication
 * Documentation</a>
 */
@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    // 30 days expiration
    private final int maxAgeSeconds = 60 * 60 * 24 * 30;

    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final LeaderboardRepository leaderboardRepository;
    private final UserTagRepository userTagRepository;
    private final JDAClient jdaClient;
    private final LeetcodeClient leetcodeClient;

    public CustomAuthenticationSuccessHandler(final UserRepository userRepository, final SessionRepository sessionRepository,
                    final LeaderboardRepository leaderboardRepository,
                    final JDAClient jdaClient,
                    final UserTagRepository userTagRepository, final ThrottledLeetcodeClient throttledLeetcodeClient) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.leaderboardRepository = leaderboardRepository;
        this.jdaClient = jdaClient.connect();
        this.userTagRepository = userTagRepository;
        this.leetcodeClient = throttledLeetcodeClient;
    }

    @Override
    public void onAuthenticationSuccess(final HttpServletRequest request, final HttpServletResponse response,
                    final Authentication authentication) throws IOException, ServletException {
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
                if (existingUser.getLeetcodeUsername() != null) {
                    UserProfile leetcodeUserProfile = leetcodeClient.getUserProfile(existingUser.getLeetcodeUsername());
                    existingUser.setProfileUrl(leetcodeUserProfile.getUserAvatar());
                }
                userRepository.updateUser(existingUser);
            } else {
                User newUser = User.builder()
                                .discordId(discordId)
                                .discordName(discordName)
                                .build();
                userRepository.createUser(newUser);
                Leaderboard leaderboard = leaderboardRepository.getRecentLeaderboardMetadata();
                leaderboardRepository.addUserToLeaderboard(newUser.getId(), leaderboard.getId());
                existingUser = newUser;
            }

            List<Guild> guilds = jdaClient.getGuilds();
            String patinaGuildId = String.valueOf(jdaClient.getJdaPatinaProperties().getGuildId());

            Guild foundGuild = null;
            for (Guild g : guilds) {
                // System.out.println(g.getId() + "=" +
                // jdaInitializer.getJdaProperties().getId());
                if (g.getId().equals(patinaGuildId)) {
                    foundGuild = g;
                    break;
                }
            }

            if (foundGuild != null) {
                List<Member> members = foundGuild.getMembers();

                for (Member m : members) {
                    // System.out.println(m.getId() + "=" + existingUser.getDiscordId());
                    // System.out.println(m.getNickname() + "&" + m.getUser().getName());
                    if (m.getId().equals(existingUser.getDiscordId())) {
                        if (m.getNickname() != null) {
                            existingUser.setNickname(m.getNickname());
                        } else if (m.getUser().getGlobalName() != null) {
                            existingUser.setNickname(m.getUser().getGlobalName());
                        } else {
                            existingUser.setNickname(existingUser.getDiscordName());
                        }
                        if (userTagRepository.findTagByUserIdAndTag(existingUser.getId(), Tag.Patina) == null) {
                            userTagRepository.createTag(UserTag.builder()
                                            .userId(existingUser.getId())
                                            .tag(Tag.Patina)
                                            .build());
                        }
                        userRepository.updateUser(existingUser);
                    }
                }
            }

            LocalDateTime expirationTime = StandardizedLocalDateTime.now().plusSeconds(maxAgeSeconds);

            Session session = Session.builder()
                            .userId(existingUser.getId())
                            .expiresAt(expirationTime)
                            .build();
            sessionRepository.createSession(session);

            if (session == null || session.getId() == null) {
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
