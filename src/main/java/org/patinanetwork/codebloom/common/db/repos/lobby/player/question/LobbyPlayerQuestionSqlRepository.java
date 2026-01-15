package org.patinanetwork.codebloom.common.db.repos.lobby.player.question;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.sql.DataSource;
import org.patinanetwork.codebloom.common.db.helper.NamedPreparedStatement;
import org.patinanetwork.codebloom.common.db.models.lobby.player.LobbyPlayerQuestion;
import org.springframework.stereotype.Component;

@Component
public class LobbyPlayerQuestionSqlRepository implements LobbyPlayerQuestionRepository {

    private DataSource ds;

    public LobbyPlayerQuestionSqlRepository(final DataSource ds) {
        this.ds = ds;
    }

    private LobbyPlayerQuestion parseResultSetToLobbyPlayerQuestion(final ResultSet resultSet) throws SQLException {
        return LobbyPlayerQuestion.builder()
                .id(resultSet.getString("id"))
                .lobbyPlayerId(resultSet.getString("lobbyPlayerId"))
                .questionId(Optional.ofNullable(resultSet.getString("questionId")))
                .points(Optional.ofNullable(resultSet.getObject("points", Integer.class)))
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

        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setObject("id", UUID.fromString(lobbyPlayerQuestion.getId()));
            stmt.setObject("lobbyPlayerId", UUID.fromString(lobbyPlayerQuestion.getLobbyPlayerId()));
            stmt.setObject(
                    "questionId",
                    lobbyPlayerQuestion.getQuestionId().map(UUID::fromString).orElse(null));
            stmt.setObject("points", lobbyPlayerQuestion.getPoints().orElse(null), java.sql.Types.INTEGER);

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create lobby player question", e);
        }
    }

    @Override
    public Optional<LobbyPlayerQuestion> findLobbyPlayerQuestionById(final String id) {
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

        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setObject("id", UUID.fromString(id));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(parseResultSetToLobbyPlayerQuestion(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find lobby player question by id", e);
        }

        return Optional.empty();
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

        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
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

        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
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
    public boolean updateLobbyPlayerQuestionById(final LobbyPlayerQuestion lobbyPlayerQuestion) {
        String sql = """
            UPDATE "LobbyPlayerQuestion"
            SET
                points = :points,
                "questionId" = :questionId
            WHERE
                id = :id
            """;

        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setObject("points", lobbyPlayerQuestion.getPoints().orElse(null), java.sql.Types.INTEGER);
            stmt.setObject(
                    "questionId",
                    lobbyPlayerQuestion.getQuestionId().map(UUID::fromString).orElse(null));
            stmt.setObject("id", UUID.fromString(lobbyPlayerQuestion.getId()));

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected == 1;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update lobby player question", e);
        }
    }

    @Override
    public boolean deleteLobbyPlayerQuestionByLobbyPlayerId(final String lobbyPlayerId) {
        String sql = """
            DELETE FROM "LobbyPlayerQuestion"
            WHERE "lobbyPlayerId" = :lobbyPlayerId
            """;

        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
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

        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setObject("id", UUID.fromString(id));

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected == 1;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete lobby player question by id", e);
        }
    }

    @Override
    public List<String> findUniqueQuestionIdsByLobbyId(final String lobbyId) {
        List<String> result = new ArrayList<>();
        String sql = """
            SELECT DISTINCT "questionId"
            FROM
                "LobbyPlayerQuestion"
            WHERE
                "lobbyPlayerId" IN (
                    SELECT id FROM "LobbyPlayer" WHERE "lobbyId" = :lobbyId
                )
            AND "questionId" IS NOT NULL
            ORDER BY
                "questionId"
            """;

        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setObject("lobbyId", UUID.fromString(lobbyId));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    result.add(rs.getString("questionId"));
                }
            }

            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find unique question IDs by lobby id", e);
        }
    }
}
