package com.patina.codebloom.common.db.repos.lobby;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.patina.codebloom.common.db.models.lobby.Lobby;
import com.patina.codebloom.common.db.models.lobby.LobbyQuestion;
import com.patina.codebloom.common.db.models.question.bank.QuestionBank;
import com.patina.codebloom.common.db.helper.NamedPreparedStatement;

import com.patina.codebloom.common.db.DbConnection;

@Component
public class LobbyQuestionSqlRepository implements LobbyQuestionRepository {
    private Connection conn;

    public LobbyQuestionSqlRepository(final DbConnection dbConnection) {
        this.conn = dbConnection.getConn();
    }
    private LobbyQuestion parseResultSetToLobbyQuestion(final ResultSet resultSet) throws SQLException {
        return LobbyQuestion.builder()
        .id(resultSet.getString("id"))
        .lobbyId(resultSet.getString("lobbyId"))
        .questionBankId(resultSet.getString("questionBankId"))
        .userSolvedCount(resultSet.getInt("userSolvedCount"))
        .createdAt(resultSet.getObject("createdAt", OffsetDateTime.class))
        .build();
    }

    @Override
    public void createLobbyQuestion(final LobbyQuestion lobbyQuestion) {
            String sql = """
                        INSERT INTO "LobbyQuestion"
                    (id, "lobbyId", "questionBankId", "userSolvedCount" )
                    VALUES 
                    (:id, :"lobbyId", :"questionBankId", :UserSolvedCount )
                    RETURNING
                    "createdAt"
            """;
            lobbyQuestion.setId(UUID.randomUUID().toString());
        try(NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setObject("id", UUID.fromString(lobbyQuestion.getId()));
            stmt.setObject("lobbyId", UUID.fromString(lobbyQuestion.getLobbyId()));
            stmt.setObject("questionBankId", UUID.fromString(lobbyQuestion.getQuestionBankId()));
            stmt.setInt("UserSolvedCount", lobbyQuestion.getUserSolvedCount());

        try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    lobbyQuestion.setCreatedAt(rs.getObject("createdAt", OffsetDateTime.class));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create lobby", e);
        }
    
    }
    @Override
    public Optional<LobbyQuestion> findLobbyQuestionById(final String id) {
      String sql = """
                   SELECT 
                   id
                   "lobbyId"
                   "questionBankId"
                   "UserSolvedCount"
                   "CreatedAt"

                   FROM
                   "LobbyQuestion"

                   WHERE
                     id = :id

              """;
            try (NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setObject("id", UUID.fromString(id));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(parseResultSetToLobbyQuestion(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find lobbyQuestion by id", e);
        }
         return Optional.empty();
    }

    @Override
    public Optional<LobbyQuestion> findLobbyQuestionByLobbyId(final String lobbyId) {
      String sql = """
                   SELECT 
                   id
                   "lobbyId"
                   "questionBankId"
                   "UserSolvedCount"
                   "CreatedAt"

                   FROM
                   "LobbyQuestion"

                   WHERE
                     "lobbyId" = :"lobbyId"
              """;
            
            try (NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setObject("id", UUID.fromString(lobbyId));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(parseResultSetToLobbyQuestion(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find lobbyQuestion by id", e);
        }
         return Optional.empty();
    }

    @Override
    public Optional<LobbyQuestion> findLobbyQuestionByQuestionBankId(final String questionBankId) {
      String sql = """
                   SELECT 
                   id
                   "lobbyId"
                   "questionBankId"
                   "UserSolvedCount"
                   "CreatedAt"

                   FROM
                   "LobbyQuestion"

                   WHERE
                     "questionBankId" = :"questionBankId"
              """;
            
            try (NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setObject("questionBankId", UUID.fromString("questionBankId"));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(parseResultSetToLobbyQuestion(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find lobbyQuestion by questionBankId", e);
        }
         return Optional.empty();
    }

    @Override
    public boolean updateQuestionLobby(final LobbyQuestion lobbyQuestion) {
                String sql = """
                        UPDATE "LobbyQuestion"
                        SET
                            "userSolvedCount" = "userSolvedCount"
                        WHERE
                            id = :id
                        """;

        try (NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setInt("userSolvedCount", lobbyQuestion.getUserSolvedCount());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected == 1;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update lobby", e);
        }
    }

    @Override
    public boolean deleteLobbyQuestionById(final String id) {
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