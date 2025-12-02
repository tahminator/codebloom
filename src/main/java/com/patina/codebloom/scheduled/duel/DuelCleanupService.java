package com.patina.codebloom.scheduled.duel;

import com.patina.codebloom.common.components.DuelManager;
import com.patina.codebloom.common.db.models.lobby.Lobby;
import com.patina.codebloom.common.db.repos.lobby.LobbyRepository;
import com.patina.codebloom.common.utils.function.ThrowableProcedure;
import java.util.concurrent.TimeUnit;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class DuelCleanupService {

    private final DuelManager duelManager;
    private final LobbyRepository lobbyRepository;

    public DuelCleanupService(DuelManager duelManager, LobbyRepository lobbyRepository) {
        this.duelManager = duelManager;
        this.lobbyRepository = lobbyRepository;
    }

    public static <T> void swallow(ThrowableProcedure procedure) {
        try {
            procedure.run();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Scheduled(initialDelay = 30, fixedDelay = 30, timeUnit = TimeUnit.MINUTES)
    public void cleanupExpiredDuels() {
        var lobbies = lobbyRepository.findActiveLobbies();

        lobbies.stream().map(Lobby::getId).forEach(id -> swallow(() -> duelManager.endDuel(id)));
    }
}
