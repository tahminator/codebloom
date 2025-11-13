package com.patina.codebloom.common.components;

import org.springframework.stereotype.Component;

import com.patina.codebloom.api.duel.dto.DuelData;
import com.patina.codebloom.api.duel.dto.LobbyDto;
import com.patina.codebloom.common.db.models.lobby.Lobby;

@Component
public class DuelManager {
    public DuelManager() {
    }

    public DuelData generateDuelData(final Lobby lobby) {
        return DuelData.builder()
                        .lobby(LobbyDto.fromLobby(lobby))
                        .build();
    }
}
