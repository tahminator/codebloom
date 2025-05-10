package com.patina.codebloom.common.db.repos.leaderboard;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.patina.codebloom.common.db.DbConnection;
import com.patina.codebloom.common.db.models.leaderboard.Leaderboard;
import com.patina.codebloom.common.db.models.user.UserWithScore;
import com.patina.codebloom.common.db.repos.user.UserRepository;

@Component
public class LeaderboardSqlRepository implements LeaderboardRepository {
    private Connection conn;
    private final UserRepository userRepository;

    public LeaderboardSqlRepository(final DbConnection dbConnection, final UserRepository userRepository) {
        this.conn = dbConnection.getConn();
        this.userRepository = userRepository;
    }

    @Override
    public boolean disableLeaderboardById(final String leaderboardId) {
        String sql = "UPDATE \"Leaderboard\" SET \"deletedAt\" = NOW() WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, UUID.fromString(leaderboardId));
            int rowsAffected = stmt.executeUpdate();

            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to disable leaderboard", e);
        }
    }

    @Override
    public boolean addNewLeaderboard(final Leaderboard leaderboard) {
        String sql = "INSERT INTO \"Leaderboard\" (id, name) VALUES (?, ?) RETURNING \"createdAt\"";
        leaderboard.setId(UUID.randomUUID().toString());
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, UUID.fromString(leaderboard.getId()));
            stmt.setString(2, leaderboard.getName());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    var createdAt = rs.getTimestamp("createdAt").toLocalDateTime();
                    leaderboard.setCreatedAt(createdAt);
                    return true;
                }
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create new leaderboard", e);
        }
    }

    @Override
    public Leaderboard getRecentLeaderboardMetadata() {
        String sql = """
                        SELECT
                            id,
                            name,
                            "createdAt",
                            "deletedAt",
                            "shouldExpireBy"
                        FROM "Leaderboard"
                        WHERE
                            "deletedAt" IS NULL
                        ORDER BY "createdAt" DESC
                        LIMIT 1
                        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    var id = rs.getString("id");
                    var name = rs.getString("name");
                    var createdAt = rs.getTimestamp("createdAt").toLocalDateTime();
                    Timestamp deletedAtTimestamp = rs.getTimestamp("deletedAt");
                    Timestamp shouldExpireByTimestamp = rs.getTimestamp("shouldExpireBy");

                    LocalDateTime deletedAt = null;
                    if (deletedAtTimestamp != null) {
                        deletedAt = deletedAtTimestamp.toLocalDateTime();
                    }

                    LocalDateTime shouldExpireBy = null;
                    if (shouldExpireByTimestamp != null) {
                        deletedAt = shouldExpireByTimestamp.toLocalDateTime();
                    }

                    return new Leaderboard(id, name, createdAt, deletedAt, shouldExpireBy);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch recent leaderboard metadata", e);
        }

        return null;
    }

    @Override
    public ArrayList<UserWithScore> getRecentLeaderboardUsers(final int page, final int pageSize, final String query, final boolean patina) {
        ArrayList<UserWithScore> users = new ArrayList<>();

        String sql = """
                        WITH latest_leaderboard AS (
                            SELECT
                                id
                            FROM
                                "Leaderboard"
                            WHERE
                                "deletedAt" IS NULL
                            ORDER BY
                                "createdAt" DESC
                            LIMIT 1
                        )
                        SELECT
                            m."userId",
                            ll.id as "leaderboardId"
                        FROM
                            latest_leaderboard ll
                        JOIN "Metadata" m ON
                            m."leaderboardId" = ll.id
                        JOIN "User" u ON
                            u.id = m."userId"
                        LEFT JOIN "UserTag" ut
                            ON ut."userId" = m."userId"
                        WHERE
                            (? = FALSE OR ut.tag = 'Patina')
                        AND
                            (u."discordName" ILIKE ? OR u."leetcodeUsername" ILIKE ? OR u."nickname" ILIKE ?)
                        ORDER BY
                            m."totalScore" DESC,
                            -- The following case is used to put users with linked leetcode names before
                            -- those who don't.
                            CASE
                                WHEN m."totalScore" = 0 THEN
                                    CASE WHEN u."leetcodeUsername" IS NOT NULL THEN 0 ELSE 1 END
                                ELSE 0
                            END,
                            -- This is the tie breaker if we can't sort them by the above conditions.
                            m."createdAt" ASC
                        LIMIT ? OFFSET ?;
                                                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBoolean(1, patina);
            stmt.setString(2, "%" + query + "%");
            stmt.setString(3, "%" + query + "%");
            stmt.setString(4, "%" + query + "%");
            stmt.setInt(5, pageSize);
            stmt.setInt(6, (page - 1) * pageSize);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    var userId = rs.getString("userId");
                    var leaderboardId = rs.getString("leaderboardId");

                    UserWithScore user = userRepository.getUserWithScoreById(userId, leaderboardId);

                    if (user != null) {
                        users.add(user);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch recent leaderboard users", e);
        }

        return users;
    }

    @Override
    public boolean updateLeaderboard(final Leaderboard leaderboard) {
        String sql = "UPDATE \"Leaderboard\" SET name = ?, \"createdAt\" = ?, \"deletedAt\" = ?, WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
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
    public boolean addUserToLeaderboard(final String userId, final String leaderboardId) {
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
    public boolean updateUserPointsFromLeaderboard(final String leaderboardId, final String userId, final int totalScore) {
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

    @Override
    public int getRecentLeaderboardUserCount(final boolean patina, final String query) {
        String sql = """
                            WITH latest_leaderboard AS (
                                SELECT id
                                FROM "Leaderboard"
                                WHERE "deletedAt" IS NULL
                                ORDER BY "createdAt" DESC
                                LIMIT 1
                            )
                            SELECT
                                COUNT(m.id)
                            FROM
                                "Leaderboard" l
                            INNER JOIN latest_leaderboard ON latest_leaderboard.id = l.id
                            JOIN
                                "Metadata" m
                            ON
                                m."leaderboardId" = l.id
                            JOIN
                                "User" u
                            ON
                                u.id = m."userId"
                            LEFT JOIN
                                "UserTag" ut
                            ON
                                ut."userId" = m."userId"
                            WHERE
                                (? = FALSE OR ut.tag = 'Patina')
                            AND
                                (u."discordName" ILIKE ? OR u."leetcodeUsername" ILIKE ?)
                        """;
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBoolean(1, patina);
            stmt.setString(2, "%" + query + "%");
            stmt.setString(3, "%" + query + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve leaderboard users", e);
        }

        return 0;
    }

    @Override
    public ArrayList<Leaderboard> getAllLeaderboardsShallow(final int page, final int pageSize, final String query) {
        ArrayList<Leaderboard> leaderboards = new ArrayList<>();
        String sql = """
                            SELECT
                                id,
                                name,
                                "createdAt",
                                "deletedAt",
                                "shouldExpireBy"
                            FROM "Leaderboard"
                            WHERE name ILIKE ?
                            ORDER BY id
                            LIMIT ? OFFSET ?
                        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + query + "%");
            stmt.setInt(2, pageSize);
            stmt.setInt(3, (page - 1) * pageSize);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    var id = rs.getString("id");
                    var name = rs.getString("name");
                    var createdAt = rs.getTimestamp("createdAt").toLocalDateTime();
                    Timestamp tsDeletedAt = rs.getTimestamp("deletedAt");
                    Timestamp tsShouldExpireBy = rs.getTimestamp("shouldExpireBy");
                    LocalDateTime deletedAt = (tsDeletedAt != null) ? tsDeletedAt.toLocalDateTime() : null;
                    LocalDateTime shouldExpireBy = (tsShouldExpireBy != null) ? tsShouldExpireBy.toLocalDateTime() : null;

                    leaderboards.add(new Leaderboard(id, name, createdAt, deletedAt, shouldExpireBy));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while retrieving all paginated leaderboards", e);
        }

        return leaderboards;
    }

    @Override
    public boolean addAllUsersToLeaderboard(final String leaderboardId) {
        var users = userRepository.getAllUsers();
        String sql = "INSERT INTO \"Metadata\" (id, \"userId\", \"leaderboardId\") VALUES (?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (var user : users) {
                String userMetaId = UUID.randomUUID().toString();
                stmt.setObject(1, UUID.fromString(userMetaId));
                stmt.setObject(2, UUID.fromString(user.getId()));
                stmt.setObject(3, UUID.fromString(leaderboardId));
                stmt.addBatch();
            }

            int[] updates = stmt.executeBatch();
            long successfulInsertions = Arrays.stream(updates).filter(count -> count > 0).count();
            return successfulInsertions == users.size();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to add all users to the the leaderboard", e);
        }
    }

    @Override
    public int getLeaderboardCount() {
        String sql = "SELECT COUNT(*) FROM \"Leaderboard\"";

        try (PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while retrieving leaderboard count", e);
        }

        return 0;
    }
}
