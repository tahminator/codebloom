package com.patina.codebloom.common.db.repos.lobby.player;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.patina.codebloom.common.db.DbConnection;
import com.patina.codebloom.common.db.helper.NamedPreparedStatement;
import com.patina.codebloom.common.db.models.lobby.player.LobbyPlayer;

@Component
public class LobbyPlayerSqlRepository implements LobbyPlayerRepository {
    private Connection conn;

    public LobbyPlayerSqlRepository(final DbConnection dbConnection) {
        this.conn = dbConnection.getConn();
    }

    private LobbyPlayer parseResultSetToLobbyPlayer(final ResultSet resultSet) throws SQLException {
        return LobbyPlayer.builder()
                        .id(resultSet.getString("id"))
                        .lobbyId(resultSet.getString("lobbyId"))
                        .playerId(resultSet.getString("playerId"))
                        .points(resultSet.getInt("points"))
                        .build();
    }

    @Override
    public void createLobbyPlayer(final LobbyPlayer lobbyPlayer) {
        String sql = """
                        INSERT INTO "LobbyPlayer"
                            (id, "lobbyId", "playerId", points)
                        VALUES
                            (:id, :lobbyId, :playerId, :points)
                        """;

        lobbyPlayer.setId(UUID.randomUUID().toString());

        try (NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setObject("id", UUID.fromString(lobbyPlayer.getId()));
            stmt.setObject("lobbyId", UUID.fromString(lobbyPlayer.getLobbyId()));
            stmt.setObject("playerId", UUID.fromString(lobbyPlayer.getPlayerId()));
            stmt.setInt("points", lobbyPlayer.getPoints());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create lobby player", e);
        }
    }

    @Override
    public Optional<LobbyPlayer> findLobbyPlayerById(final String id) {
        String sql = """
                        SELECT
                            id,
                            "lobbyId",
                            "playerId",
                            points
                        FROM
                            "LobbyPlayer"
                        WHERE
                            id = :id
                        """;

        try (NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setObject("id", UUID.fromString(id));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(parseResultSetToLobbyPlayer(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find lobby player by id", e);
        }

        return Optional.empty();
    }

    @Override
    public Optional<LobbyPlayer> findLobbyPlayerByPlayerId(final String playerId) {
        String sql = """
                        SELECT
                            id,
                            "lobbyId",
                            "playerId",
                            points
                        FROM
                            "LobbyPlayer"
                        WHERE
                            "playerId" = :playerId
                        """;

        try (NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setObject("playerId", UUID.fromString(playerId));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(parseResultSetToLobbyPlayer(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find lobby player by player id", e);
        }

        return Optional.empty();
    }

    @Override
    public List<LobbyPlayer> findPlayersByLobbyId(final String lobbyId) {
        List<LobbyPlayer> result = new ArrayList<>();
        String sql = """
                        SELECT
                            id,
                            "lobbyId",
                            "playerId",
                            points
                        FROM
                            "LobbyPlayer"
                        WHERE
                            "lobbyId" = :lobbyId
                        ORDER BY
                            points DESC
                        """;

        try (NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setObject("lobbyId", UUID.fromString(lobbyId));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    result.add(parseResultSetToLobbyPlayer(rs));
                }
            }

            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find players by lobby id", e);
        }
    }

    @Override
    public boolean updateLobbyPlayer(final LobbyPlayer lobbyPlayer) {
        String sql = """
                        UPDATE "LobbyPlayer"
                        SET
                            points = :points
                        WHERE
                            id = :id
                        """;

        try (NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setInt("points", lobbyPlayer.getPoints());
            stmt.setObject("id", UUID.fromString(lobbyPlayer.getId()));

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected == 1;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update lobby player", e);
        }
    }

    @Override
    public boolean deletePlayersByLobbyId(final String lobbyId) {
        String sql = """
                        DELETE FROM "LobbyPlayer"
                        WHERE "lobbyId" = :lobbyId
                        """;

        try (NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setObject("lobbyId", UUID.fromString(lobbyId));

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete players by lobby id", e);
        }
    }

    @Override
    public boolean deleteLobbyPlayerById(final String id) {
        String sql = """
                        DELETE FROM "LobbyPlayer"
                        WHERE id = :id
                        """;

        try (NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setObject("id", UUID.fromString(id));

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected == 1;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete lobby player by id", e);
        }
    }
}
