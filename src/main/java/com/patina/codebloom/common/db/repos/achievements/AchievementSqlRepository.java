package com.patina.codebloom.common.db.repos.achievements;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
        var userId = rs.getString("user_id");
        var iconUrl = rs.getString("icon_url");
        var title = rs.getString("title");
        var description = rs.getString("description");
        var isActive = rs.getBoolean("is_active");
        var createdAt = rs.getObject("created_at", OffsetDateTime.class);
        var deletedAt = rs.getObject("deleted_at", OffsetDateTime.class);
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
                    (id, "user_id", "icon_url", "title", "description", is_active, "deleted_at")
                VALUES
                    (:id, :user_id, :icon_url, :title, :description, :is_active, :deleted_at)
                RETURNING
                    created_at
                """;
        try (NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setObject("id", UUID.fromString(achievement.getId()));
            stmt.setObject("user_id", UUID.fromString(achievement.getUserId()));
            stmt.setString("icon_url", achievement.getIconUrl());
            stmt.setString("title", achievement.getTitle());
            stmt.setString("description", achievement.getDescription());
            stmt.setBoolean("is_active", achievement.isActive());
            stmt.setObject("deleted_at", achievement.getDeletedAt());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    achievement.setCreatedAt(rs.getObject("created_at", OffsetDateTime.class));
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
                    "icon_url" = :icon_url,
                    "title" = :title,
                    "description" = :description,
                    is_active = :is_active,
                    "deleted_at" = :deleted_at
                WHERE
                    id = :id
                """;

        try (NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setString("icon_url", achievement.getIconUrl());
            stmt.setString("title", achievement.getTitle());
            stmt.setString("description", achievement.getDescription());
            stmt.setBoolean("is_active", achievement.isActive());
            stmt.setObject("deleted_at", achievement.getDeletedAt());
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
                    "deleted_at" = :deleted_at
                WHERE
                    id = :id
                """;

        try (NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setObject("deleted_at", OffsetDateTime.now());
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
                    "user_id",
                    "icon_url",
                    "title",
                    "description",
                    is_active,
                    "created_at",
                    "deleted_at"
                FROM
                    "Achievement"
                WHERE
                    id = :id
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
                    "user_id",
                    "icon_url",
                    "title",
                    "description",
                    is_active,
                    "created_at",
                    "deleted_at"
                FROM
                    "Achievement"
                WHERE
                    "user_id" = :user_id
                """;

        try (NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setObject("user_id", UUID.fromString(userId));
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