package com.patina.codebloom.db.lobby.player;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.patina.codebloom.common.db.models.lobby.Lobby;
import com.patina.codebloom.common.db.models.lobby.LobbyStatus;
import com.patina.codebloom.common.db.models.lobby.player.LobbyPlayer;
import com.patina.codebloom.common.db.repos.lobby.LobbyRepository;
import com.patina.codebloom.common.db.repos.lobby.player.LobbyPlayerRepository;
import com.patina.codebloom.common.time.StandardizedOffsetDateTime;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
@Slf4j
public class LobbyPlayerRepositoryTest {
    private LobbyPlayerRepository lobbyPlayerRepository;
    private LobbyRepository lobbyRepository;
    private LobbyPlayer testLobbyPlayer;
    private Lobby testLobby;
    private String mockPlayerId = "ed3bfe18-e42a-467f-b4fa-07e8da4d2555";
    private String mockJoinCode = "PLAYER-TEST-" + UUID.randomUUID().toString().substring(0, 8);

    @Autowired
    public LobbyPlayerRepositoryTest(final LobbyPlayerRepository lobbyPlayerRepository,
                    final LobbyRepository lobbyRepository) {
        this.lobbyPlayerRepository = lobbyPlayerRepository;
        this.lobbyRepository = lobbyRepository;
    }

    @BeforeAll
    void setup() {
        testLobby = Lobby.builder()
                        .joinCode(mockJoinCode)
                        .status(LobbyStatus.AVAILABLE)
                        .expiresAt(StandardizedOffsetDateTime.now().plusHours(1))
                        .playerCount(1)
                        .winnerId(null)
                        .build();

        lobbyRepository.createLobby(testLobby);

        testLobbyPlayer = LobbyPlayer.builder()
                        .lobbyId(testLobby.getId())
                        .playerId(mockPlayerId)
                        .points(100)
                        .build();

        lobbyPlayerRepository.createLobbyPlayer(testLobbyPlayer);
    }

    @AfterAll
    void cleanup() {
        boolean isSuccessful = lobbyPlayerRepository.deletePlayersByLobbyId(testLobby.getId()) && lobbyRepository.deleteLobbyById(testLobby.getId());

        if (!isSuccessful) {
            fail("Failed to delete test lobby");
        }
    }

    @Test
    @Order(1)
    void testFindLobbyPlayerById() {
        LobbyPlayer foundLobbyPlayer = lobbyPlayerRepository.findLobbyPlayerById(testLobbyPlayer.getId());
        assertNotNull(foundLobbyPlayer);
        assertEquals(foundLobbyPlayer, testLobbyPlayer);
    }

    @Test
    @Order(2)
    void testFindPlayersByLobbyId() {
        List<LobbyPlayer> players = lobbyPlayerRepository.findPlayersByLobbyId(testLobby.getId());
        assertNotNull(players);
        assertTrue(players.contains(testLobbyPlayer));
    }

    @Test
    @Order(3)
    void testUpdateLobbyPlayer() {
        int newPoints = 250;
        testLobbyPlayer.setPoints(newPoints);

        boolean updateResult = lobbyPlayerRepository.updateLobbyPlayer(testLobbyPlayer);
        assertTrue(updateResult);

        LobbyPlayer updatedLobbyPlayer = lobbyPlayerRepository.findLobbyPlayerById(testLobbyPlayer.getId());
        assertNotNull(updatedLobbyPlayer);
        assertEquals(newPoints, updatedLobbyPlayer.getPoints());
    }
}