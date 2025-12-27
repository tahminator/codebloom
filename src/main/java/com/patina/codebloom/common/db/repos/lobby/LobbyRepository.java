package com.patina.codebloom.common.db.repos.lobby;

import com.patina.codebloom.common.db.models.lobby.Lobby;
import com.patina.codebloom.common.db.models.lobby.LobbyStatus;
import java.util.List;
import java.util.Optional;

public interface LobbyRepository {
    /**
     * Creates a new lobby in the database.
     *
     * @note - The provided object's methods will be overridden with any returned data from the database.
     * @param lobby - required fields:
     *     <ul>
     *       <li>joinCode
     *       <li>status
     *       <li>expiresAt
     *     </ul>
     *     optional fields:
     *     <ul>
     *       <li>playerCount (defaults to 0)
     *       <li>winnerId
     *     </ul>
     */
    void createLobby(Lobby lobby);

    /**
     * Finds a lobby by its ID.
     *
     * @param id the lobby ID
     * @return an {@code Optional} containing the lobby player for the specified player, or {@code Optional.empty()} if
     *     not found
     */
    Optional<Lobby> findLobbyById(String id);

    /**
     * Finds a lobby by its join code and status of {@code AVAILABLE}
     *
     * @param joinCode the lobby join code
     * @return an {@code Optional} containing the lobby if found, or {@code Optional.empty()} otherwise
     */
    Optional<Lobby> findAvailableLobbyByJoinCode(String joinCode);

    /**
     * Finds a lobby by its join code and status of {@code ACTIVE}
     *
     * @param joinCode the lobby join code
     * @return an {@code Optional} containing the lobby if found, or {@code Optional.empty()} otherwise
     */
    Optional<Lobby> findActiveLobbyByJoinCode(String joinCode);

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

    /** Finds all active lobbies. */
    List<Lobby> findActiveLobbies();

    /** Finds all expired lobbies. */
    List<Lobby> findExpiredLobbies();

    /**
     * Finds active lobby for the given `Player` with a status of ACTIVE.
     *
     * @param lobbyPlayerPlayerId the player ID ({@code User.id}) on {@code LobbyPlayer}
     * @return an {@code Optional} containing the lobby if found, or {@code Optional.empty()} otherwise
     *     <p>TODO: Union with {@code LobbyRepository.findAvailableLobbyByLobbyPlayerId}
     */
    Optional<Lobby> findActiveLobbyByLobbyPlayerPlayerId(String lobbyPlayerPlayerId);

    /**
     * Finds available lobby for the given `Player` with a status of AVAILABLE.
     *
     * @param lobbyPlayerPlayerId the player ID ({@code User.id}) on {@code LobbyPlayer}
     * @return an {@code Optional} containing the lobby if found, or {@code Optional.empty()} otherwise
     *     <p>TODO: Union with {@code LobbyRepository.findActiveLobbyByLobbyPlayerId}
     */
    Optional<Lobby> findAvailableLobbyByLobbyPlayerPlayerId(String lobbyPlayerPlayerId);

    /**
     * Updates an existing lobby in the database.
     *
     * @note - The provided object's methods will be overridden with any returned data from the database.
     * @param lobby - required fields:
     *     <ul>
     *       <li>id
     *     </ul>
     *     updatable fields:
     *     <ul>
     *       <li>status
     *       <li>playerCount
     *       <li>winnerId
     *     </ul>
     *
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
