package com.patina.codebloom.scheduled.duel;

import com.patina.codebloom.common.components.duel.DuelManager;
import com.patina.codebloom.common.db.models.lobby.Lobby;
import com.patina.codebloom.common.db.repos.lobby.LobbyRepository;
import com.patina.codebloom.common.utils.function.ThrowableProcedure;
import com.patina.codebloom.common.utils.log.LogExecutionTime;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DuelCleanupService {

    private final DuelManager duelManager;
    private final LobbyRepository lobbyRepository;

    public DuelCleanupService(DuelManager duelManager, LobbyRepository lobbyRepository) {
        this.duelManager = duelManager;
        this.lobbyRepository = lobbyRepository;
    }

    /** Swallows exceptions and logs them without breaking control flow. */
    private void swallow(ThrowableProcedure procedure) {
        try {
            procedure.run();
        } catch (Exception e) {
            log.error("Exception swallowed", e);
        }
    }

    @Scheduled(initialDelay = 30, fixedDelay = 30, timeUnit = TimeUnit.MINUTES)
    @LogExecutionTime
    public void cleanupExpiredDuels() {
        log.info("cleanupExpiredDuels triggered");
        var lobbies = lobbyRepository.findExpiredLobbies();

        lobbies.stream().map(Lobby::getId).forEach(id -> {
            swallow(() -> duelManager.endDuel(id, true));
        });
    }
}
