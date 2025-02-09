package com.patina.codebloom.common.db.repos.leaderboard;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.patina.codebloom.common.db.DbConnection;
import com.patina.codebloom.common.db.models.leaderboard.Leaderboard;
import com.patina.codebloom.common.db.models.leaderboard.LeaderboardWithUsers;
import com.patina.codebloom.common.db.models.user.UserWithScore;

@Component
public class LeaderboardSqlRepository implements LeaderboardRepository {
    DbConnection dbConnection;
    Connection conn;

    public LeaderboardSqlRepository(DbConnection dbConnection) {
        this.dbConnection = dbConnection;
        this.conn = dbConnection.getConn();
    }

    @Override
    public ArrayList<Leaderboard> getAllLeaderboardsShallow() {
        ArrayList<Leaderboard> leaderboards = new ArrayList<>();

        String sql = "SELECT id, name, \"createdAt\", \"deletedAt\" FROM \"Leaderboard\" ORDER BY \"createdAt\" DESC";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    var id = rs.getString("id");
                    var name = rs.getString("name");
                    var createdAt = rs.getTimestamp("createdAt").toLocalDateTime();

                    Timestamp deletedAtTimestamp = rs.getTimestamp("deletedAt");

                    LocalDateTime deletedAt = null;
                    if (deletedAtTimestamp != null) {
                        deletedAt = deletedAtTimestamp.toLocalDateTime();
                    }

                    leaderboards.add(new Leaderboard(id, name, createdAt, deletedAt));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get all shallow leaderboards", e);
        }

