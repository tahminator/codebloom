package com.patina.codebloom.db.lobby;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.time.OffsetDateTime;
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
import com.patina.codebloom.common.db.repos.lobby.LobbyRepository;
import com.patina.codebloom.common.time.StandardizedOffsetDateTime;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
@Slf4j
public class LobbySqlRepositoryTest {
    private LobbyRepository lobbyRepository;
    private Lobby testLobby;
    private String mockJoinCode = "TEST-" + UUID.randomUUID().toString().substring(0, 8);
    private String mockWinnerId = UUID.randomUUID().toString();

    @Autowired
    public LobbySqlRepositoryTest(final LobbyRepository lobbyRepository) {
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
    }

    @AfterAll
    void cleanup() {
        boolean isSuccessful = lobbyRepository.deleteLobbyById(testLobby.getId());

        if (!isSuccessful) {
            fail("Failed to delete test lobby");
        }
    }

    @Test
    @Order(1)
    void testCreateLobby() {
        assertNotNull(testLobby.getId());
        assertNotNull(testLobby.getCreatedAt());
        assertEquals(mockJoinCode, testLobby.getJoinCode());
        assertEquals(LobbyStatus.AVAILABLE, testLobby.getStatus());
        assertEquals(1, testLobby.getPlayerCount());
        assertNull(testLobby.getWinnerId());
    }

    @Test
    @Order(2)
    void testFindLobbyById() {
        Lobby foundLobby = lobbyRepository.findLobbyById(testLobby.getId());
        assertNotNull(foundLobby);
        assertEquals(foundLobby, testLobby);
    }

    @Test
    @Order(3)
    void testFindLobbyByJoinCode() {
        Lobby foundLobby = lobbyRepository.findLobbyByJoinCode(mockJoinCode);
        assertNotNull(foundLobby);
        assertEquals(testLobby.getId(), foundLobby.getId());
        assertEquals(mockJoinCode, foundLobby.getJoinCode());
    }

    @Test
    @Order(4)
    void testFindLobbiesByStatus() {
        List<Lobby> availableLobbies = lobbyRepository.findLobbiesByStatus(LobbyStatus.AVAILABLE);
        assertNotNull(availableLobbies);
        assertTrue(availableLobbies.contains(testLobby));
    }

    @Test
    @Order(6)
    void testFindAvailableLobbies() {
        List<Lobby> availableLobbies = lobbyRepository.findAvailableLobbies();
        assertNotNull(availableLobbies);
        assertTrue(availableLobbies.contains(testLobby));
    }

    @Test
    @Order(7)
    void testUpdateLobby() {
        testLobby.setStatus(LobbyStatus.ACTIVE);
        testLobby.setPlayerCount(2);
        testLobby.setExpiresAt(StandardizedOffsetDateTime.now().plusHours(2));

        boolean updateResult = lobbyRepository.updateLobby(testLobby);
        assertTrue(updateResult);
        
        Lobby updatedLobby = lobbyRepository.findLobbyById(testLobby.getId());
        assertNotNull(updatedLobby);
        assertEquals(LobbyStatus.ACTIVE, updatedLobby.getStatus());
        assertEquals(2, updatedLobby.getPlayerCount());
        assertNull(updatedLobby.getWinnerId());
    }
}