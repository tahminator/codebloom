package com.patina.codebloom.common.db.repos.session;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.patina.codebloom.common.db.DbConnection;
import com.patina.codebloom.common.db.models.Session;

@Component
public class SessionSqlRepository implements SessionRepository {
    DbConnection dbConnection;
    Connection conn;

    public SessionSqlRepository(DbConnection dbConnection) {
        this.dbConnection = dbConnection;
        this.conn = dbConnection.getConn();
    }

    @Override
    public Session createSession(Session session) {
        String sql = "INSERT INTO \"Session\" (id, \"userId\", \"expiresAt\") VALUES (?, ?, ?)";
        // Don't want dashes inside of the cookie, so better to just remove it from the
        // ID altogether.
        session.setId(UUID.randomUUID().toString().replace("-", ""));

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, session.getId());
            stmt.setObject(2, UUID.fromString(session.getUserId()));
            stmt.setObject(3, session.getExpiresAt());

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                return getSessionById(session.getId());
            } else {
                throw new RuntimeException("Something went wrong.");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to create session", e);
        }
    }

    @Override
    public Session getSessionById(String id) {
        Session session = null;
        String sql = "SELECT id, \"userId\", \"expiresAt\" FROM \"Session\" WHERE id=?";

        try (PreparedStatement stmt = conn.prepareStatement(sql);) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    var sessionId = rs.getString("id");
                    var userId = rs.getString("userId");
                    var expiresAt = rs.getTimestamp("expiresAt").toLocalDateTime();
                    session = new Session(sessionId, userId, expiresAt);
                    return session;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while retrieving session", e);
        }

        return session;
    }

    @Override
    public ArrayList<Session> getSessionsByUserId(String id) {
        String sql = "SELECT id, \"userId\", \"expiresAt\" FROM \"Session\" WHERE \"userId\"=?";
        ArrayList<Session> sessions = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, UUID.fromString(id));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    var sessionId = rs.getString("id");
                    var userId = rs.getString("userId");
                    var expiresAt = rs.getTimestamp("expiresAt").toLocalDateTime();
                    sessions.add(new Session(sessionId, userId, expiresAt));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while retrieving session", e);
        }

        return sessions;
    }

    @Override
    public boolean deleteSessionById(String id) {
        String sql = "DELETE FROM \"Session\" WHERE id=?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            int rowsAffected = stmt.executeUpdate();

            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error while deleting session", e);
        }

    }
}
