package com.patina.codebloom.common.db.repos.leaderboard;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.patina.codebloom.common.db.DbConnection;
import com.patina.codebloom.common.db.helper.NamedPreparedStatement;
import com.patina.codebloom.common.db.models.leaderboard.Leaderboard;
import com.patina.codebloom.common.db.models.user.UserWithScore;
import com.patina.codebloom.common.db.repos.user.UserRepository;
import com.patina.codebloom.common.db.repos.user.options.UserFilterOptions;
import com.patina.codebloom.common.page.Indexed;
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
                            id = :id
                        """;

        try (NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setObject("id", UUID.fromString(leaderboardId));
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
                            (:id, :name)
                        RETURNING
                            "createdAt"
                        """;
        leaderboard.setId(UUID.randomUUID().toString());
        try (NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setObject("id", UUID.fromString(leaderboard.getId()));
            stmt.setString("name", leaderboard.getName());
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
                            id = :id
                        """;

        try (NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setObject("id", UUID.fromString(id));
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
    public List<Indexed<UserWithScore>> getGlobalRankedIndexedLeaderboardUsersById(final String leaderboardId, final LeaderboardFilterOptions options) {
        List<UserWithScore> users = this.getLeaderboardUsersById(leaderboardId, options);
        Map<String, UserWithScore> userIdToUserMap = users.stream()
                        .collect(
                                        Collectors.toMap(
                                                        user -> user.getId(),
                                                        Function.identity()));
        List<Indexed<UserWithScore>> result = new ArrayList<>();

        String sql = """
                            WITH ranks AS (
                                SELECT
                                    m."userId",
                                    ROW_NUMBER() OVER (
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
                                            m."createdAt" ASC,
                                            -- This is extremely rare, but if the createdAt time is somehow not unique,
                                            -- this serves to be the final tiebreaker.
                                            m."userId"
                                    ) AS rank
                                FROM "Metadata" m
                                JOIN "User" u ON u.id = m."userId"
                                WHERE m."leaderboardId" = :leaderboardId
                            )
                            SELECT
                                r."userId",
                                r.rank
                            FROM
                                ranks r
                            WHERE
                                r."userId" = ANY(:userIds)
                            ORDER BY
                                r.rank ASC
                        """;

        try (NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            UUID[] userIds = users.stream()
                            .map(user -> UUID.fromString(user.getId()))
                            .toArray(size -> new UUID[size]);

            stmt.setArray("userIds", conn.createArrayOf("UUID", userIds));
            stmt.setObject("leaderboardId", UUID.fromString(leaderboardId));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    var userId = rs.getString("userId");
                    var rank = rs.getInt("rank");
                    result.add(Indexed.of(userIdToUserMap.get(userId), rank));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get ranks for leaderboard users", e);
        }

        return result;
    }

    @Override
    public List<Indexed<UserWithScore>> getRankedIndexedLeaderboardUsersById(final String leaderboardId, final LeaderboardFilterOptions options) {
        List<UserWithScore> users = this.getLeaderboardUsersById(leaderboardId, options);
        Map<String, UserWithScore> userIdToUserMap = users.stream()
                        .collect(
                                        Collectors.toMap(
                                                        user -> user.getId(),
                                                        Function.identity()));
        List<Indexed<UserWithScore>> result = new ArrayList<>();

        String sql = """
                        WITH ranks AS (
                            SELECT
                                m."userId",
                                ROW_NUMBER() OVER (
                                    ORDER BY
                                        m."totalScore" DESC,
                                        CASE
                                            WHEN m."totalScore" = 0 THEN
                                                CASE WHEN u."leetcodeUsername" IS NOT NULL THEN 0 ELSE 1 END
                                            ELSE 0
                                        END,
                                        m."createdAt" ASC,
                                        m."userId"
                                ) AS rank
                            FROM
                                "Metadata" m
                            JOIN
                                "User" u
                            ON
                                u.id = m."userId"
                            JOIN
                                "Leaderboard" l
                            ON
                                m."leaderboardId" = l.id
                            WHERE
                                m."leaderboardId" = :leaderboardId
                            AND (
                                EXISTS (
                                    SELECT 1 FROM "UserTag" ut
                                    WHERE ut."userId" = m."userId"
                                    AND (
                                        (:patina = TRUE AND ut.tag = 'Patina') OR
                                        (:hunter = TRUE AND ut.tag = 'Hunter') OR
                                        (:nyu = TRUE AND ut.tag = 'Nyu') OR
                                        (:baruch = TRUE AND ut.tag = 'Baruch') OR
                                        (:rpi = TRUE AND ut.tag = 'Rpi') OR
                                        (:gwc = TRUE AND ut.tag = 'Gwc') OR
                                        (:sbu = TRUE AND ut.tag = 'Sbu')
                                    )
                                    AND (
                                        -- Any tag is valid for current leaderboard
                                        (l."deletedAt" IS NULL)
                                        OR
                                        -- Tag is only valid for previous leaderboards if it was created before
                                        -- leaderboard started, or during the lifespan of leaderboard.
                                        (l."deletedAt" IS NOT NULL AND ut."createdAt" <= l."deletedAt")
                                    )
                                )
                                OR (:patina = FALSE AND :hunter = FALSE AND :nyu = FALSE AND :baruch = FALSE AND :rpi = FALSE AND :gwc = FALSE AND :sbu = FALSE )
                            )
                        )
                        SELECT
                            r."userId",
                            r.rank
                        FROM
                            ranks r
                        WHERE
                            r."userId" = ANY(:userIds)
                        ORDER BY
                            r.rank ASC
                                                """;

        try (NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            UUID[] userIds = users.stream()
                            .map(user -> UUID.fromString(user.getId()))
                            .toArray(size -> new UUID[size]);

            stmt.setArray("userIds", conn.createArrayOf("UUID", userIds));
            stmt.setObject("leaderboardId", UUID.fromString(leaderboardId));

            stmt.setBoolean("patina", options.isPatina());
            stmt.setBoolean("hunter", options.isHunter());
            stmt.setBoolean("nyu", options.isNyu());
            stmt.setBoolean("baruch", options.isBaruch());
            stmt.setBoolean("rpi", options.isRpi());
            stmt.setBoolean("gwc", options.isGwc());
            stmt.setBoolean("sbu", options.isSbu());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    var userId = rs.getString("userId");
                    var rank = rs.getInt("rank");
                    result.add(Indexed.of(userIdToUserMap.get(userId), rank));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get ranks for leaderboard users", e);
        }

        return result;
    }

    @Override
    public List<UserWithScore> getRecentLeaderboardUsers(final LeaderboardFilterOptions options) {
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
                            ll.id as "leaderboardId",
                            l."deletedAt" as "leaderboardDeletedAt"
                        FROM
                            "Leaderboard" l
                        JOIN
                            latest_leaderboard ll
                        ON
                            ll.id = l.id
                        JOIN "Metadata" m ON
                            m."leaderboardId" = ll.id
                        JOIN "User" u ON
                            u.id = m."userId"
                        WHERE (
                            EXISTS (
                                SELECT 1 FROM "UserTag" ut
                                WHERE ut."userId" = m."userId"
                                AND (
                                    (:patina = TRUE AND ut.tag = 'Patina') OR
                                    (:hunter = TRUE AND ut.tag = 'Hunter') OR
                                    (:nyu = TRUE AND ut.tag = 'Nyu') OR
                                    (:baruch = TRUE AND ut.tag = 'Baruch') OR
                                    (:rpi = TRUE AND ut.tag = 'Rpi') OR
                                    (:gwc = TRUE AND ut.tag = 'Gwc') OR
                                    (:sbu = TRUE AND ut.tag = 'Sbu')
                                )
                                AND (
                                    -- Any tag is valid for current leaderboard
                                    (l."deletedAt" IS NULL)
                                    OR
                                    -- Tag is only valid for previous leaderboards if it was created before
                                    -- leaderboard started, or during the lifespan of leaderboard.
                                    (l."deletedAt" IS NOT NULL AND ut."createdAt" <= l."deletedAt")
                                )
                            )
                            OR (:patina = FALSE AND :hunter = FALSE AND :nyu = FALSE AND :baruch = FALSE AND :rpi = FALSE AND :gwc = FALSE  AND :sbu = FALSE )
                        )
                        AND
                            (u."discordName" ILIKE :searchQuery OR u."leetcodeUsername" ILIKE :searchQuery OR u."nickname" ILIKE :searchQuery)
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
                            m."createdAt" ASC,
                            -- This is extremely rare, but if the createdAt time is somehow not unique,
                            -- this serves to be the final tiebreaker.
                            m."userId"
                        LIMIT :pageSize OFFSET :pageNumber;
                                        """;

        try (NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setBoolean("patina", options.isPatina());
            stmt.setBoolean("hunter", options.isHunter());
            stmt.setBoolean("nyu", options.isNyu());
            stmt.setBoolean("baruch", options.isBaruch());
            stmt.setBoolean("rpi", options.isRpi());
            stmt.setBoolean("gwc", options.isGwc());
            stmt.setBoolean("sbu", options.isSbu());
            stmt.setString("searchQuery", "%" + options.getQuery() + "%");
            stmt.setInt("pageSize", options.getPageSize());
            stmt.setInt("pageNumber", (options.getPage() - 1) * options.getPageSize());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    var userId = rs.getString("userId");
                    var leaderboardId = rs.getString("leaderboardId");
                    var leaderboardDeletedAt = rs.getObject("leaderboardDeletedAt", OffsetDateTime.class);

                    UserWithScore user = userRepository.getUserWithScoreById(
                                    userId,
                                    leaderboardId,
                                    UserFilterOptions.builder()
                                                    .pointOfTime(leaderboardDeletedAt)
                                                    .build());

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
    public List<UserWithScore> getLeaderboardUsersById(final String id, final LeaderboardFilterOptions options) {
        ArrayList<UserWithScore> users = new ArrayList<>();

        String sql = """
                        SELECT
                            m."userId",
                            l.id as "leaderboardId",
                            l."deletedAt" as "leaderboardDeletedAt"
                        FROM
                            "Leaderboard" l
                        JOIN "Metadata" m ON
                            m."leaderboardId" = l.id
                        JOIN "User" u ON
                            u.id = m."userId"
                        WHERE
                            l.id = :leaderboardId
                        AND (
                            EXISTS (
                                SELECT 1 FROM "UserTag" ut
                                WHERE ut."userId" = m."userId"
                                AND (
                                    (:patina = TRUE AND ut.tag = 'Patina') OR
                                    (:hunter = TRUE AND ut.tag = 'Hunter') OR
                                    (:nyu = TRUE AND ut.tag = 'Nyu') OR
                                    (:baruch = TRUE AND ut.tag = 'Baruch') OR
                                    (:rpi = TRUE AND ut.tag = 'Rpi') OR
                                    (:gwc = TRUE AND ut.tag = 'Gwc') OR
                                    (:sbu = TRUE AND ut.tag = 'Sbu')
                                )
                                AND (
                                    -- Any tag is valid for current leaderboard
                                    (l."deletedAt" IS NULL)
                                    OR
                                    -- Tag is only valid for previous leaderboards if it was created before
                                    -- leaderboard started, or during the lifespan of leaderboard.
                                    (l."deletedAt" IS NOT NULL AND ut."createdAt" <= l."deletedAt")
                                )
                            )
                            OR (:patina = FALSE AND :hunter = FALSE AND :nyu = FALSE AND :baruch = FALSE AND :rpi = FALSE AND :gwc = FALSE  AND :sbu = FALSE)
                        )
                        AND
                            (u."discordName" ILIKE :searchQuery OR u."leetcodeUsername" ILIKE :searchQuery OR u."nickname" ILIKE :searchQuery)
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
                            m."createdAt" ASC,
                            -- This is extremely rare, but if the createdAt time is somehow not unique,
                            -- this serves to be the final tiebreaker.
                            m."userId"
                        LIMIT :pageSize OFFSET :pageNumber;
                                        """;

        try (NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setObject("leaderboardId", UUID.fromString(id));
            stmt.setBoolean("patina", options.isPatina());
            stmt.setBoolean("hunter", options.isHunter());
            stmt.setBoolean("nyu", options.isNyu());
            stmt.setBoolean("baruch", options.isBaruch());
            stmt.setBoolean("rpi", options.isRpi());
            stmt.setBoolean("gwc", options.isGwc());
            stmt.setBoolean("sbu", options.isSbu());
            stmt.setString("searchQuery", "%" + options.getQuery() + "%");
            stmt.setInt("pageSize", options.getPageSize());
            stmt.setInt("pageNumber", (options.getPage() - 1) * options.getPageSize());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    var userId = rs.getString("userId");
                    var leaderboardId = rs.getString("leaderboardId");
                    var leaderboardDeletedAt = rs.getObject("leaderboardDeletedAt", OffsetDateTime.class);

                    UserWithScore user = userRepository.getUserWithScoreById(
                                    userId,
                                    leaderboardId,
                                    UserFilterOptions.builder()
                                                    .pointOfTime(leaderboardDeletedAt)
                                                    .build());

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
                            name = :name,
                            "createdAt" = :createdAt,
                            "deletedAt" = :deletedAt
                        WHERE id = :id
                        """;

        try (NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setString("name", leaderboard.getName());
            stmt.setObject("createdAt", leaderboard.getCreatedAt());
            stmt.setObject("deletedAt", leaderboard.getDeletedAt());
            stmt.setObject("id", UUID.fromString(leaderboard.getId()));

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
                            (:id, :userId, :leaderboardId)
                        """;

        try (NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setObject("id", UUID.randomUUID());
            stmt.setObject("userId", UUID.fromString(userId));
            stmt.setObject("leaderboardId", UUID.fromString(leaderboardId));

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
                            "totalScore" = :totalScore
                        WHERE
                            "userId" = :userId
                        AND
                            "leaderboardId" = :leaderboardId
                        """;

        try (NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setInt("totalScore", totalScore);
            stmt.setObject("userId", UUID.fromString(userId));
            stmt.setObject("leaderboardId", UUID.fromString(leaderboardId));

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
                            WHERE (
                                EXISTS (
                                    SELECT 1 FROM "UserTag" ut
                                    WHERE ut."userId" = m."userId"
                                    AND (
                                        (:patina = TRUE AND ut.tag = 'Patina') OR
                                        (:hunter = TRUE AND ut.tag = 'Hunter') OR
                                        (:nyu = TRUE AND ut.tag = 'Nyu') OR
                                        (:baruch = TRUE AND ut.tag = 'Baruch') OR
                                        (:rpi = TRUE AND ut.tag = 'Rpi') OR
                                        (:gwc = TRUE AND ut.tag = 'Gwc') OR
                                        (:sbu = TRUE AND ut.tag = 'Sbu')
                                    )
                                    AND (
                                        -- Any tag is valid for current leaderboard
                                        (l."deletedAt" IS NULL)
                                        OR
                                        -- Tag is only valid for previous leaderboards if it was created before
                                        -- leaderboard started, or during the lifespan of leaderboard.
                                        (l."deletedAt" IS NOT NULL AND ut."createdAt" <= l."deletedAt")
                                    )
                                )
                                OR (:patina = FALSE AND :hunter = FALSE AND :nyu = FALSE AND :baruch = FALSE AND :rpi = FALSE AND :gwc = FALSE AND :sbu = FALSE )
                            )
                            AND
                                (u."discordName" ILIKE :searchQuery OR u."leetcodeUsername" ILIKE :searchQuery)
                        """;
        try (NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setBoolean("patina", options.isPatina());
            stmt.setBoolean("hunter", options.isHunter());
            stmt.setBoolean("nyu", options.isNyu());
            stmt.setBoolean("baruch", options.isBaruch());
            stmt.setBoolean("rpi", options.isRpi());
            stmt.setBoolean("gwc", options.isGwc());
            stmt.setBoolean("sbu", options.isSbu());
            stmt.setString("searchQuery", "%" + options.getQuery() + "%");
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
                            WHERE
                                l.id = :leaderboardId
                            AND (
                                EXISTS (
                                    SELECT 1 FROM "UserTag" ut
                                    WHERE ut."userId" = m."userId"
                                    AND (
                                        (:patina = TRUE AND ut.tag = 'Patina') OR
                                        (:hunter = TRUE AND ut.tag = 'Hunter') OR
                                        (:nyu = TRUE AND ut.tag = 'Nyu') OR
                                        (:baruch = TRUE AND ut.tag = 'Baruch') OR
                                        (:rpi = TRUE AND ut.tag = 'Rpi') OR
                                        (:gwc = TRUE AND ut.tag = 'Gwc') OR
                                        (:sbu = TRUE AND ut.tag = 'Sbu')
                                    )
                                    AND (
                                        -- Any tag is valid for current leaderboard
                                        (l."deletedAt" IS NULL)
                                        OR
                                        -- Tag is only valid for previous leaderboards if it was created before
                                        -- leaderboard started, or during the lifespan of leaderboard.
                                        (l."deletedAt" IS NOT NULL AND ut."createdAt" <= l."deletedAt")
                                    )
                                )
                                OR (:patina = FALSE AND :hunter = FALSE AND :nyu = FALSE AND :baruch = FALSE AND :rpi = FALSE AND :gwc = FALSE AND :sbu = FALSE )
                            )
                            AND
                                (u."discordName" ILIKE :searchQuery OR u."leetcodeUsername" ILIKE :searchQuery)
                        """;
        try (NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setObject("leaderboardId", UUID.fromString(id));
            stmt.setBoolean("patina", options.isPatina());
            stmt.setBoolean("hunter", options.isHunter());
            stmt.setBoolean("nyu", options.isNyu());
            stmt.setBoolean("baruch", options.isBaruch());
            stmt.setBoolean("rpi", options.isRpi());
            stmt.setBoolean("gwc", options.isGwc());
            stmt.setBoolean("sbu", options.isSbu());
            stmt.setString("searchQuery", "%" + options.getQuery() + "%");
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
    public List<Leaderboard> getAllLeaderboardsShallow(final LeaderboardFilterOptions options) {
        ArrayList<Leaderboard> leaderboards = new ArrayList<>();
        String sql = """
                            SELECT
                                id,
                                name,
                                "createdAt",
                                "deletedAt",
                                "shouldExpireBy"
                            FROM "Leaderboard"
                            WHERE name ILIKE :searchQuery
                            ORDER BY
                                "createdAt" DESC
                            LIMIT :pageSize OFFSET :pageNumber
                        """;

        try (NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setString("searchQuery", "%" + options.getQuery() + "%");
            stmt.setInt("pageSize", options.getPageSize());
            stmt.setInt("pageNumber", (options.getPage() - 1) * options.getPageSize());

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
                            (:id, :userId, :leaderboardId)
                        """;

        try (NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            for (var user : users) {
                String userMetaId = UUID.randomUUID().toString();
                stmt.setObject("id", UUID.fromString(userMetaId));
                stmt.setObject("userId", UUID.fromString(user.getId()));
                stmt.setObject("leaderboardId", UUID.fromString(leaderboardId));
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
                                id = :id
                        """;

        try (NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setObject("id", UUID.fromString(id));

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
                                id = :id
                        """;

        try (NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setObject("id", UUID.fromString(id));

            int rowsAffected = stmt.executeUpdate();

            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to enable leaderboard by id", e);
        }
    }
}
