package com.patina.codebloom.common.db.repos.leaderboard;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.patina.codebloom.common.db.DbConnection;
import com.patina.codebloom.common.db.models.leaderboard.Leaderboard;
import com.patina.codebloom.common.db.models.user.UserWithScore;
import com.patina.codebloom.common.db.repos.user.UserRepository;
import com.patina.codebloom.common.db.repos.leaderboard.options.LeaderboardFilterOptions;

@Component
public class LeaderboardSqlRepository implements LeaderboardRepository {
    private Connection conn;
    private final UserRepository userRepository;

    public LeaderboardSqlRepository(final DbConnection dbConnection, final UserRepository userRepository) {
        this.conn = dbConnection.getConn();
        this.userRepository = userRepository;
    }

    private Leaderboard parseResultSetToLeaderboard(final ResultSet resultSet) throws SQLException {
        return Leaderboard.builder()
                        .id(resultSet.getString("id"))
                        .createdAt(resultSet.getTimestamp("createdAt").toLocalDateTime())
                        .deletedAt(
                                        Optional.ofNullable(
                                                        resultSet.getTimestamp("deletedAt"))
                                                        .map(Timestamp::toLocalDateTime)
                                                        .orElse(null))
                        .name(resultSet.getString("name"))
                        .shouldExpireBy(
                                        Optional.ofNullable(
                                                        resultSet.getTimestamp("shouldExpireBy"))
                                                        .map(Timestamp::toLocalDateTime)
                                                        .orElse(null))
                        .build();
    }

    @Override
    public boolean disableLeaderboardById(final String leaderboardId) {
        String sql = """
                        UPDATE "Leaderboard"
                        SET
                            "deletedAt" = NOW()
                        WHERE
                            id = ?
                        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, UUID.fromString(leaderboardId));
            int rowsAffected = stmt.executeUpdate();

            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to disable leaderboard", e);
        }
    }

    @Override
    public void addNewLeaderboard(final Leaderboard leaderboard) {
        String sql = """
                        INSERT INTO "Leaderboard"
                            (id, name)
                        VALUES
                            (?, ?)
                        RETURNING
                            "createdAt"
                        """;
        leaderboard.setId(UUID.randomUUID().toString());
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, UUID.fromString(leaderboard.getId()));
            stmt.setString(2, leaderboard.getName());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    var createdAt = rs.getTimestamp("createdAt").toLocalDateTime();
                    leaderboard.setCreatedAt(createdAt);
                }
            }
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
                    return parseResultSetToLeaderboard(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch recent leaderboard metadata", e);
        }

        return null;
    }

    @Override
    public Leaderboard getLeaderboardMetadataById(final String id) {
        String sql = """
                        SELECT
                            id,
                            name,
                            "createdAt",
                            "deletedAt",
                            "shouldExpireBy"
                        FROM "Leaderboard"
                        WHERE
                            id = ?
                        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, UUID.fromString(id));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return parseResultSetToLeaderboard(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch recent leaderboard metadata", e);
        }

