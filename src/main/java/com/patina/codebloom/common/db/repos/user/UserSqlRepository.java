package com.patina.codebloom.common.db.repos.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.patina.codebloom.common.db.DbConnection;
import com.patina.codebloom.common.db.helper.NamedPreparedStatement;
import com.patina.codebloom.common.db.models.user.User;
import com.patina.codebloom.common.db.models.user.UserWithScore;
import com.patina.codebloom.common.db.repos.user.options.UserFilterOptions;
import com.patina.codebloom.common.db.repos.usertag.UserTagRepository;

@Component
public class UserSqlRepository implements UserRepository {
    private Connection conn;
    private final UserTagRepository userTagRepository;

    public UserSqlRepository(final DbConnection dbConnection, final UserTagRepository userTagRepository) {
        this.conn = dbConnection.getConn();
        this.userTagRepository = userTagRepository;
    }

    private User parseResultSetToUser(final ResultSet rs) throws SQLException {
        var id = rs.getString("id");
        return User.builder()
                        .id(id)
                        .discordId(rs.getString("discordId"))
                        .discordName(rs.getString("discordName"))
                        .leetcodeUsername(rs.getString("leetcodeUsername"))
                        .nickname(rs.getString("nickname"))
                        .verifyKey(rs.getString("verifyKey"))
                        .admin(rs.getBoolean("admin"))
                        .schoolEmail(rs.getString("schoolEmail"))
                        .profileUrl(rs.getString("profileUrl"))
                        .tags(userTagRepository.findTagsByUserId(id))
                        .build();
    }

    private UserWithScore parseResultSetToUserWithScore(final ResultSet rs) throws SQLException {
        var id = rs.getString("id");
        return UserWithScore.builder()
                        .id(id)
                        .discordId(rs.getString("discordId"))
                        .discordName(rs.getString("discordName"))
                        .leetcodeUsername(rs.getString("leetcodeUsername"))
                        .nickname(rs.getString("nickname"))
                        .verifyKey(rs.getString("verifyKey"))
                        .admin(rs.getBoolean("admin"))
                        .schoolEmail(rs.getString("schoolEmail"))
                        .profileUrl(rs.getString("profileUrl"))
                        .tags(userTagRepository.findTagsByUserId(id))
                        .totalScore(rs.getInt("totalScore"))
                        .build();
    }

    /**
     * @implNote - You can not set tags on a new user. Create the user, and if the
     * returned user is not null, you can use updateUserTagById from
     * {@link UserTagRepository}
     */
    @Override
    public void createUser(final User user) {
        String sql = """
                        INSERT INTO "User"
                            (id, "discordName", "discordId")
                        VALUES
                            (:id, :discordName, :discordId)
                        RETURNING
                            "verifyKey"
                        """;
        user.setId(UUID.randomUUID().toString());
        try (NamedPreparedStatement stmt = NamedPreparedStatement.create(conn, sql)) {
            stmt.setObject("id", UUID.fromString(user.getId()));
            stmt.setString("discordName", user.getDiscordName());
            stmt.setString("discordId", user.getDiscordId());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    user.setVerifyKey(rs.getString("verifyKey"));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error while creating user", e);
        }
    }

    @Override
    public User getUserById(final String inputId) {
        String sql = """
                        SELECT
                            id,
                            "discordId",
                            "discordName",
                            "leetcodeUsername",
                            "nickname",
                            "schoolEmail",
                            admin,
                            "verifyKey",
                            "profileUrl"
                        FROM "User"
                        WHERE
                            id=:id
                        """;

        try (NamedPreparedStatement stmt = NamedPreparedStatement.create(conn, sql)) {
            stmt.setObject("id", UUID.fromString(inputId));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return parseResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while retrieving user", e);
        }

        return null;
    }

    @Override
    public User getUserByLeetcodeUsername(final String inputLeetcodeUsername) {
        String sql = """
                            SELECT
                                id,
                                "discordId",
                                "discordName",
                                "leetcodeUsername",
                                "nickname",
                                "schoolEmail",
                                admin,
                                "verifyKey",
                                "profileUrl"
                            FROM "User"
                            WHERE "leetcodeUsername" = :leetcodeUsername
                        """;

        try (NamedPreparedStatement stmt = NamedPreparedStatement.create(conn, sql)) {
            stmt.setString("leetcodeUsername", inputLeetcodeUsername);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return parseResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while retrieving user", e);
        }

        return null;
    }

