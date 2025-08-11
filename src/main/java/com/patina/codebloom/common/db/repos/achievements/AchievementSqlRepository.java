package com.patina.codebloom.common.db.repos.achievements;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Optional;
import java.sql.Timestamp;


import org.springframework.stereotype.Component;

import com.patina.codebloom.common.db.DbConnection;
import com.patina.codebloom.common.db.helper.NamedPreparedStatement;
import com.patina.codebloom.common.db.models.achievements.Achievement;

@Component
public class AchievementSqlRepository implements AchievementRepository {
    private Connection conn;

    public AchievementSqlRepository(final DbConnection dbConnection) {
        this.conn = dbConnection.getConn();
    }

    private Achievement parseResultSetToAchievement(final ResultSet rs) throws SQLException {
        var id = rs.getString("id");
        var userId = rs.getString("userId");
        var iconUrl = rs.getString("iconUrl");
        var title = rs.getString("title");
        var description = rs.getString("description");
        var isActive = rs.getBoolean("isActive");
        var createdAt = rs.getTimestamp("createdAt").toLocalDateTime();
        LocalDateTime deletedAt = Optional.ofNullable(rs.getTimestamp("deletedAt"))
            .map(Timestamp::toLocalDateTime)
                .orElse(null);
        return Achievement.builder()
                .id(id)
                .userId(userId)
                .iconUrl(iconUrl)
                .title(title)
                .description(description)
                .isActive(isActive)
                .createdAt(createdAt)
                .deletedAt(deletedAt)
                .build();
    }

    @Override
    public void createAchievement(final Achievement achievement) {
        achievement.setId(UUID.randomUUID().toString());
        String sql = """
                INSERT INTO "Achievement"
                    (id, "userId", "iconUrl", "title", "description", "isActive", "deletedAt")
                VALUES
                    (:id, :userId, :iconUrl, :title, :description, :isActive, :deletedAt)
                RETURNING
                    "createdAt"
                """;
        try (NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setObject("id", UUID.fromString(achievement.getId()));
            stmt.setObject("userId", UUID.fromString(achievement.getUserId()));
            stmt.setString("iconUrl", achievement.getIconUrl());
            stmt.setString("title", achievement.getTitle());
            stmt.setString("description", achievement.getDescription());
            stmt.setBoolean("isActive", achievement.isActive());
            stmt.setObject("deletedAt", achievement.getDeletedAt());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    achievement.setCreatedAt(rs.getTimestamp("createdAt").toLocalDateTime());
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create achievement", e);
        }
    }

    @Override
    public Achievement updateAchievement(final Achievement achievement) {
        String sql = """
                UPDATE
                    "Achievement"
                SET
                    "iconUrl" = :iconUrl,
                    "title" = :title,
                    "description" = :description,
                    "isActive" = :isActive,
                    "deletedAt" = :deletedAt
                WHERE
                    id = :id
                """;

        try (NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setString("iconUrl", achievement.getIconUrl());
            stmt.setString("title", achievement.getTitle());
            stmt.setString("description", achievement.getDescription());
            stmt.setBoolean("isActive", achievement.isActive());
            stmt.setObject("deletedAt", achievement.getDeletedAt());
            stmt.setObject("id", UUID.fromString(achievement.getId()));

            stmt.executeUpdate();
            return getAchievementById(achievement.getId());
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update achievement", e);
        }
    }

    @Override
    public boolean deleteAchievementById(final String id) {
        String sql = """
                UPDATE
                    "Achievement"
                SET
                    "deletedAt" = :deletedAt
                WHERE
                    id = :id
                """;

        try (NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setObject("deletedAt", LocalDateTime.now());
            stmt.setObject("id", UUID.fromString(id));

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete achievement by ID", e);
        }
    }

    @Override
    public Achievement getAchievementById(final String id) {
        String sql = """
                SELECT
                    id,
                    "userId",
                    "iconUrl",
                    "title",
                    "description",
                    "isActive",
                    "createdAt",
                    "deletedAt"
                FROM
                    "Achievement"
                WHERE
                    id = :id
                    AND "deletedAt" IS NULL
                """;

        try (NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setObject("id", UUID.fromString(id));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return parseResultSetToAchievement(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get achievement by ID", e);
        }

        return null;
    }

    @Override
    public List<Achievement> getAchievementsByUserId(final String userId) {
        List<Achievement> achievements = new ArrayList<>();
        String sql = """
                SELECT
                    id,
                    "userId",
                    "iconUrl",
                    "title",
                    "description",
                    "isActive",
                    "createdAt",
                    "deletedAt"
                FROM
                    "Achievement"
                WHERE
                    "userId" = :userId
                    AND "deletedAt" IS NULL
                """;

        try (NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setObject("userId", UUID.fromString(userId));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    var achievement = parseResultSetToAchievement(rs);
                    achievements.add(achievement);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get achievements by user ID", e);
        }

        return achievements;
    }
}