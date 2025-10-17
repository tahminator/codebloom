package com.patina.codebloom.common.db.repos.lobby;

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
import com.patina.codebloom.common.db.models.lobby.Lobby;
import com.patina.codebloom.common.db.models.lobby.LobbyStatus;

@Component
public class LobbySqlRepository implements LobbyRepository {
    private Connection conn;

    public LobbySqlRepository(final DbConnection dbConnection) {
        this.conn = dbConnection.getConn();
    }

    private Lobby parseResultSetToLobby(final ResultSet resultSet) throws SQLException {
        return Lobby.builder()
                        .id(resultSet.getString("id"))
                        .joinCode(resultSet.getString("joinCode"))
                        .status(LobbyStatus.valueOf(resultSet.getString("status")))
                        .createdAt(resultSet.getObject("createdAt", OffsetDateTime.class))
                        .expiresAt(resultSet.getObject("expiresAt", OffsetDateTime.class))
                        .playerCount(resultSet.getInt("playerCount"))
                        .winnerId(resultSet.getString("winnerId"))
                        .build();
    }

    @Override
    public void createLobby(final Lobby lobby) {
        String sql = """
                        INSERT INTO "Lobby"
                            (id, "joinCode", status, "expiresAt", "playerCount", "winnerId")
                        VALUES
                            (:id, :joinCode, :status, :expiresAt, :playerCount, :winnerId)
                        RETURNING
                            "createdAt"
                        """;

        lobby.setId(UUID.randomUUID().toString());

        try (NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setObject("id", UUID.fromString(lobby.getId()));
            stmt.setString("joinCode", lobby.getJoinCode());
            stmt.setObject("status", lobby.getStatus().name(), java.sql.Types.OTHER);
            stmt.setObject("expiresAt", lobby.getExpiresAt());
            stmt.setInt("playerCount", lobby.getPlayerCount());
            stmt.setObject("winnerId", lobby.getWinnerId() != null ? UUID.fromString(lobby.getWinnerId()) : null);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    lobby.setCreatedAt(rs.getObject("createdAt", OffsetDateTime.class));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create lobby", e);
        }
    }

    @Override
    public Lobby findLobbyById(final String id) {
        String sql = """
                        SELECT
                            id,
                            "joinCode",
                            status,
                            "createdAt",
                            "expiresAt",
                            "playerCount",
                            "winnerId"
                        FROM
                            "Lobby"
                        WHERE
                            id = :id
                        """;

        try (NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setObject("id", UUID.fromString(id));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return parseResultSetToLobby(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find lobby by id", e);
        }

        return null;
    }

    @Override
    public Lobby findLobbyByJoinCode(final String joinCode) {
        String sql = """
                        SELECT
                            id,
                            "joinCode",
                            status,
                            "createdAt",
                            "expiresAt",
                            "playerCount",
                            "winnerId"
                        FROM
                            "Lobby"
                        WHERE
                            "joinCode" = :joinCode
                        """;

        try (NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setString("joinCode", joinCode);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return parseResultSetToLobby(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find lobby by join code", e);
        }

        return null;
    }

    @Override
    public List<Lobby> findLobbiesByStatus(final LobbyStatus status) {
        List<Lobby> result = new ArrayList<>();
        String sql = """
                        SELECT
                            id,
                            "joinCode",
                            status,
                            "createdAt",
                            "expiresAt",
                            "playerCount",
                            "winnerId"
                        FROM
                            "Lobby"
                        WHERE
                            status = :status
                        ORDER BY
                            "createdAt" DESC
                        """;

        try (NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setObject("status", status.name(), java.sql.Types.OTHER);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    result.add(parseResultSetToLobby(rs));
                }
            }

            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find lobbies by status", e);
        }
    }

    @Override
    public List<Lobby> findAvailableLobbies() {
        List<Lobby> result = new ArrayList<>();
        String sql = """
                        SELECT
                            id,
                            "joinCode",
                            status,
                            "createdAt",
                            "expiresAt",
                            "playerCount",
                            "winnerId"
                        FROM
                            "Lobby"
                        WHERE
                            status = :status
                            AND "expiresAt" > NOW()
                        ORDER BY
                            "createdAt" DESC
                        """;

        try (NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setObject("status", LobbyStatus.AVAILABLE.name(), java.sql.Types.OTHER);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    result.add(parseResultSetToLobby(rs));
                }
            }

            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find available lobbies", e);
        }
    }

    @Override
    public boolean updateLobby(final Lobby lobby) {
        String sql = """
                        UPDATE "Lobby"
                        SET
                            status = :status,
                            "expiresAt" = :expiresAt,
                            "playerCount" = :playerCount
                        WHERE
                            id = :id
                        """;

        try (NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setObject("status", lobby.getStatus().name(), java.sql.Types.OTHER);
            stmt.setObject("expiresAt", lobby.getExpiresAt());
            stmt.setInt("playerCount", lobby.getPlayerCount());
            stmt.setObject("id", UUID.fromString(lobby.getId()));

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected == 1;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update lobby", e);
        }
    }

    @Override
    public boolean deleteLobbyById(final String id) {
        String sql = """
                        DELETE FROM "Lobby"
                        WHERE id = :id
                        """;

        try (NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setObject("id", UUID.fromString(id));

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected == 1;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete lobby", e);
        }
    }
}