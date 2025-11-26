package com.patina.codebloom.common.components;

import com.patina.codebloom.common.db.repos.lobby.LobbyRepository;
import com.patina.codebloom.common.dto.lobby.DuelData;
import com.patina.codebloom.common.dto.lobby.LobbyDto;
import org.springframework.stereotype.Component;

@Component
public class DuelManager {

    private final LobbyRepository lobbyRepository;

    public DuelManager(final LobbyRepository lobbyRepository) {
        this.lobbyRepository = lobbyRepository;
    }

    public DuelData generateDuelData(final String lobbyId) {
        var fetchedLobby = lobbyRepository
            .findLobbyById(lobbyId)
            .map(LobbyDto::fromLobby)
            .orElse(null);

        return DuelData.builder().lobby(fetchedLobby).build();
    }
}
