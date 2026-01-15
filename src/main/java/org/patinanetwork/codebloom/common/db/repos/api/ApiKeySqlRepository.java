package org.patinanetwork.codebloom.common.db.repos.api;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.sql.DataSource;
import org.patinanetwork.codebloom.common.db.helper.NamedPreparedStatement;
import org.patinanetwork.codebloom.common.db.models.api.ApiKey;
import org.patinanetwork.codebloom.common.time.StandardizedLocalDateTime;
import org.springframework.stereotype.Component;

@Component
public class ApiKeySqlRepository implements ApiKeyRepository {

    private final DataSource ds;

    public ApiKeySqlRepository(final DataSource ds) {
        this.ds = ds;
    }

    private ApiKey parseResultSetToApiKey(final ResultSet resultSet) throws SQLException {
        return ApiKey.builder()
                .id(resultSet.getString("id"))
                .apiKey(resultSet.getString("apiKeyHash"))
                .expiresAt(Optional.ofNullable(resultSet.getTimestamp("expiresAt"))
                        .map(Timestamp::toLocalDateTime)
                        .orElse(null))
                .createdAt(resultSet.getTimestamp("createdAt").toLocalDateTime())
                .updatedAt(resultSet.getTimestamp("updatedAt").toLocalDateTime())
                .updatedBy(resultSet.getString("updatedBy"))
                .build();
    }

    @Override
    public ApiKey getApiKeyById(final String id) {
        String sql = """
            SELECT
                id,
                "apiKeyHash",
                "expiresAt",
                "createdAt",
                "updatedAt",
                "updatedBy"
            FROM
                "ApiKey"
            WHERE
                id = :id
            """;

        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setObject("id", UUID.fromString(id));
            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    return parseResultSetToApiKey(resultSet);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch apiKeyHash by ID", e);
        }

        return null;
    }

    @Override
    public ApiKey getApiKeyByHash(final String hash) {
        String sql = """
            SELECT
                id,
                "apiKeyHash",
                "expiresAt",
                "createdAt",
                "updatedAt",
                "updatedBy"
            FROM
                "ApiKey"
            WHERE
                "apiKeyHash" = :hash
            """;

        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setString("hash", hash);
            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    return parseResultSetToApiKey(resultSet);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch apiKeyHash by hash", e);
        }

        return null;
    }

    @Override
    public List<ApiKey> getAllApiKeys() {
        String sql = """
            SELECT
                id,
                "apiKeyHash",
                "expiresAt",
                "createdAt",
                "updatedAt",
                "updatedBy"
            FROM
                "ApiKey"
            """;

        final List<ApiKey> results = new ArrayList<>();

        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql);
                ResultSet resultSet = stmt.executeQuery()) {
            while (resultSet.next()) {
                results.add(parseResultSetToApiKey(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch all ApiKeys", e);
        }

        return results;
    }

    @Override
    public void createApiKey(final ApiKey apiKey) {
        final String sql = """
            INSERT INTO "ApiKey" (
                id,
                "apiKeyHash",
                "expiresAt",
                "updatedBy"
            )
            VALUES (
                :id,
                :apiKeyHash,
                :expiresAt,
                :updatedBy
            )
            """;

        final UUID id = UUID.fromString(apiKey.getId());
        final UUID updatedBy = UUID.fromString(apiKey.getUpdatedBy());

        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setObject("id", id);
            stmt.setString("apiKeyHash", apiKey.getApiKey());
            stmt.setObject("expiresAt", apiKey.getExpiresAt());
            stmt.setObject("updatedBy", updatedBy);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create ApiKey", e);
        }
    }

    @Override
    public boolean updateApiKeyById(final ApiKey apiKey) {
        final String sql = """
            UPDATE
                "ApiKey"
            SET
                "apiKeyHash"  = :apiKeyHash,
                "expiresAt" = :expiresAt,
                "updatedAt" = :updatedAt,
                "updatedBy" = :updatedBy
            WHERE
                "id" = :id
            """;
        UUID id = UUID.fromString(apiKey.getId());
        var localTime = StandardizedLocalDateTime.now();
        apiKey.setUpdatedAt(localTime);

        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setObject("id", id);
            stmt.setString("apiKeyHash", apiKey.getApiKey());
            stmt.setObject("expiresAt", apiKey.getExpiresAt());
            stmt.setObject("updatedAt", apiKey.getUpdatedAt());
            stmt.setObject("updatedBy", UUID.fromString(apiKey.getUpdatedBy()));

            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update ApiKey by ID", e);
        }
    }

    @Override
    public boolean deleteApiKeyById(final String id) {
        final String sql = """
            DELETE FROM
                "ApiKey"
            WHERE
                "id" = :id
            """;

        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setObject("id", UUID.fromString(id));

            final int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete ApiKey by ID", e);
        }
    }

    @Override
    public boolean deleteApiKeyByHash(final String hash) {
        final String sql = """
            DELETE FROM
                "ApiKey"
            WHERE
                "apiKeyHash" = :hash
            """;

        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setString("hash", hash);

            final int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete ApiKey by hash", e);
        }
    }
}
