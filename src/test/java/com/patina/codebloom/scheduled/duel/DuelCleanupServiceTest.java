package com.patina.codebloom.scheduled.duel;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.patina.codebloom.common.components.duel.DuelException;
import com.patina.codebloom.common.components.duel.DuelManager;
import com.patina.codebloom.common.db.models.lobby.Lobby;
import com.patina.codebloom.common.db.models.lobby.LobbyStatus;
import com.patina.codebloom.common.db.repos.lobby.LobbyRepository;
import com.patina.codebloom.common.time.StandardizedOffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

public class DuelCleanupServiceTest {
    private final DuelCleanupService duelCleanupService;

    private final DuelManager duelManager = mock(DuelManager.class);
    private final LobbyRepository lobbyRepository = mock(LobbyRepository.class);

    public DuelCleanupServiceTest() {
        duelCleanupService = new DuelCleanupService(duelManager, lobbyRepository);
    }

    @Test
    void cleanupExpiredDuelsSuccessful() {
        var expiredLobbies = List.of(
                Lobby.builder()
                        .id("12345467890")
                        .createdAt(StandardizedOffsetDateTime.now())
                        .expiresAt(Optional.of(StandardizedOffsetDateTime.now().minus(30, ChronoUnit.MINUTES)))
                        .joinCode("ABC123")
                        .playerCount(2)
                        .status(LobbyStatus.ACTIVE)
                        .winnerId(Optional.empty())
                        .build(),
                Lobby.builder()
                        .id("12345467891")
                        .createdAt(StandardizedOffsetDateTime.now())
                        .expiresAt(Optional.of(StandardizedOffsetDateTime.now().minus(30, ChronoUnit.MINUTES)))
                        .joinCode("DEF456")
                        .playerCount(2)
                        .status(LobbyStatus.ACTIVE)
                        .winnerId(Optional.empty())
                        .build());

        when(lobbyRepository.findExpiredLobbies()).thenReturn(expiredLobbies);
        try {
            doNothing().when(duelManager).endDuel(any(), eq(true));
            duelCleanupService.cleanupExpiredDuels();
        } catch (DuelException e) {
            fail(e);
        }

        try {
            verify(duelManager, times(expiredLobbies.size())).endDuel(any(), eq(true));
        } catch (DuelException e) {
            fail(e);
        }
    }

    @Test
    void cleanupExpiredDuelsSomeDuelsFailed() {
        var expiredLobbies = List.of(
                Lobby.builder()
                        .id("12345467890")
                        .createdAt(StandardizedOffsetDateTime.now())
                        .expiresAt(Optional.of(StandardizedOffsetDateTime.now().minus(30, ChronoUnit.MINUTES)))
                        .joinCode("ABC123")
                        .playerCount(2)
                        .status(LobbyStatus.ACTIVE)
                        .winnerId(Optional.empty())
                        .build(),
                Lobby.builder()
                        .id("12345467891")
                        .createdAt(StandardizedOffsetDateTime.now())
                        .expiresAt(Optional.of(StandardizedOffsetDateTime.now().minus(30, ChronoUnit.MINUTES)))
                        .joinCode("DEF456")
                        .playerCount(2)
                        .status(LobbyStatus.ACTIVE)
                        .winnerId(Optional.empty())
                        .build());

        when(lobbyRepository.findExpiredLobbies()).thenReturn(expiredLobbies);
        try {
            doThrow(new DuelException(HttpStatus.INTERNAL_SERVER_ERROR, "Example exception."))
                    .when(duelManager)
                    .endDuel(any(), eq(true));
        } catch (DuelException e) {
            e.printStackTrace();
        }

        duelCleanupService.cleanupExpiredDuels();

        try {
            verify(duelManager, times(expiredLobbies.size())).endDuel(any(), eq(true));
        } catch (DuelException e) {
            fail(e);
        }
    }
}
