package com.patina.codebloom.common.db.repos.auth;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.patina.codebloom.common.db.DbConnection;
import com.patina.codebloom.common.db.models.auth.Auth;

@Component
public class AuthSqlRepository implements AuthRepository {
    private Connection conn;

    public AuthSqlRepository(final DbConnection dbConnection) {
        this.conn = dbConnection.getConn();
    }

    @Override
    public void createAuth(final Auth auth) {
        String sql = """
                        INSERT INTO "Auth"
                            (id, token)
                        VALUES
                            (?, ?)
                        RETURNING
                            "createdAt"
                        """;
        auth.setId(UUID.randomUUID().toString());

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, UUID.fromString(auth.getId()));
            stmt.setString(2, auth.getToken());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    auth.setCreatedAt(rs.getTimestamp("createdAt").toLocalDateTime());
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create new auth", e);
        }
    }

    @Override
    public boolean updateAuthById(final Auth auth) {
        String sql = """
                UPDATE "Auth"
                SET
                    token = ?
                WHERE
                    id = ?
                """;
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, auth.getToken());
            stmt.setObject(2, UUID.fromString(auth.getId()));

            int rowsAffected = stmt.executeUpdate();

            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update auth", e);
        }
    }

    @Override
    public Auth getAuthById(final String inputtedId) {
        String sql = """
                SELECT
                    id, token, "createdAt"
                FROM "Auth"
                WHERE
                    id = ?
                """;
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, UUID.fromString(inputtedId));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    var id = rs.getString("id");
                    var token = rs.getString("token");
                    var createdAt = rs.getTimestamp("createdAt").toLocalDateTime();
                    return new Auth(id, token, createdAt);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get auth by id", e);
        }

        return null;
    }

    @Override
    public Auth getMostRecentAuth() {
        String sql = """
                SELECT
                    id, token, "createdAt"
                FROM "Auth"
                ORDER BY "createdAt" DESC
                LIMIT 1
                """;
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    var id = rs.getString("id");
                    var token = rs.getString("token");
                    var createdAt = rs.getTimestamp("createdAt").toLocalDateTime();
                    return new Auth(id, token, createdAt);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get most recent auth", e);
        }

        return null;
    }

    @Override
    public boolean deleteAuthById(final String id) {
        String sql = """
                            DELETE FROM
                                "Auth"
                            WHERE
                                id = ?
                        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, UUID.fromString(id));

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error while deleting auth by ID", e);
        }
    }

}
