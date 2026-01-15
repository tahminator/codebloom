package org.patinanetwork.codebloom.common.db.repos.lobby.player.question;

import java.util.List;
import java.util.Optional;
import org.patinanetwork.codebloom.common.db.models.lobby.player.LobbyPlayerQuestion;

public interface LobbyPlayerQuestionRepository {
    /**
     * Creates a new lobby player question in the database.
     *
     * @note - The provided object's methods will be overridden with any returned data from the database.
     * @param lobbyPlayerQuestion - required fields:
     *     <ul>
     *       <li>lobbyPlayerId
     *     </ul>
     *     optional fields:
     *     <ul>
     *       <li>questionId (can be null)
     *       <li>points (can be null)
     *     </ul>
     */
    void createLobbyPlayerQuestion(LobbyPlayerQuestion lobbyPlayerQuestion);

    /**
     * Finds a lobby player question by its ID.
     *
     * @param id the lobby player question ID
     * @return an {@code Optional} containing the lobby player question if found, or {@code Optional.empty()} otherwise
     */
    Optional<LobbyPlayerQuestion> findLobbyPlayerQuestionById(String id);

    /**
     * Finds all questions for a specific lobby player.
     *
     * @param lobbyPlayerId the lobby player ID
     * @return list of questions for the specified lobby player
     */
    List<LobbyPlayerQuestion> findQuestionsByLobbyPlayerId(String lobbyPlayerId);

    /**
     * Finds all lobby player questions for a specific question.
     *
     * @param questionId the question ID
     * @return list of lobby player questions for the specified question
     */
    List<LobbyPlayerQuestion> findLobbyPlayerQuestionsByQuestionId(String questionId);

    /**
     * Updates an existing lobby player question in the database.
     *
     * @note - The provided object's methods will be overridden with any returned data from the database.
     * @param lobbyPlayerQuestion
     *     <p>overridable fields:
     *     <ul>
     *       <li>points
     *       <li>questionId
     *     </ul>
     *
     * @return true if the update was successful, false otherwise
     */
    boolean updateLobbyPlayerQuestionById(LobbyPlayerQuestion lobbyPlayerQuestion);

    /**
     * Deletes all questions for a specific lobby player.
     *
     * @param lobbyPlayerId the lobby player ID
     * @return true if all questions were successfully deleted, false otherwise
     */
    boolean deleteLobbyPlayerQuestionByLobbyPlayerId(String lobbyPlayerId);

    /**
     * Deletes a single lobby player question by its ID.
     *
     * @param id the lobby player question ID to delete
     * @return true if the lobby player question was deleted successfully, false otherwise
     */
    boolean deleteLobbyPlayerQuestionById(String id);

    /**
     * Finds all unique question IDs for a specific lobby.
     *
     * @param lobbyId the lobby ID
     * @return list of unique question IDs in the specified lobby
     */
    List<String> findUniqueQuestionIdsByLobbyId(String lobbyId);
}