    @Override
    public User getUserByDiscordId(final String inputDiscordId) {
        String sql = """
                        SELECT
                            id,
                            "discordId",
                            "discordName",
                            "leetcodeUsername",
                            "nickname",
                            "schoolEmail",
                            admin,
                            "verifyKey",
                            "profileUrl"
                        FROM "User"
                        WHERE
                            "discordId" = :discordId
                        """;

        try (NamedPreparedStatement stmt = NamedPreparedStatement.create(conn, sql)) {
            stmt.setString("discordId", inputDiscordId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return parseResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while retrieving user", e);
        }

        return null;
    }

    @Override
    public int getUserCount() {
        String sql = "SELECT COUNT(*) FROM \"User\"";

        try (PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while retrieving user count", e);
        }

        return 0;
    }

    @Override
    public boolean updateUser(final User inputUser) {
        String sql = """
                        UPDATE "User"
                        SET
                            "discordName" = :discordName,
                            "discordId" = :discordId,
                            "leetcodeUsername" = :leetcodeUsername,
                            "nickname" = :nickname,
                            "admin" = :admin,
                            "profileUrl"= :profileUrl,
                            "schoolEmail" = :schoolEmail
                        WHERE
                            id = :id
                        """;

        try (NamedPreparedStatement stmt = NamedPreparedStatement.create(conn, sql)) {
            stmt.setObject("id", UUID.fromString(inputUser.getId()));
            stmt.setString("discordName", inputUser.getDiscordName());
            stmt.setString("discordId", inputUser.getDiscordId());
            stmt.setString("leetcodeUsername", inputUser.getLeetcodeUsername());
            stmt.setString("nickname", inputUser.getNickname());
            stmt.setBoolean("admin", inputUser.isAdmin());
            stmt.setString("profileUrl", inputUser.getProfileUrl());
            stmt.setString("schoolEmail", inputUser.getSchoolEmail());

            // We don't care what this actually returns, it can never be more than 1 anyways
            // because id is UNIQUE. Just return the new user every time if we want to do
            // any work on it.
            int rowsAffected = stmt.executeUpdate();

            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error while updating user", e);
        }
    }

    @Override
    public ArrayList<User> getAllUsers() {
        ArrayList<User> users = new ArrayList<>();
        String sql = """
                        SELECT
                            id,
                            "discordId",
                            "discordName",
                            "leetcodeUsername",
                            "nickname",
                            "schoolEmail",
                            admin,
                            "verifyKey",
                            "profileUrl"
                        FROM "User"
                        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    users.add(parseResultSetToUser(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while retrieving user", e);
        }

        return users;
    }

    @Override
    public ArrayList<User> getAllUsers(final int page, final int pageSize, final String query) {
        ArrayList<User> users = new ArrayList<>();
        String sql = """
                            SELECT
                                id,
                                "discordId",
                                "discordName",
                                "leetcodeUsername",
                                "nickname",
                                "schoolEmail",
                                admin,
                                "verifyKey",
                                "profileUrl"
                            FROM
                                "User"
                            WHERE
                                ("discordName" ILIKE :query OR "leetcodeUsername" ILIKE :query OR "nickname" ILIKE :query)
                            ORDER BY
                                id
                            LIMIT :limit OFFSET :offset
                        """;

        try (NamedPreparedStatement stmt = NamedPreparedStatement.create(conn, sql)) {
            stmt.setString("query", "%" + query + "%");
            stmt.setInt("limit", pageSize);
            stmt.setInt("offset", (page - 1) * pageSize);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    users.add(parseResultSetToUser(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while retrieving paginated users", e);
        }

        return users;
    }

    @Override
    public boolean userExistsByLeetcodeUsername(final String leetcodeUsername) {
        String sql = """
                        SELECT
                            1
                        FROM "User"
                        WHERE "leetcodeUsername" = ?
                        LIMIT 1
                        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, leetcodeUsername);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while retrieving user count with given leetcodeUsername", e);
        }
    }

    @Override
    public UserWithScore getUserWithScoreByIdAndLeaderboardId(final String userId, final String leaderboardId, final UserFilterOptions options) {
        String sql = """
                            SELECT
                                u.id,
                                u."discordId",
                                u."discordName",
                                u."leetcodeUsername",
                                u."nickname",
                                u."schoolEmail",
                                u.admin,
                                u."verifyKey",
                                u."profileUrl",
                                m."totalScore"
                            FROM
                                "User" u
                            JOIN "Metadata" m ON m."userId" = u.id
                            WHERE
                                u.id = :id
                                AND
                                m."leaderboardId" = :leaderboardId
                        """;

        try (NamedPreparedStatement stmt = NamedPreparedStatement.create(conn, sql)) {
            stmt.setObject("id", UUID.fromString(userId));
            stmt.setObject("leaderboardId", UUID.fromString(leaderboardId));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return parseResultSetToUserWithScore(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get user with score by id", e);
        }

        return null;
    }

    @Override
    public UserWithScore getUserWithScoreByLeetcodeUsernameAndLeaderboardId(final String userLeetcodeUsername, final String leaderboardId) {
        String sql = """
                            SELECT
                                u.id,
                                u."discordId",
                                u."discordName",
                                u."leetcodeUsername",
                                u.nickname,
                                u.admin,
                                u."profileUrl",
                                u."verifyKey",
                                m."totalScore"
                            FROM
                                "User" u
                            JOIN "Metadata" m ON m."userLeetcodeUsername" = u."leetcodeUsername"
                            WHERE
                                u."leetcodeUsername" = :leetcodeUsername
                                AND
                                m."leaderboardId" = :leaderboardId
                        """;

        try (NamedPreparedStatement stmt = NamedPreparedStatement.create(conn, sql)) {
            stmt.setString("leetcodeUsername", userLeetcodeUsername);
            stmt.setObject("leaderboardId", UUID.fromString(leaderboardId));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return parseResultSetToUserWithScore(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get user with score by leetcode username", e);
        }

        return null;
    }

    @Override
    public int getUserCount(final String query) {
        String sql = """
                        SELECT
                            COUNT(*)
                        FROM
                            "User"
                        WHERE
                            ("discordName" ILIKE :query OR "leetcodeUsername" ILIKE :query OR "nickname" ILIKE :query)
                        """;
        try (NamedPreparedStatement stmt = NamedPreparedStatement.create(conn, sql)) {
            stmt.setString("query", "%" + query + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while counting users", e);
        }
        return 0;
    }

    @Override
    public boolean deleteUserById(final String id) {
        String sql = """
                            DELETE FROM "User"
                            WHERE
                                id = :id
                        """;

        try (NamedPreparedStatement stmt = NamedPreparedStatement.create(conn, sql)) {
            stmt.setObject("id", UUID.fromString(id));

            int rowsAffected = stmt.executeUpdate();

            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete user by id", e);
        }
    }
}
