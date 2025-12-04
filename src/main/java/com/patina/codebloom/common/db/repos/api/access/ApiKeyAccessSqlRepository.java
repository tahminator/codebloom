package com.patina.codebloom.common.db.repos.api.access;

import com.patina.codebloom.common.db.DbConnection;
import com.patina.codebloom.common.db.helper.NamedPreparedStatement;
import com.patina.codebloom.common.db.models.api.ApiKeyAccessEnum;
import com.patina.codebloom.common.db.models.api.access.ApiKeyAccess;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class ApiKeyAccessSqlRepository implements ApiKeyAccessRepository {

    private Connection conn;

    public ApiKeyAccessSqlRepository(final DbConnection dbConnection) {
        this.conn = dbConnection.getConn();
    }

    private ApiKeyAccess parseResultSetToApiKeyAccess(final ResultSet rs) throws SQLException {
        return ApiKeyAccess.builder()
                .id(rs.getString("id"))
                .apiKeyId(rs.getString("apiKeyId"))
                .access(ApiKeyAccessEnum.valueOf(rs.getString("access")))
                .build();
    }

    @Override
    public ApiKeyAccess getApiKeyAccessById(final String id) {
        String sql = """
            SELECT
                id,
                "apiKeyId",
                access
            FROM
                "ApiKeyAccess"
            WHERE
                id = :id
            """;

        try (NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setObject("id", UUID.fromString(id));
            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    return parseResultSetToApiKeyAccess(resultSet);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch apiKeyAccess by ID", e);
        }

        return null;
    }

    @Override
    public ArrayList<ApiKeyAccess> getApiKeyAccessesByApiKeyId(final String apiKeyId) {
        String sql = """
            SELECT
                id,
                "apiKeyId",
                access
            FROM
                "ApiKeyAccess"
            WHERE
                "apiKeyId" = :apiKeyId
            """;

        final ArrayList<ApiKeyAccess> results = new java.util.ArrayList<>();
        try (NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setObject("apiKeyId", java.util.UUID.fromString(apiKeyId));
            try (ResultSet resultSet = stmt.executeQuery()) {
                while (resultSet.next()) {
                    results.add(parseResultSetToApiKeyAccess(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch apiKeyAccesses by apiKeyId", e);
        }
        return results;
    }

    @Override
    public void createApiKeyAccess(final ApiKeyAccess apiKeyAccess) {
        String sql = """
            INSERT INTO
                "ApiKeyAccess" (id, "apiKeyId", access)
            VALUES
                (:id, :apiKeyId, :access)
            """;

        try (NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setObject("id", java.util.UUID.fromString(apiKeyAccess.getId()));
            stmt.setObject("apiKeyId", java.util.UUID.fromString(apiKeyAccess.getApiKeyId()));
            stmt.setObject("access", apiKeyAccess.getAccess().name(), java.sql.Types.OTHER);

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create ApiKeyAccess", e);
        }
    }

    @Override
    public boolean updateApiKeyAccessById(final ApiKeyAccess apiKeyAccess) {
        String sql = """
            UPDATE
                "ApiKeyAccess"
            SET
                "apiKeyId" = :apiKeyId,
                access = :access
            WHERE
                id = :id
            """;

        try (NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setObject("id", UUID.fromString(apiKeyAccess.getId()));
            stmt.setObject("apiKeyId", UUID.fromString(apiKeyAccess.getApiKeyId()));
            stmt.setObject("access", apiKeyAccess.getAccess().name(), java.sql.Types.OTHER);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update ApiKeyAccess by ID", e);
        }
    }

    @Override
    public boolean deleteApiKeyAccessesByApiKeyId(final String apiKeyId) {
        String sql = """
            DELETE FROM
                "ApiKeyAccess"
            WHERE
                "apiKeyId" = :apiKeyId
            """;

        try (NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setObject("apiKeyId", UUID.fromString(apiKeyId));

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete ApiKeyAccess rows by apiKeyId", e);
        }
    }

    @Override
    public boolean deleteApiKeyAccessById(final String id) {
        String sql = """
            DELETE FROM
                "ApiKeyAccess"
            WHERE
                id = :id
            """;

        try (NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setObject("id", UUID.fromString(id));

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete ApiKeyAccess by ID: " + id, e);
        }
    }
}
