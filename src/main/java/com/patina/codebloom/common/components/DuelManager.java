package com.patina.codebloom.common.components;

import java.util.List;

import org.springframework.stereotype.Component;

import com.patina.codebloom.common.db.models.lobby.Lobby;
import com.patina.codebloom.common.db.models.lobby.player.LobbyPlayer;
import com.patina.codebloom.common.db.repos.lobby.LobbyRepository;
import com.patina.codebloom.common.db.repos.lobby.player.LobbyPlayerRepository;
import com.patina.codebloom.common.dto.lobby.DuelData;
import com.patina.codebloom.common.dto.lobby.LobbyDto;

@Component
public class DuelManager {
    private final LobbyRepository lobbyRepository;
    private final LobbyPlayerRepository lobbyPlayerRepository;

    public DuelManager(final LobbyRepository lobbyRepository,
                    final LobbyPlayerRepository lobbyPlayerRepository) {
        this.lobbyRepository = lobbyRepository;
        this.lobbyPlayerRepository = lobbyPlayerRepository;
    }

    public DuelData generateDuelData(final String lobbyId) {
        Lobby fetchedLobby = lobbyRepository.findLobbyById(lobbyId);
        return DuelData.builder()
                        .lobby(LobbyDto.fromLobby(fetchedLobby))
                        .build();
    }

    public void assignNewQuestionToLobby(final String lobbyId) {
        List<LobbyPlayer> players = lobbyPlayerRepository.findPlayersByLobbyId(lobbyId);

        for (LobbyPlayer player : players) {
            player.setPoints(null);
            lobbyPlayerRepository.updateLobbyPlayer(player);
        }
    }
}