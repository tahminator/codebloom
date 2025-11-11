package com.patina.codebloom.common.db.repos.lobby;

import java.util.List;

import com.patina.codebloom.common.db.models.lobby.Lobby;
import com.patina.codebloom.common.db.models.lobby.LobbyStatus;

public interface LobbyRepository {
    /**
     * Creates a new lobby in the database.
     * 
     * @note - The provided object's methods will be overridden with any returned
     * data from the database.
     *
     * @param lobby - required fields:
     * <ul>
     * <li>joinCode</li>
     * <li>status</li>
     * <li>expiresAt</li>
     * </ul>
     * optional fields:
     * <ul>
     * <li>playerCount (defaults to 0)</li>
     * <li>winnerId</li>
     * </ul>
     */
    void createLobby(Lobby lobby);

    /**
     * Finds a lobby by its ID.
     * 
     * @param id the lobby ID
     * @return the lobby if found, null otherwise
     */
    Lobby findLobbyById(String id);

    /**
     * Finds a lobby by its join code.
     * 
     * @param joinCode the lobby join code
     * @return the lobby if found, null otherwise
     */
    Lobby findLobbyByJoinCode(String joinCode);

    /**
     * Finds all lobbies with a specific status.
     * 
     * @param status the lobby status to search for
     * @return list of lobbies with the specified status
     */
    List<Lobby> findLobbiesByStatus(LobbyStatus status);

    /**
     * Finds all available lobbies (available for joining).
     * 
     * @return list of available lobbies
     */
    List<Lobby> findAvailableLobbies();

    /**
     * Finds active lobby for the given `Player` with a status of ACTIVE.
     *
     * @param lobbyPlayerId the lobby player ID
     * @return the lobby if found, null otherwise
     */
    Lobby findActiveLobbyByLobbyPlayerId(String lobbyPlayerId);

    /**
     * Finds active lobby for the given `Player` with a status of AVAILABLE.
     *
     * @param lobbyPlayerId the lobby player ID
     * @return the lobby if found, null otherwise
     */
    Lobby findAvailableLobbyByLobbyPlayerId(String lobbyPlayerId);

    /**
     * Updates an existing lobby in the database.
     * 
     * @note - The provided object's methods will be overridden with any returned
     * data from the database.
     *
     * @param lobby - required fields:
     * <ul>
     * <li>id</li>
     * </ul>
     * updatable fields:
     * <ul>
     * <li>status</li>
     * <li>expiresAt</li>
     * <li>playerCount</li>
     * </ul>
     * @return true if the update was successful, false otherwise
     */
    boolean updateLobby(Lobby lobby);

    /**
     * Deletes a lobby by its ID.
     * 
     * @param id the lobby ID to delete
     * @return true if the lobby was deleted successfully, false otherwise
     */
    boolean deleteLobbyById(String id);
}
