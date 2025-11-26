package com.patina.codebloom.common.db.repos.auth;

import com.patina.codebloom.common.db.DbConnection;
import com.patina.codebloom.common.db.helper.NamedPreparedStatement;
import com.patina.codebloom.common.db.models.auth.Auth;
import com.patina.codebloom.common.time.StandardizedOffsetDateTime;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class AuthSqlRepository implements AuthRepository {

    private Connection conn;

    public AuthSqlRepository(final DbConnection dbConnection) {
        this.conn = dbConnection.getConn();
    }

    private Auth parseResultSetToAuth(final ResultSet rs) throws SQLException {
        return Auth.builder()
            .id(rs.getString("id"))
            .token(rs.getString("token"))
            .csrf(rs.getString("csrf"))
            .createdAt(
                StandardizedOffsetDateTime.normalize(
                    rs.getObject("createdAt", OffsetDateTime.class)
                )
            )
            .build();
    }

    @Override
    public void createAuth(final Auth auth) {
        String sql = """
            INSERT INTO "Auth"
                (id, token, csrf)
            VALUES
                (:id, :token, :csrf)
            RETURNING
                "createdAt"
            """;
        auth.setId(UUID.randomUUID().toString());

        try (
            NamedPreparedStatement stmt = NamedPreparedStatement.create(
                conn,
                sql
            )
        ) {
            stmt.setObject("id", UUID.fromString(auth.getId()));
            stmt.setString("token", auth.getToken());
            stmt.setString("csrf", auth.getCsrf());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    auth.setCreatedAt(
                        StandardizedOffsetDateTime.normalize(
                            rs.getObject("createdAt", OffsetDateTime.class)
                        )
                    );
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
                token = :token,
                csrf = :csrf
            WHERE
                id = :id
            """;
        try (
            NamedPreparedStatement stmt = NamedPreparedStatement.create(
                conn,
                sql
            )
        ) {
            stmt.setObject("id", UUID.fromString(auth.getId()));
            stmt.setString("token", auth.getToken());
            stmt.setString("csrf", auth.getCsrf());

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
                id, token, csrf, "createdAt"
            FROM "Auth"
            WHERE
                id = :id;
            """;
        try (
            NamedPreparedStatement stmt = NamedPreparedStatement.create(
                conn,
                sql
            )
        ) {
            stmt.setObject("id", UUID.fromString(inputtedId));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return parseResultSetToAuth(rs);
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
                id, token, csrf, "createdAt"
            FROM "Auth"
            ORDER BY "createdAt" DESC
            LIMIT 1
            """;
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return parseResultSetToAuth(rs);
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
                    id = :id
            """;

        try (
            NamedPreparedStatement stmt = NamedPreparedStatement.create(
                conn,
                sql
            )
        ) {
            stmt.setObject("id", UUID.fromString(id));

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error while deleting auth by ID", e);
        }
    }
}