        return null;
    }

    @Override
    public ArrayList<UserWithScore> getRecentLeaderboardUsers(final LeaderboardFilterOptions options) {
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
            stmt.setBoolean(1, options.isPatina());
            stmt.setString(2, "%" + options.getQuery() + "%");
            stmt.setString(3, "%" + options.getQuery() + "%");
            stmt.setString(4, "%" + options.getQuery() + "%");
            stmt.setInt(5, options.getPageSize());
            stmt.setInt(6, (options.getPage() - 1) * options.getPageSize());
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
    public ArrayList<UserWithScore> getLeaderboardUsersById(final String id, final LeaderboardFilterOptions options) {
        ArrayList<UserWithScore> users = new ArrayList<>();

        String sql = """
                        SELECT
                            m."userId",
                            l.id as "leaderboardId"
                        FROM
                            "Leaderboard" l
                        JOIN "Metadata" m ON
                            m."leaderboardId" = l.id
                        JOIN "User" u ON
                            u.id = m."userId"
                        LEFT JOIN "UserTag" ut
                            ON ut."userId" = m."userId"
                        WHERE
                            l.id = ?
                        AND
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
            stmt.setObject(1, UUID.fromString(id));
            stmt.setBoolean(2, options.isPatina());
            stmt.setString(3, "%" + options.getQuery() + "%");
            stmt.setString(4, "%" + options.getQuery() + "%");
            stmt.setString(5, "%" + options.getQuery() + "%");
            stmt.setInt(6, options.getPageSize());
            stmt.setInt(7, (options.getPage() - 1) * options.getPageSize());
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
        String sql = """
                        UPDATE "Leaderboard"
                        SET
                            name = ?,
                            "createdAt" = ?,
                            "deletedAt" = ?
                        WHERE id = ?
                        """;

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
        String sql = """
                        INSERT INTO "Metadata"
                            (id, "userId", "leaderboardId")
                        VALUES
                            (?, ?, ?)
                        """;

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
        String sql = """
                        UPDATE "Metadata"
                        SET
                            "totalScore" = ?
                        WHERE
                            "userId" = ?
                        AND
                            "leaderboardId" = ?
                        """;

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
    public int getRecentLeaderboardUserCount(final LeaderboardFilterOptions options) {
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
            stmt.setBoolean(1, options.isPatina());
            stmt.setString(2, "%" + options.getQuery() + "%");
            stmt.setString(3, "%" + options.getQuery() + "%");
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
    public int getLeaderboardUserCountById(final String id, final LeaderboardFilterOptions options) {
        String sql = """
                            SELECT
                                COUNT(m.id)
                            FROM
                                "Leaderboard" l
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
                                l.id = ?
                            AND
                                (? = FALSE OR ut.tag = 'Patina')
                            AND
                                (u."discordName" ILIKE ? OR u."leetcodeUsername" ILIKE ?)
                        """;
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, UUID.fromString(id));
            stmt.setBoolean(2, options.isPatina());
            stmt.setString(3, "%" + options.getQuery() + "%");
            stmt.setString(4, "%" + options.getQuery() + "%");
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
    public ArrayList<Leaderboard> getAllLeaderboardsShallow(final LeaderboardFilterOptions options) {
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
                            ORDER BY
                                "createdAt" DESC
                            LIMIT ? OFFSET ?
                        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + options.getQuery() + "%");
            stmt.setInt(2, options.getPageSize());
            stmt.setInt(3, (options.getPage() - 1) * options.getPageSize());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    leaderboards.add(parseResultSetToLeaderboard(rs));
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
        String sql = """
                        INSERT INTO "Metadata"
                            (id, "userId", "leaderboardId")
                        VALUES
                            (?, ?, ?)
                        """;

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
        String sql = """
                        SELECT
                            COUNT(*)
                        FROM
                            "Leaderboard"
                        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while retrieving leaderboard count", e);
        }

        return 0;
    }

    /**
     * Internal use only. Intended for testing use cases (access via reflection).
     */
    private boolean deleteLeaderboardById(final String id) {
        String sql = """
                            DELETE FROM "Leaderboard"
                            WHERE
                                id = ?
                        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, UUID.fromString(id));

            int rowsAffected = stmt.executeUpdate();

            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete leaderboard by id", e);
        }
    }

    /**
     * Internal use only. Intended for testing use cases (access via reflection).
     *
     * @note This will only re-activate a leaderboard if it's the most recent
     * leaderboard entry.
     */
    private boolean enableLeaderboardById(final String id) {
        String sql = """
                            UPDATE "Leaderboard"
                            SET
                                "deletedAt" = NULL
                            WHERE
                                id = ?
                        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, UUID.fromString(id));

            int rowsAffected = stmt.executeUpdate();

            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to enable leaderboard by id", e);
        }
    }
}
