package com.patina.codebloom.common.db.repos.announcement;

import com.patina.codebloom.common.db.DbConnection;
import com.patina.codebloom.common.db.helper.NamedPreparedStatement;
import com.patina.codebloom.common.db.models.announcement.Announcement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class AnnouncementSqlRepository implements AnnouncementRepository {

    private Connection conn;

    public AnnouncementSqlRepository(final DbConnection dbConnection) {
        this.conn = dbConnection.getConn();
    }

    private Announcement parseResultSetToTag(final ResultSet resultSet)
        throws SQLException {
        return Announcement.builder()
            .id(resultSet.getString("id"))
            .createdAt(resultSet.getObject("createdAt", OffsetDateTime.class))
            .expiresAt(resultSet.getObject("expiresAt", OffsetDateTime.class))
            .showTimer(resultSet.getBoolean("showTimer"))
            .message(resultSet.getString("message"))
            .build();
    }

    @Override
    public List<Announcement> getAllAnnouncements() {
        List<Announcement> result = new ArrayList<>();
        String sql = """
                SELECT
                    id,
                    "createdAt",
                    "expiresAt",
                    "showTimer",
                    message
                FROM
                    "Announcement"
                ORDER BY
                    "createdAt" ASC
            """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    result.add(parseResultSetToTag(rs));
                }
            }

            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get all announcements", e);
        }
    }

    @Override
    public Announcement getAnnouncementById(final String id) {
        String sql = """
                                SELECT
                                    id,
                                    "createdAt",
                                    "expiresAt",
                                    "showTimer",
                                    message
                                FROM
                                    "Announcement"
                                WHERE
                                    id = ?
            """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, UUID.fromString(id));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return parseResultSetToTag(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get announcement by id", e);
        }

        return null;
    }

    @Override
    public Announcement getRecentAnnouncement() {
        String sql = """
                                SELECT
                                    id,
                                    "createdAt",
                                    "expiresAt",
                                    "showTimer",
                                    message
                                FROM
                                    "Announcement"
                                ORDER BY
                                    "createdAt" DESC
                                LIMIT 1
            """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return parseResultSetToTag(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(
                "Failed to fetch most recent announcement",
                e
            );
        }

        return null;
    }

    @Override
    public boolean createAnnouncement(final Announcement announcement) {
        String sql = """
               INSERT INTO "Announcement"
                   (id, "expiresAt", "showTimer", "message")
               VALUES
                   (?, ?, ?, ?)
                RETURNING
                    id, "createdAt"
            """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, UUID.randomUUID());
            stmt.setObject(2, announcement.getExpiresAt());
            stmt.setBoolean(3, announcement.isShowTimer());
            stmt.setString(4, announcement.getMessage());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    announcement.setId(rs.getString("id"));
                    announcement.setCreatedAt(
                        rs.getObject("createdAt", OffsetDateTime.class)
                    );
                    return true;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create announcement", e);
        }

        return false;
    }

    @Override
    public boolean deleteAnnouncementById(final String id) {
        String sql = """
                DELETE FROM
                    "Announcement"
                WHERE
                    id = ?
            """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, UUID.fromString(id));
            int rowsAffected = stmt.executeUpdate();

            return rowsAffected == 1;
        } catch (SQLException e) {
            throw new RuntimeException(
                "Failed to delete announcement by ID",
                e
            );
        }
    }

    @Override
    public boolean updateAnnouncement(final Announcement announcement) {
        String sql = """
            UPDATE
                "Announcement"
            SET
                "expiresAt" = :expiresAt,
                "showTimer" = :showTimer,
                "message"  = :message
            WHERE
                id = :id
            """;
        try (
            NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)
        ) {
            stmt.setObject("expiresAt", announcement.getExpiresAt());
            stmt.setBoolean("showTimer", announcement.isShowTimer());
            stmt.setString("message", announcement.getMessage());
            stmt.setObject("id", UUID.fromString(announcement.getId()));
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update announcement", e);
        }
    }
}
