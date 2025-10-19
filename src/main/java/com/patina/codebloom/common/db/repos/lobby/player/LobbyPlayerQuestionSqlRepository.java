package com.patina.codebloom.common.db.repos.lobby.player;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.patina.codebloom.common.db.DbConnection;
import com.patina.codebloom.common.db.helper.NamedPreparedStatement;
import com.patina.codebloom.common.db.models.lobby.player.LobbyPlayerQuestion;

@Component
public class LobbyPlayerQuestionSqlRepository implements LobbyPlayerQuestionRepository {
    private Connection conn;

    public LobbyPlayerQuestionSqlRepository(final DbConnection dbConnection) {
        this.conn = dbConnection.getConn();
    }

    private LobbyPlayerQuestion parseResultSetToLobbyPlayerQuestion(final ResultSet resultSet) throws SQLException {
        return LobbyPlayerQuestion.builder()
                        .id(resultSet.getString("id"))
                        .lobbyPlayerId(resultSet.getString("lobbyPlayerId"))
                        .questionId(resultSet.getString("questionId"))
                        .points(resultSet.getInt("points"))
                        .build();
    }

    @Override
    public void createLobbyPlayerQuestion(final LobbyPlayerQuestion lobbyPlayerQuestion) {
        String sql = """
                        INSERT INTO "LobbyPlayerQuestion"
                            (id, "lobbyPlayerId", "questionId", points)
                        VALUES
                            (:id, :lobbyPlayerId, :questionId, :points)
                        """;

        lobbyPlayerQuestion.setId(UUID.randomUUID().toString());

        try (NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setObject("id", UUID.fromString(lobbyPlayerQuestion.getId()));
            stmt.setObject("lobbyPlayerId", UUID.fromString(lobbyPlayerQuestion.getLobbyPlayerId()));
            stmt.setObject("questionId", UUID.fromString(lobbyPlayerQuestion.getQuestionId()));
            stmt.setInt("points", lobbyPlayerQuestion.getPoints());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create lobby player question", e);
        }
    }

    @Override
    public LobbyPlayerQuestion findLobbyPlayerQuestionById(final String id) {
        String sql = """
                        SELECT
                            id,
                            "lobbyPlayerId",
                            "questionId",
                            points
                        FROM
                            "LobbyPlayerQuestion"
                        WHERE
                            id = :id
                        """;

        try (NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setObject("id", UUID.fromString(id));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return parseResultSetToLobbyPlayerQuestion(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find lobby player question by id", e);
        }

        return null;
    }

    @Override
    public List<LobbyPlayerQuestion> findQuestionsByLobbyPlayerId(final String lobbyPlayerId) {
        List<LobbyPlayerQuestion> result = new ArrayList<>();
        String sql = """
                        SELECT
                            id,
                            "lobbyPlayerId",
                            "questionId",
                            points
                        FROM
                            "LobbyPlayerQuestion"
                        WHERE
                            "lobbyPlayerId" = :lobbyPlayerId
                        """;

        try (NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setObject("lobbyPlayerId", UUID.fromString(lobbyPlayerId));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    result.add(parseResultSetToLobbyPlayerQuestion(rs));
                }
            }

            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find lobby player questions by lobby player id", e);
        }
    }

    @Override
    public List<LobbyPlayerQuestion> findLobbyPlayerQuestionsByQuestionId(final String questionId) {
        List<LobbyPlayerQuestion> result = new ArrayList<>();
        String sql = """
                        SELECT
                            id,
                            "lobbyPlayerId",
                            "questionId",
                            points
                        FROM
                            "LobbyPlayerQuestion"
                        WHERE
                            "questionId" = :questionId
                        """;

        try (NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setObject("questionId", UUID.fromString(questionId));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    result.add(parseResultSetToLobbyPlayerQuestion(rs));
                }
            }

            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find lobby player questions by question id", e);
        }
    }

    @Override
    public boolean updateLobbyPlayerQuestion(final LobbyPlayerQuestion lobbyPlayerQuestion) {
        String sql = """
                        UPDATE "LobbyPlayerQuestion"
                        SET
                            points = :points,
                            "questionId" = :questionId
                        WHERE
                            id = :id
                        """;

        try (NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setInt("points", lobbyPlayerQuestion.getPoints());
            stmt.setObject("questionId", UUID.fromString(lobbyPlayerQuestion.getQuestionId()));
            stmt.setObject("id", UUID.fromString(lobbyPlayerQuestion.getId()));

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected == 1;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update lobby player question", e);
        }
    }

    @Override
    public boolean deleteQuestionsByLobbyPlayerId(final String lobbyPlayerId) {
        String sql = """
                        DELETE FROM "LobbyPlayerQuestion"
                        WHERE "lobbyPlayerId" = :lobbyPlayerId
                        """;

        try (NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setObject("lobbyPlayerId", UUID.fromString(lobbyPlayerId));

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete questions by lobby player id", e);
        }
    }

    @Override
    public boolean deleteLobbyPlayerQuestionById(final String id) {
        String sql = """
                        DELETE FROM "LobbyPlayerQuestion"
                        WHERE id = :id
                        """;

        try (NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setObject("id", UUID.fromString(id));

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected == 1;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete lobby player question by id", e);
        }
    }
}