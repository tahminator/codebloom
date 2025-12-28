package com.patina.codebloom.common.db.repos.lobby;

import com.patina.codebloom.common.db.helper.NamedPreparedStatement;
import com.patina.codebloom.common.db.models.lobby.Lobby;
import com.patina.codebloom.common.db.models.lobby.LobbyStatus;
import com.patina.codebloom.common.time.StandardizedOffsetDateTime;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.sql.DataSource;
import org.springframework.stereotype.Component;

@Component
public class LobbySqlRepository implements LobbyRepository {

    private final DataSource ds;

    public LobbySqlRepository(final DataSource ds) {
        this.ds = ds;
    }

    private Lobby parseResultSetToLobby(final ResultSet resultSet) throws SQLException {
        return Lobby.builder()
                .id(resultSet.getString("id"))
                .joinCode(resultSet.getString("joinCode"))
                .status(LobbyStatus.valueOf(resultSet.getString("status")))
                .createdAt(resultSet.getObject("createdAt", OffsetDateTime.class))
                .expiresAt(resultSet.getObject("expiresAt", OffsetDateTime.class))
                .playerCount(resultSet.getInt("playerCount"))
                .winnerId(Optional.ofNullable(resultSet.getString("winnerId")))
                .isTie(resultSet.getBoolean("isTie"))
                .build();
    }

