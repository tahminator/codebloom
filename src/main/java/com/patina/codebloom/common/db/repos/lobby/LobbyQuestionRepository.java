package com.patina.codebloom.common.db.repos.lobby;

import java.util.Optional;

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

    Optional<LobbyQuestion> findLobbyQuestionByLobbyId(String lobbyId);

    Optional<LobbyQuestion> findLobbyQuestionByQuestionBankId(String questionBankId);

    Optional<LobbyQuestion> findLobbyRecentQuestionByLobbyId(String lobbyId);

    List <


        /**
     * Updates an existing lobby in the database.
     * 
     * @note - The provided object's methods will be overridden with any returned
     * data from the database.
     *
     * @param LobbyQuestion  - required fields:
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