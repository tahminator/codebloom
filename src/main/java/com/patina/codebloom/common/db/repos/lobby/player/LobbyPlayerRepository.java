package com.patina.codebloom.common.db.repos.lobby.player;

import java.util.List;

import com.patina.codebloom.common.db.models.lobby.player.LobbyPlayer;

public interface LobbyPlayerRepository {
    /**
     * Creates a new lobby player in the database.
     * 
     * @note - The provided object's methods will be overridden with any returned
     * data from the database.
     *
     * @param lobbyPlayer - required fields:
     * <ul>
     * <li>lobbyId</li>
     * <li>playerId</li>
     * </ul>
     * optional fields:
     * <ul>
     * <li>points (defaults to 0)</li>
     * </ul>
     */
    void createLobbyPlayer(LobbyPlayer lobbyPlayer);

    /**
     * Finds a lobby player by its ID.
     * 
     * @param id the lobby player ID
     * @return the lobby player if found, null otherwise
     */
    LobbyPlayer findLobbyPlayerById(String id);

    /**
     * Finds a lobby player for a specific player ID.
     * 
     * @param playerId the player ID
     * @return the lobby player for the specified player, null if not found
     */
    LobbyPlayer findLobbyPlayerByPlayerId(String playerId);

    /**
     * Finds all players in a specific lobby.
     * 
     * @param lobbyId the lobby ID
     * @return list of lobby players in the specified lobby
     */
    List<LobbyPlayer> findPlayersByLobbyId(String lobbyId);

    /**
     * Updates an existing lobby player in the database.
     * 
     * @note - The provided object's methods will be overridden with any returned
     * data from the database.
     *
     * @param lobbyPlayer - required fields:
     * <ul>
     * <li>id</li>
     * </ul>
     * updatable fields:
     * <ul>
     * <li>points</li>
     * </ul>
     * @return true if the update was successful, false otherwise
     */
    boolean updateLobbyPlayer(LobbyPlayer lobbyPlayer);

    /**
     * Deletes all players from a specific lobby.
     * 
     * @param lobbyId the lobby ID
     * @return true if all players were successfully deleted from the lobby, false
     * otherwise
     */
    boolean deletePlayersByLobbyId(String lobbyId);

    /**
     * Deletes a single lobby player by its ID.
     * 
     * @param id the lobby player ID to delete
     * @return true if the lobby player was deleted successfully, false otherwise
     */
    boolean deleteLobbyPlayerById(String id);
}