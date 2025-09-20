package com.patina.codebloom.common.db.repos.user.v2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.patina.codebloom.common.db.DbConnection;
import com.patina.codebloom.common.db.models.user.User;
import com.patina.codebloom.common.db.models.user.UserWithScore;
import com.patina.codebloom.common.db.repos.usertag.UserTagRepository;

@Component
public class UserV2SqlRepository implements UserV2Repository {
    private Connection conn;
    private final UserTagRepository userTagRepository;

    public UserV2SqlRepository(final DbConnection dbConnection, final UserTagRepository userTagRepository) {
        this.conn = dbConnection.getConn();
        this.userTagRepository = userTagRepository;
    }

    @Override
    public User getUserByLeetcodeUsername(final String inputLeetcodeUsername) {
        User user = null;
        String sql = """
                            SELECT id, "discordId", "discordName", "leetcodeUsername", "nickname", admin, "profileUrl"
                            FROM "User"
                            WHERE "leetcodeUsername" = ?
                        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, inputLeetcodeUsername);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    var id = rs.getString("id");
                    var discordId = rs.getString("discordId");
                    var discordName = rs.getString("discordName");
                    var leetcodeUsername = rs.getString("leetcodeUsername");
                    var nickname = rs.getString("nickname");
                    var admin = rs.getBoolean("admin");
                    var profileUrl = rs.getString("profileUrl");
                    var tags = userTagRepository.findTagsByUserId(id);

                    user = new User(id, discordId, discordName, leetcodeUsername, nickname, admin, profileUrl, tags);
                    return user;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while retrieving user", e);
        }

        return user;
    }

    @Override
    public UserWithScore getUserWithScoreByLeetcodeUsername(final String userLeetcodeUsername, final String leaderboardId) {
        String sql = """
                            SELECT
                                u.id,
                                u."discordId",
                                u."discordName",
                                u."leetcodeUsername",
                                u.nickname,
                                u.admin,
                                u."profileUrl",
                                m."totalScore"
                            FROM
                                "User" u
                            JOIN "Metadata" m ON m."userLeetcodeUsername" = u.leetcodeUsername
                            WHERE
                                u."leetcodeUsername" = ?
                                AND
                                m."leaderboardId" = ?
                        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, userLeetcodeUsername);
            stmt.setObject(2, UUID.fromString(leaderboardId));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    var id = rs.getString("id");
                    var discordId = rs.getString("discordId");
                    var discordName = rs.getString("discordName");
                    var leetcodeUsername = rs.getString("leetcodeUsername");
                    var nickname = rs.getString("nickname");
                    var admin = rs.getBoolean("admin");
                    var profileUrl = rs.getString("profileUrl");
                    var totalScore = rs.getInt("totalScore");

                    var tags = userTagRepository.findTagsByUserId(id);

                    var userWithScore = new UserWithScore(id, discordId, discordName, leetcodeUsername, nickname, admin, profileUrl, totalScore, tags);
                    return userWithScore;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get user with score by leetcode username", e);
        }

        return null;
    }
}