    @Override
    public void createLobby(final Lobby lobby) {
        String sql = """
            INSERT INTO "Lobby"
                (id, "joinCode", status, "expiresAt", "playerCount", "winnerId", "isTie")
            VALUES
                (:id, :joinCode, :status, :expiresAt, :playerCount, :winnerId, :isTie)
            RETURNING
                "createdAt"
            """;

        lobby.setId(UUID.randomUUID().toString());

        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setObject("id", UUID.fromString(lobby.getId()));
            stmt.setString("joinCode", lobby.getJoinCode());
            stmt.setObject("status", lobby.getStatus().name(), java.sql.Types.OTHER);
            stmt.setObject("expiresAt", StandardizedOffsetDateTime.normalize(lobby.getExpiresAt()));
            stmt.setInt("playerCount", lobby.getPlayerCount());
            stmt.setObject("winnerId", lobby.getWinnerId().map(UUID::fromString).orElse(null));
            stmt.setBoolean("isTie", lobby.isTie());

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
    public Optional<Lobby> findLobbyById(final String id) {
        String sql = """
            SELECT
                id,
                "joinCode",
                status,
                "createdAt",
                "expiresAt",
                "playerCount",
                "winnerId",
                "isTie"
            FROM
                "Lobby"
            WHERE
                id = :id
            """;

        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setObject("id", UUID.fromString(id));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(parseResultSetToLobby(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find lobby by id", e);
        }

        return Optional.empty();
    }

    @Override
    public Optional<Lobby> findAvailableLobbyByJoinCode(final String joinCode) {
        String sql = """
            SELECT
                id,
                "joinCode",
                status,
                "createdAt",
                "expiresAt",
                "playerCount",
                "winnerId",
                "isTie"
            FROM
                "Lobby"
            WHERE
                "joinCode" = :joinCode
                AND status = :status
            """;

        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setString("joinCode", joinCode);
            stmt.setObject("status", LobbyStatus.AVAILABLE.name(), java.sql.Types.OTHER);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(parseResultSetToLobby(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find lobby by join code and status", e);
        }

        return Optional.empty();
    }

    @Override
    public Optional<Lobby> findActiveLobbyByJoinCode(final String joinCode) {
        String sql = """
            SELECT
                id,
                "joinCode",
                status,
                "createdAt",
                "expiresAt",
                "playerCount",
                "winnerId",
                "isTie"
            FROM
                "Lobby"
            WHERE
                "joinCode" = :joinCode
                AND status = 'ACTIVE'
            """;

        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setString("joinCode", joinCode);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(parseResultSetToLobby(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find lobby by join code and status", e);
        }

        return Optional.empty();
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
                "winnerId",
                "isTie"
            FROM
                "Lobby"
            WHERE
                status = :status
            ORDER BY
                "createdAt" DESC
            """;

        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
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
                "winnerId",
                "isTie"
            FROM
                "Lobby"
            WHERE
                status = :status
                AND "expiresAt" > NOW()
            ORDER BY
                "createdAt" DESC
            """;

        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
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
    public List<Lobby> findActiveLobbies() {
        List<Lobby> result = new ArrayList<>();
        String sql = """
            SELECT
                id,
                "joinCode",
                status,
                "createdAt",
                "expiresAt",
                "playerCount",
                "winnerId",
                "isTie"
            FROM
                "Lobby"
            WHERE
                status = :status
                AND "expiresAt" > NOW()
            ORDER BY
                "createdAt" DESC
            """;

        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setObject("status", LobbyStatus.ACTIVE.name(), java.sql.Types.OTHER);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    result.add(parseResultSetToLobby(rs));
                }
            }

            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find active lobbies", e);
        }
    }

    @Override
    public List<Lobby> findExpiredLobbies() {
        List<Lobby> result = new ArrayList<>();
        String sql = """
            SELECT
                id,
                "joinCode",
                status,
                "createdAt",
                "expiresAt",
                "playerCount",
                "winnerId",
                "isTie"
            FROM
                "Lobby"
            WHERE
                status = :status
                AND "expiresAt" <= NOW()
            ORDER BY
                "createdAt" DESC
            """;

        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setObject("status", LobbyStatus.ACTIVE.name(), java.sql.Types.OTHER);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    result.add(parseResultSetToLobby(rs));
                }
            }

            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find expired lobbies", e);
        }
    }

    @Override
    public Optional<Lobby> findActiveLobbyByLobbyPlayerPlayerId(final String lobbyPlayerId) {
        String sql = """
            SELECT
                l.id,
                l."joinCode",
                l.status,
                l."createdAt",
                l."expiresAt",
                l."playerCount",
                l."winnerId",
                l."isTie"
            FROM
                "Lobby" l
            INNER JOIN
                "LobbyPlayer" lp ON l.id = lp."lobbyId"
            WHERE
                l.status = :status
                AND lp."playerId" = :playerId
            """;

        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setObject("status", LobbyStatus.ACTIVE.name(), java.sql.Types.OTHER);
            stmt.setObject("playerId", UUID.fromString(lobbyPlayerId));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(parseResultSetToLobby(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find active lobby by lobby player id", e);
        }

        return Optional.empty();
    }

    @Override
    public Optional<Lobby> findAvailableLobbyByLobbyPlayerPlayerId(final String lobbyPlayerId) {
        String sql = """
            SELECT
                l.id,
                l."joinCode",
                l.status,
                l."createdAt",
                l."expiresAt",
                l."playerCount",
                l."winnerId",
                l."isTie"
            FROM
                "Lobby" l
            JOIN
                "LobbyPlayer" lp ON l.id = lp."lobbyId"
            WHERE
                l.status = 'AVAILABLE'
            AND
                lp."playerId" = :playerId
            """;

        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setObject("playerId", UUID.fromString(lobbyPlayerId));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(parseResultSetToLobby(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find available lobby by lobby player id", e);
        }

        return Optional.empty();
    }

    @Override
    public boolean updateLobby(final Lobby lobby) {
        String sql = """
            UPDATE "Lobby"
            SET
                status = :status,
                "playerCount" = :playerCount,
                "winnerId" = :winnerId,
                "isTie" = :isTie
            WHERE
                id = :id
            """;

        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setObject("status", lobby.getStatus().name(), java.sql.Types.OTHER);
            stmt.setInt("playerCount", lobby.getPlayerCount());
            stmt.setObject("id", UUID.fromString(lobby.getId()));
            stmt.setObject("winnerId", lobby.getWinnerId().map(UUID::fromString).orElse(null));
            stmt.setBoolean("isTie", lobby.isTie());

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

        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setObject("id", UUID.fromString(id));

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected == 1;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete lobby", e);
        }
    }
}
