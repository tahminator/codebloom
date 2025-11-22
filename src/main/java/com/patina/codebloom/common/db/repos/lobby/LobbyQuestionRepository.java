package com.patina.codebloom.common.db.repos.lobby;

import java.util.Optional;
import java.util.List;

import com.patina.codebloom.common.db.models.lobby.LobbyQuestion;

public interface LobbyQuestionRepository {
    /**
     * Creates a new lobbyQuestion in the database.
     * 
     * @note - The provided object's methods will be overridden with any returned
     * data from the database.
     *
     * @param lobbyQuestion - required fields:
     * <ul>
     * <li>lobbyId</li>
     * <li>questionBankId</li>
     * <li>createdAt</li>
     * </ul>
     */
    void createLobbyQuestion(LobbyQuestion lobbyQuestion);

    Optional<LobbyQuestion> findLobbyQuestionById(String id);

    List<LobbyQuestion> findLobbyQuestionsByLobbyId(String lobbyId);

    List<LobbyQuestion> findLobbyQuestionByLobbyIdAndQuestionBankId(String lobbyId, String questionBankId);

    Optional<LobbyQuestion> findMostRecentLobbyQuestionByLobbyId(String lobbyId);

    List<LobbyQuestion> findAllLobbyQuestions();

    /**
     * Updates an existing lobby in the database.
     * 
     * @note - The provided object's methods will be overridden with any returned
     * data from the database.
     *
     * @param LobbyQuestion - required fields:
     * <ul>
     * <li>id</li>
     * </ul>
     * updatable fields:
     * <ul>
     * <li>lobbyId</li>
     * <li>questionBankId</li>
     * </ul>
     * @return true if the update was successful, false otherwise
     */
    boolean updateQuestionLobby(LobbyQuestion lobbyQuestion);

    boolean deleteLobbyQuestionById(String id);

}