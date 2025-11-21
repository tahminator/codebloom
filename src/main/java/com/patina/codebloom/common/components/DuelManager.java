package com.patina.codebloom.common.components;

import com.patina.codebloom.common.db.repos.lobby.LobbyRepository;
import com.patina.codebloom.common.dto.lobby.DuelData;
import com.patina.codebloom.common.dto.lobby.LobbyDto;
import org.springframework.stereotype.Component;
import com.patina.codebloom.common.db.repos.lobby.player.LobbyPlayerQuestionRepository;

@Component
public class DuelManager {

    private final LobbyRepository lobbyRepository;
    private final LobbyPlayerQuestionRepository lobbyPlayerQuestionRepository;

    public DuelManager(final LobbyRepository lobbyRepository,
                    final LobbyPlayerQuestionRepository lobbyPlayerQuestionRepository) {
        this.lobbyRepository = lobbyRepository;
        this.lobbyPlayerQuestionRepository = lobbyPlayerQuestionRepository;
    }

    public DuelData generateDuelData(final String lobbyId) {
        var fetchedLobby = lobbyRepository
            .findLobbyById(lobbyId)
            .map(LobbyDto::fromLobby)
            .orElse(null);

        return DuelData.builder()
                        .lobby(fetchedLobby)
                        .questions(lobbyPlayerQuestionRepository.findUniqueQuestionsByLobbyId(lobbyId))
                        .build();
    }
}
