package com.patina.codebloom.common.db.repos.achivenents;

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
public class AchievementSqlRepository {
    private final Connection conn;

    public AchievementSqlRepository(final DbConnection dbConnection) {
        this.conn = dbConnection.getConn();
    }

    public void createAchievement(final Achievement achievement) {
        String sql = """
                            INSERT INTO achievement
                                (id, user_id, icon_url, title, description, is_active, created_at, deleted_at)
                            VALUES
                               (?, ?, ?, ?, ?, ?,?, ?)
                            RETURNING created_at
                        """;
        achievement.setId(UUID.randomUUID().toString());
        try (NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setObject("id", UUID.fromString(achievement.getId()));
            stmt.setObject("user_id", UUID.fromString(achievement.getUserId()));
            stmt.setString("icon_url", achievement.getIconUrl());
            stmt.setString("title", achievement.getTitle());
            stmt.setString("description", achievement.getDescription());
            stmt.setBoolean("is_active", achievement.isActive());
            stmt.setObject("created_at", achievement.getCreatedAt());
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

    public boolean updateAchievement(final Achievement achievement) {
        String sql = """
                            UPDATE achievement
                            SET
                                icon_url = ?,
                                title = ?,
                                description = ?,
                                is_active = ?,
                                deleted_at = ?,
                            WHERE
                                id = ?
                        """;
        try (NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setString("icon_url", achievement.getIconUrl());
            stmt.setString("title", achievement.getTitle());
            stmt.setString("description", achievement.getDescription());
            stmt.setBoolean("is_active", achievement.isActive());
            stmt.setObject("deleted_at", achievement.getDeletedAt());
            stmt.setObject("id", UUID.fromString(achievement.getId()));
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update achievement", e);
        }
    }

    public Achievement getAchievementById(final String id) {
        String sql = """
                            SELECT
                                id,
                                user_id,
                                icon_url,
                                title,
                                description,
                                is_active,
                                created_at,
                                deleted_at
                            FROM Achievement
                            WHERE id = ?
                        """;
        try (NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setObject("id", UUID.fromString(id));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get achievement by id", e);
        }
        return null;
    }

    public List<Achievement> getAchievementsByUserId(final String userId) {
        String sql = """
                            SELECT
                                id,
                                user_id,
                                icon_url,
                                title,
                                description,
                                is_active,
                                created_at,
                                deleted_at
                            FROM Achievement a
                            WHERE a.id = ?
                        """;
        List<Achievement> achievements = new ArrayList<>();
        try (NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setObject("user_id", UUID.fromString(userId));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    achievements.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get achievements by user id", e);
        }
        return achievements;
    }

    public boolean deleteAchievementById(final String id) {
               String sql = """
                            DELETE FROM
                                "Achievement"
                            WHERE
                                id = ?
                        """;
        try (NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setObject("id", UUID.fromString(id));
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete achievement by id", e);
        }
    }

    private Achievement mapRow(ResultSet rs) throws SQLException {
        return Achievement.builder()
                        .id(rs.getObject("id").toString())
                        .userId(rs.getObject("user_id").toString())
                        .iconUrl(rs.getString("icon_url"))
                        .title(rs.getString("title"))
                        .description(rs.getString("description"))
                        .isActive(rs.getBoolean("is_active"))
                        .createdAt(rs.getObject("created_at", OffsetDateTime.class))
                        .deletedAt(rs.getObject("deleted_at", OffsetDateTime.class))
                        .build();
    }
}
