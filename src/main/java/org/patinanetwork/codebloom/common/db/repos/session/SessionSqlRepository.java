package org.patinanetwork.codebloom.common.db.repos.session;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;
import javax.sql.DataSource;
import org.patinanetwork.codebloom.common.db.models.Session;
import org.springframework.stereotype.Component;

@Component
public class SessionSqlRepository implements SessionRepository {

    private DataSource ds;

    public SessionSqlRepository(final DataSource ds) {
        this.ds = ds;
    }

    private Session parseResultSetToSession(final ResultSet resultSet) throws SQLException {
        return Session.builder()
                .id(resultSet.getString("id"))
                .userId(resultSet.getString("userId"))
                .expiresAt(resultSet.getTimestamp("expiresAt").toLocalDateTime())
                .build();
    }

    private void updateSessionWithResultSet(final ResultSet resultSet, final Session session) throws SQLException {
        session.setId(resultSet.getString("id"));
    }

    @Override
    public void createSession(final Session session) {
        String sql = "INSERT INTO \"Session\" (id, \"userId\", \"expiresAt\") VALUES (?, ?, ?) RETURNING \"id\"";
        // Don't want dashes inside of the cookie, so better to just remove it from the
        // ID altogether.
        session.setId(UUID.randomUUID().toString().replace("-", ""));

        try (Connection conn = ds.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, session.getId());
            stmt.setObject(2, UUID.fromString(session.getUserId()));
            stmt.setObject(3, session.getExpiresAt());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    updateSessionWithResultSet(rs, session);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create session", e);
        }
    }

    @Override
    public Session getSessionById(final String id) {
        Session session = null;
        String sql = "SELECT id, \"userId\", \"expiresAt\" FROM \"Session\" WHERE id=?";

        try (Connection conn = ds.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return parseResultSetToSession(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while retrieving session", e);
        }

        return session;
    }

    @Override
    public ArrayList<Session> getSessionsByUserId(final String id) {
        String sql = "SELECT id, \"userId\", \"expiresAt\" FROM \"Session\" WHERE \"userId\"=?";
        ArrayList<Session> sessions = new ArrayList<>();

        try (Connection conn = ds.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, UUID.fromString(id));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    sessions.add(parseResultSetToSession(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while retrieving session", e);
        }

        return sessions;
    }

    @Override
    public boolean deleteSessionById(final String id) {
        String sql = "DELETE FROM \"Session\" WHERE id=?";

        try (Connection conn = ds.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            int rowsAffected = stmt.executeUpdate();

            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error while deleting session", e);
        }
    }
}
