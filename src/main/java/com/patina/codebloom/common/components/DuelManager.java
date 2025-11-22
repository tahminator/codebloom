package com.patina.codebloom.common.components;

import org.springframework.stereotype.Component;

import com.patina.codebloom.common.dto.lobby.DuelData;
import com.patina.codebloom.common.dto.lobby.LobbyDto;
import com.patina.codebloom.common.db.repos.lobby.LobbyRepository;

@Component
public class DuelManager {
    private final LobbyRepository lobbyRepository;

    public DuelManager(final LobbyRepository lobbyRepository) {
        this.lobbyRepository = lobbyRepository;
    }

    public DuelData generateDuelData(final String lobbyId) {
        var fetchedLobby = lobbyRepository.findLobbyById(lobbyId).map(LobbyDto::fromLobby).orElse(null);

        return DuelData.builder()
                        .lobby(fetchedLobby)
                        .build();
    }
}
