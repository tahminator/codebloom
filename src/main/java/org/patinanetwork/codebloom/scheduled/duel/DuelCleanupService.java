package org.patinanetwork.codebloom.scheduled.duel;

import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.patinanetwork.codebloom.common.components.duel.DuelManager;
import org.patinanetwork.codebloom.common.db.models.lobby.Lobby;
import org.patinanetwork.codebloom.common.db.repos.lobby.LobbyRepository;
import org.patinanetwork.codebloom.common.utils.function.ThrowableProcedure;
import org.patinanetwork.codebloom.common.utils.log.LogExecutionTime;
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
