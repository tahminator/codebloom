package com.patina.codebloom.common.db.repos.weekly;

import com.patina.codebloom.common.db.DbConnection;
import com.patina.codebloom.common.db.models.weekly.WeeklyMessage;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class WeeklyMessageSqlRepository implements WeeklyMessageRepository {

    private Connection conn;

    public WeeklyMessageSqlRepository(final DbConnection dbConnection) {
        this.conn = dbConnection.getConn();
    }

    private WeeklyMessage parseResultSetToWeeklyMessage(final ResultSet resultSet) throws SQLException {
        return WeeklyMessage.builder()
                .id(resultSet.getString("id"))
                .createdAt(resultSet.getTimestamp("createdAt").toLocalDateTime())
                .build();
    }

    private void updateWeeklyMessageWithResultSet(final WeeklyMessage message, final ResultSet resultSet)
            throws SQLException {
        message.setId(resultSet.getString("id"));
        message.setCreatedAt(resultSet.getTimestamp("createdAt").toLocalDateTime());
    }

    @Override
    public WeeklyMessage getLatestWeeklyMessage() {
        String sql = """
            SELECT
                id,
                "createdAt"
            FROM
                "WeeklyMessage"
            ORDER BY
                "createdAt" DESC
            LIMIT 1
                                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return parseResultSetToWeeklyMessage(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while retrieving latest weekly message", e);
        }

        return null;
    }

    @Override
    public WeeklyMessage getWeeklyMessageById(final String id) {
        String sql = """
            SELECT
                id,
                "createdAt"
            FROM
                "WeeklyMessage"
            WHERE
                id = ?
            LIMIT 1
                                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, UUID.fromString(id));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return parseResultSetToWeeklyMessage(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get weekly message by ID", e);
        }

        return null;
    }

    @Override
    public boolean createLatestWeeklyMessage(final WeeklyMessage message) {
        String sql = """
                INSERT INTO "WeeklyMessage"
                    (id)
                VALUES
                    (?)
                RETURNING
                    id, "createdAt"
            """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, UUID.randomUUID());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    updateWeeklyMessageWithResultSet(message, rs);
                    return true;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create new latest weekly message", e);
        }

        return false;
    }

    @Override
    public boolean createLatestWeeklyMessage() {
        String sql = """
                INSERT INTO "WeeklyMessage"
                    (id)
                VALUES
                    (?)
            """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, UUID.randomUUID());

            int rowsAffected = stmt.executeUpdate();

            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create new latest weekly message", e);
        }
    }

    @Override
    public boolean deleteWeeklyMessageById(final String id) {
        String sql = """
            DELETE FROM
                "WeeklyMessage"
            WHERE
                id = ?
            """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, UUID.fromString(id));
            int rowsAffected = stmt.executeUpdate();

            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete weekly message", e);
        }
    }

    @Override
    public boolean deleteLatestWeeklyMessage() {
        String sql = """
            WITH to_delete AS (
                SELECT *
                FROM "WeeklyMessage"
                ORDER BY "createdAt" DESC
                LIMIT 1
            )
            DELETE FROM "WeeklyMessage" wm
            USING to_delete td
            WHERE wm.id = td.id
            """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            int rowsAffected = stmt.executeUpdate();

            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete weekly message", e);
        }
    }
}
