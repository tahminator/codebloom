package com.patina.codebloom.common.components.duel;

import com.patina.codebloom.common.db.models.lobby.Lobby;
import com.patina.codebloom.common.db.models.lobby.LobbyStatus;
import com.patina.codebloom.common.db.models.lobby.player.LobbyPlayer;
import com.patina.codebloom.common.db.repos.lobby.LobbyRepository;
import com.patina.codebloom.common.db.repos.lobby.player.LobbyPlayerRepository;
import com.patina.codebloom.common.time.StandardizedOffsetDateTime;
import com.patina.codebloom.common.utils.duel.PartyCodeGenerator;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * The main difference between this class and {@code DuelManager} is that this class mainly handles operations with the
 * idea of a "party", such as joining or leaving a party.
 */
@Component
@Slf4j
public class PartyManager {
    private static final int MAX_PLAYER_COUNT = 2;

    private final LobbyRepository lobbyRepository;
    private final LobbyPlayerRepository lobbyPlayerRepository;

    public PartyManager(LobbyPlayerRepository lobbyPlayerRepository, LobbyRepository lobbyRepository) {
        this.lobbyRepository = lobbyRepository;
        this.lobbyPlayerRepository = lobbyPlayerRepository;
    }

    public void joinParty(String userId, String partyCode) throws DuelException {
        try {
            var lobby = lobbyRepository
                    .findAvailableLobbyByJoinCode(partyCode)
                    .orElseThrow(() ->
                            new DuelException(HttpStatus.NOT_FOUND, "The party with the given code cannot be found."));

            var now = StandardizedOffsetDateTime.now();
            if (lobby.getExpiresAt() != null
                    && lobby.getExpiresAt().isPresent()
                    && lobby.getExpiresAt().get().isBefore(now)) {
                // TODO: Could possibly invalidate this party here if it hasn't been invalidated
                // yet.
                throw new DuelException(HttpStatus.GONE, "The lobby has expired and cannot be joined.");
            }

            if (lobby.getPlayerCount() == MAX_PLAYER_COUNT) {
                throw new DuelException(HttpStatus.CONFLICT, "This lobby already has the maximum number of players");
            }

            var availableLobby = lobbyRepository.findAvailableLobbyByLobbyPlayerPlayerId(userId);

            if (availableLobby.isPresent()) {
                throw new DuelException(
                        HttpStatus.CONFLICT, "You are already in a party. Please leave the party, then try again.");
            }

            var activeLobby = lobbyRepository.findActiveLobbyByLobbyPlayerPlayerId(userId);

            if (activeLobby.isPresent()) {
                throw new DuelException(
                        HttpStatus.CONFLICT, "You are currently in a duel. Please forfeit the duel, then try again.");
            }

            lobbyPlayerRepository.createLobbyPlayer(LobbyPlayer.builder()
                    .lobbyId(lobby.getId())
                    .playerId(userId)
                    .build());

            lobby.setPlayerCount(lobby.getPlayerCount() + 1);
            boolean isSuccessful = lobbyRepository.updateLobby(lobby);

            if (!isSuccessful) {
                throw new DuelException(
                        HttpStatus.INTERNAL_SERVER_ERROR, "Failed to join party. Please try again later.");
            }
        } catch (DuelException e) {
            throw e;
        } catch (Exception e) {
            log.error("Exception thrown in PartyManager", e);
            throw new DuelException(e);
        }
    }

    public void leaveParty(String userId) throws DuelException {
        try {
            LobbyPlayer existingLobbyPlayer = lobbyPlayerRepository
                    .findValidLobbyPlayerByPlayerId(userId)
                    .orElseThrow(() -> new DuelException(HttpStatus.NOT_FOUND, "You are not currently in a lobby."));

            String lobbyId = existingLobbyPlayer.getLobbyId();

            boolean isLobbyPlayerDeleted = lobbyPlayerRepository.deleteLobbyPlayerById(existingLobbyPlayer.getId());
            if (!isLobbyPlayerDeleted) {
                throw new DuelException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to leave lobby. Please try again.");
            }

            Lobby lobby = lobbyRepository
                    .findLobbyById(lobbyId)
                    .orElseThrow(() -> new DuelException(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong."));

            int updatedPlayerCount = lobby.getPlayerCount() - 1;

            lobby.setPlayerCount(updatedPlayerCount);
            if (updatedPlayerCount == 0) {
                lobby.setStatus(LobbyStatus.CLOSED);
            } else {
                lobby.setStatus(LobbyStatus.AVAILABLE);
            }
            lobbyRepository.updateLobby(lobby);
        } catch (DuelException e) {
            throw e;
        } catch (Exception e) {
            log.error("Exception thrown in PartyManager", e);
            throw new DuelException(e);
        }
    }

    public String createParty(String userId) throws DuelException {
        try {
            var existingLobbyPlayer = lobbyPlayerRepository.findValidLobbyPlayerByPlayerId(userId);

            if (existingLobbyPlayer.isPresent()) {
                throw new DuelException(
                        HttpStatus.CONFLICT,
                        "You are already in a lobby. Please leave your current lobby before creating a new one.");
            }

            String joinCode = PartyCodeGenerator.generateCode();

            Lobby lobby = Lobby.builder()
                    .joinCode(joinCode)
                    .status(LobbyStatus.AVAILABLE)
                    .expiresAt(null)
                    .playerCount(1)
                    .winnerId(Optional.empty())
                    .build();

            lobbyRepository.createLobby(lobby);

            LobbyPlayer lobbyPlayer = LobbyPlayer.builder()
                    .lobbyId(lobby.getId())
                    .playerId(userId)
                    .points(0)
                    .build();

            lobbyPlayerRepository.createLobbyPlayer(lobbyPlayer);

            return joinCode;
        } catch (DuelException e) {
            throw e;
        } catch (Exception e) {
            log.error("Exception thrown in PartyManager", e);
            throw new DuelException(e);
        }
    }
}