        return leaderboards;
    }

    @Override
    public ArrayList<LeaderboardWithUsers> getAllLeaderboardsFull() {
        ArrayList<LeaderboardWithUsers> leaderboards = new ArrayList<>();

        String sql = """
                SELECT
                    l.id AS \"leaderboardId\",
                    l.name AS \"leaderboardName\",
                    l."createdAt" AS \"leaderboardCreatedAt\",
                    l."deletedAt" AS \"leaderboardDeletedAt\",
                    u.id AS \"userId\",
                    u.\"discordId\",
                    u.\"discordName\",
                    u.\"leetcodeUsername\",
                    u.\"nickname\",
                    m.\"totalScore\"
                FROM "Leaderboard" l
                LEFT JOIN "Metadata" m ON l.id = m."leaderboardId"
                LEFT JOIN "User" u ON m."userId" = u.id
                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                LeaderboardWithUsers currentLeaderboard = null;

                while (rs.next()) {
                    String leaderboardId = rs.getString("leaderboardId");
                    if (currentLeaderboard == null || !currentLeaderboard.getId().equals(leaderboardId)) {
                        var name = rs.getString("leaderboardName");
                        var createdAt = rs.getTimestamp("leaderboardCreatedAt").toLocalDateTime();

                        Timestamp deletedAtTimestamp = rs.getTimestamp("leaderboardDeletedAt");

                        LocalDateTime deletedAt = null;
                        if (deletedAtTimestamp != null) {
                            deletedAt = deletedAtTimestamp.toLocalDateTime();
                        }

                        currentLeaderboard = new LeaderboardWithUsers(
                                leaderboardId, name, createdAt, deletedAt, new ArrayList<>());
                        leaderboards.add(currentLeaderboard);
                    }

                    String userId = rs.getString("userId");
                    if (userId != null) {
                        var discordId = rs.getString("discordId");
                        var discordName = rs.getString("discordName");
                        var leetcodeUsername = rs.getString("leetcodeUsername");
                        var nickname = rs.getString("nickname");
                        var totalScore = rs.getInt("totalScore");

                        UserWithScore user = new UserWithScore(userId, discordId, discordName, leetcodeUsername,
                                nickname, totalScore);
                        currentLeaderboard.getUsers().add(user);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get all full leaderboards", e);
        }

        return leaderboards;
    }

    @Override
    public LeaderboardWithUsers getLeaderboardByIdShallow(String leaderboardId) {
        LeaderboardWithUsers leaderboard = null;

        String sql = """
                SELECT
                    l.id AS "leaderboardId",
                    l.name AS "leaderboardName",
                    l."createdAt" AS "leaderboardCreatedAt",
                    l."deletedAt" AS "leaderboardDeletedAt",
                    u.id AS "userId",
                    u."discordId",
                    u."discordName",
                    u."leetcodeUsername",
                    u."nickname",
                    m."totalScore"
                FROM "Leaderboard" l
                LEFT JOIN (
                    SELECT *
                    FROM (
                        SELECT
                            m."leaderboardId",
                            u.id AS "userId",
                            u."discordId",
                            u."discordName",
                            u."leetcodeUsername",
                            u."nickname",
                            m."totalScore",
                            ROW_NUMBER() OVER (PARTITION BY m."leaderboardId" ORDER BY m."totalScore" DESC) AS "row_num"
                        FROM "Metadata" m
                        JOIN "User" u ON m."userId" = u.id
                    ) ranked_users
                    WHERE "row_num" <= 5
                ) limited_users ON l.id = limited_users."leaderboardId"
                WHERE l.id = ?
                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, UUID.fromString(leaderboardId));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    if (leaderboard == null) {
                        var currLeaderboardId = rs.getString("leaderboardId");
                        var name = rs.getString("leaderboardName");
                        var createdAt = rs.getTimestamp("leaderboardCreatedAt").toLocalDateTime();

                        Timestamp deletedAtTimestamp = rs.getTimestamp("leaderboardDeletedAt");

                        LocalDateTime deletedAt = null;
                        if (deletedAtTimestamp != null) {
                            deletedAt = deletedAtTimestamp.toLocalDateTime();
                        }

                        LeaderboardWithUsers currentLeaderboard = new LeaderboardWithUsers(
                                currLeaderboardId, name, createdAt, deletedAt, new ArrayList<>());
                        leaderboard = currentLeaderboard;
                    }

                    String userId = rs.getString("userId");

                    if (userId != null) {
                        var discordId = rs.getString("discordId");
                        var discordName = rs.getString("discordName");
                        var leetcodeUsername = rs.getString("leetcodeUsername");
                        var nickname = rs.getString("nickname");
                        var totalScore = rs.getInt("totalScore");

                        UserWithScore user = new UserWithScore(userId, discordId, discordName, leetcodeUsername,
                                nickname,
                                totalScore);
                        leaderboard.addUser(user);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get all full leaderboards", e);
        }

        return leaderboard;
    }

    @Override
    public LeaderboardWithUsers getLeaderboardByIdFull(String leaderboardId) {
        LeaderboardWithUsers leaderboard = null;

        String sql = """
                SELECT
                    l.id AS \"leaderboardId\",
                    l.name AS \"leaderboardName\",
                    l."createdAt" AS \"leaderboardCreatedAt\",
                    l."deletedAt" AS \"leaderboardDeletedAt\",
                    u.id AS \"userId\",
                    u.\"discordId\",
                    u.\"discordName\",
                    u.\"leetcodeUsername\",
                    u.\"nickname\",
                    m.\"totalScore\"
                FROM "Leaderboard" l
                LEFT JOIN "Metadata" m ON l.id = m."leaderboardId"
                LEFT JOIN "User" u ON m."userId" = u.id
                WHERE l.id = ?
                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, UUID.fromString(leaderboardId));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    if (leaderboard == null) {
                        var currLeaderboardId = rs.getString("leaderboardId");
                        var name = rs.getString("leaderboardName");
                        var createdAt = rs.getTimestamp("leaderboardCreatedAt").toLocalDateTime();

                        Timestamp deletedAtTimestamp = rs.getTimestamp("leaderboardDeletedAt");

                        LocalDateTime deletedAt = null;
                        if (deletedAtTimestamp != null) {
                            deletedAt = deletedAtTimestamp.toLocalDateTime();
                        }

                        LeaderboardWithUsers currentLeaderboard = new LeaderboardWithUsers(
                                currLeaderboardId, name, createdAt, deletedAt, new ArrayList<>());
                        leaderboard = currentLeaderboard;
                    }

                    String userId = rs.getString("userId");

                    if (userId != null) {
                        var discordId = rs.getString("discordId");
                        var discordName = rs.getString("discordName");
                        var leetcodeUsername = rs.getString("leetcodeUsername");
                        var nickname = rs.getString("nickname");
                        var totalScore = rs.getInt("totalScore");

                        UserWithScore user = new UserWithScore(userId, discordId, discordName, leetcodeUsername,
                                nickname, totalScore);
                        leaderboard.addUser(user);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get all full leaderboards", e);
        }

        return leaderboard;
    }

    @Override
    public boolean disableLeaderboardById(String leaderboardId) {
        String SQL = "UPDATE \"Leaderboard\" SET \"deletedAt\" = NOW() WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(SQL)) {
            int rowsAffected = stmt.executeUpdate();

            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to disable leaderboard", e);
        }
    }

    @Override
    public Leaderboard addNewLeaderboard(Leaderboard leaderboard) {
        String sql = "INSERT INTO \"Leaderboard\" (id, name) VALUES (?, ?)";
        leaderboard.setId(UUID.randomUUID().toString());
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, UUID.fromString(leaderboard.getId()));
            stmt.setObject(2, UUID.fromString(leaderboard.getName()));

            stmt.executeUpdate();

            return leaderboard;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create new leaderboard", e);
        }
    }

    @Override
    public LeaderboardWithUsers getRecentLeaderboardShallow() {
        LeaderboardWithUsers leaderboard = null;

        String sql = """
                WITH latest_leaderboard AS (
                    SELECT id AS \"leaderboardId\"
                    FROM \"Leaderboard\"
                    WHERE \"deletedAt\" IS NULL
                    ORDER BY \"createdAt\" DESC
                    LIMIT 1
                )
                SELECT
                    l.id AS "leaderboardId",
                    l.name AS "leaderboardName",
                    l."createdAt" AS "leaderboardCreatedAt",
                    l."deletedAt" AS "leaderboardDeletedAt",
                    limited_users."userId",
                    limited_users."discordId",
                    limited_users."discordName",
                    limited_users."leetcodeUsername",
                    limited_users."nickname",
                    limited_users."totalScore"
                FROM "Leaderboard" l
                INNER JOIN latest_leaderboard ll ON l.id = ll.\"leaderboardId\"
                LEFT JOIN (
                    SELECT
                        ranked_users."leaderboardId",
                        ranked_users."userId",
                        ranked_users."discordId",
                        ranked_users."discordName",
                        ranked_users."leetcodeUsername",
                        ranked_users."nickname",
                        ranked_users."totalScore"
                    FROM (
                        SELECT
                            m."leaderboardId",
                            u.id AS "userId",
                            u."discordId",
                            u."discordName",
                            u."leetcodeUsername",
                            u."nickname",
                            m."totalScore",
                            ROW_NUMBER() OVER (PARTITION BY m."leaderboardId" ORDER BY m."totalScore" DESC) AS "row_num"
                        FROM "Metadata" m
                        JOIN "User" u ON m."userId" = u.id
                    ) ranked_users
                    WHERE ranked_users."row_num" <= 5
                ) limited_users ON l.id = limited_users."leaderboardId"
                ORDER BY l."createdAt" DESC
                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    if (leaderboard == null) {
                        var currLeaderboardId = rs.getString("leaderboardId");
                        var name = rs.getString("leaderboardName");
                        var createdAt = rs.getTimestamp("leaderboardCreatedAt").toLocalDateTime();

                        Timestamp deletedAtTimestamp = rs.getTimestamp("leaderboardDeletedAt");

                        LocalDateTime deletedAt = null;
                        if (deletedAtTimestamp != null) {
                            deletedAt = deletedAtTimestamp.toLocalDateTime();
                        }

                        LeaderboardWithUsers currentLeaderboard = new LeaderboardWithUsers(
                                currLeaderboardId, name, createdAt, deletedAt, new ArrayList<>());
                        leaderboard = currentLeaderboard;
                    }

                    String userId = rs.getString("userId");

                    if (userId != null) {
                        var discordId = rs.getString("discordId");
                        var discordName = rs.getString("discordName");
                        var leetcodeUsername = rs.getString("leetcodeUsername");
                        var nickname = rs.getString("nickname");
                        var totalScore = rs.getInt("totalScore");

                        UserWithScore user = new UserWithScore(userId, discordId, discordName, leetcodeUsername,
                                nickname, totalScore);
                        leaderboard.addUser(user);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get all full leaderboards", e);
        }

        return leaderboard;
    }

    @Override
    public LeaderboardWithUsers getRecentLeaderboardFull() {
        LeaderboardWithUsers leaderboard = null;

        String sql = """
                WITH latest_leaderboard AS (
                    SELECT id
                    FROM "Leaderboard"
                    WHERE "deletedAt" IS NULL
                    ORDER BY "createdAt" DESC
                    LIMIT 1
                )
                SELECT
                    l.id AS "leaderboardId",
                    l.name AS "leaderboardName",
                    l."createdAt" AS "leaderboardCreatedAt",
                    l."deletedAt" AS "leaderboardDeletedAt",
                    ranked_users."userId",
                    ranked_users."discordId",
                    ranked_users."discordName",
                    ranked_users."leetcodeUsername",
                    ranked_users."nickname",
                    ranked_users."totalScore"
                FROM "Leaderboard" l
                INNER JOIN latest_leaderboard ll ON l.id = ll.id
                LEFT JOIN (
                        SELECT
                            m."leaderboardId",
                            u.id AS "userId",
                            u."discordId",
                            u."discordName",
                            u."leetcodeUsername",
                            u."nickname",
                            m."totalScore",
                            ROW_NUMBER() OVER (PARTITION BY m."leaderboardId" ORDER BY m."totalScore" DESC) AS "row_num"
                        FROM "Metadata" m
                        JOIN "User" u ON m."userId" = u.id
                        ORDER BY "row_num" ASC
                ) ranked_users ON l.id = ranked_users."leaderboardId"
                ORDER BY "leaderboardCreatedAt" DESC
                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    if (leaderboard == null) {
                        var currLeaderboardId = rs.getString("leaderboardId");
                        var name = rs.getString("leaderboardName");
                        var createdAt = rs.getTimestamp("leaderboardCreatedAt").toLocalDateTime();

                        Timestamp deletedAtTimestamp = rs.getTimestamp("leaderboardDeletedAt");

                        LocalDateTime deletedAt = null;
                        if (deletedAtTimestamp != null) {
                            deletedAt = deletedAtTimestamp.toLocalDateTime();
                        }

                        LeaderboardWithUsers currentLeaderboard = new LeaderboardWithUsers(
                                currLeaderboardId, name, createdAt, deletedAt, new ArrayList<>());
                        leaderboard = currentLeaderboard;
                    }

                    String userId = rs.getString("userId");

                    if (userId != null) {
                        var discordId = rs.getString("discordId");
                        var discordName = rs.getString("discordName");
                        var leetcodeUsername = rs.getString("leetcodeUsername");
                        var nickname = rs.getString("nickname");
                        var totalScore = rs.getInt("totalScore");

                        UserWithScore user = new UserWithScore(userId, discordId, discordName, leetcodeUsername,
                                nickname, totalScore);
                        leaderboard.addUser(user);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get all full leaderboards", e);
        }

        return leaderboard;
    }

    @Override
    public boolean updateLeaderboard(Leaderboard leaderboard) {
        String SQL = "UPDATE \"Leaderboard\" SET name = ?, \"createdAt\" = ?, \"deletedAt\" = ?, WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(SQL)) {
            stmt.setString(1, leaderboard.getName());
            stmt.setObject(2, leaderboard.getCreatedAt());
            stmt.setObject(3, leaderboard.getDeletedAt());
            stmt.setObject(4, UUID.fromString(leaderboard.getId()));

            int rowsAffected = stmt.executeUpdate();

            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to disable update leaderboard", e);
        }
    }

    @Override
    public boolean addUserToLeaderboard(String userId, String leaderboardId) {
        String sql = "INSERT INTO \"Metadata\" (id, \"userId\", \"leaderboardId\") VALUES (?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, UUID.randomUUID());
            stmt.setObject(2, UUID.fromString(userId));
            stmt.setObject(3, UUID.fromString(leaderboardId));

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to add user to leaderboard", e);
        }
    }

    @Override
    public UserWithScore getUserFromLeaderboard(String leaderboardId, String userId) {
        UserWithScore user = null;

        String sql = """
                SELECT
                    u.id,
                    u.\"discordId\",
                    u.\"discordName\",
                    u.\"leetcodeUsername\",
                    u.\"nickname\",
                    m.\"totalScore\"
                FROM \"Metadata\" "m"
                LEFT JOIN \"User\" "u" ON u.id = m.\"userId\"
                LEFT JOIN \"Leaderboard\" "l" ON l.id = m.\"leaderboardId\"
                WHERE m.\"userId\" = ? AND m.\"leaderboardId\" = ?
                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, UUID.fromString(userId));
            stmt.setObject(2, UUID.fromString(leaderboardId));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    var id = rs.getString("id");
                    var discordId = rs.getString("discordId");
                    var discordName = rs.getString("discordName");
                    var leetcodeName = rs.getString("leetcodeUsername");
                    var nickname = rs.getString("nickname");
                    var totalScore = rs.getInt("totalScore");
                    user = new UserWithScore(id, discordId, discordName, leetcodeName, nickname, totalScore);
                    return user;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch user from a specific leaderboard", e);
        }

        return user;
    }

    @Override
    public boolean updateUserPointsFromLeaderboard(String leaderboardId, String userId, int totalScore) {
        String sql = "UPDATE \"Metadata\" SET \"totalScore\" = ? WHERE \"userId\" = ? AND \"leaderboardId\" = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, totalScore);
            stmt.setObject(2, UUID.fromString(userId));
            stmt.setObject(3, UUID.fromString(leaderboardId));

            int rowsAffected = stmt.executeUpdate();

            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update metadata", e);
        }
    }

}
