package com.patina.codebloom.common.db.repos.lobby;

import com.patina.codebloom.common.db.models.lobby.LobbyQuestion;
import java.util.List;
import java.util.Optional;

public interface LobbyQuestionRepository {
    /**
     * Creates a new lobbyQuestion in the database.
     *
     * @note - The provided object's methods will be overridden with any returned data from the database.
     * @param lobbyQuestion - required fields:
     *     <ul>
     *       <li>lobbyId
     *       <li>questionBankId
     *     </ul>
     *     optional fields:
     *     <ul>
     *       <li>userSolvedCount (defaults to 0 if not provided)
     *     </ul>
     */
    void createLobbyQuestion(LobbyQuestion lobbyQuestion);

    Optional<LobbyQuestion> findLobbyQuestionById(String id);

    List<LobbyQuestion> findLobbyQuestionsByLobbyId(String lobbyId);

    List<LobbyQuestion> findLobbyQuestionsByLobbyIdAndQuestionBankId(String lobbyId, String questionBankId);

    Optional<LobbyQuestion> findMostRecentLobbyQuestionByLobbyId(String lobbyId);

    List<LobbyQuestion> findAllLobbyQuestions();

    /**
     * Updates an existing lobbyQuestion in the database.
     *
     * @note - The provided object's methods will be overridden with any returned data from the database.
     * @param lobbyQuestion - required fields:
     *     <ul>
     *       <li>id
     *     </ul>
     *     updatable fields:
     *     <ul>
     *       <li>userSolvedCount
     *     </ul>
     *
     * @return true if the update was successful, false otherwise
     */
    boolean updateQuestionLobby(LobbyQuestion lobbyQuestion);

    boolean deleteLobbyQuestionById(String id);
}
