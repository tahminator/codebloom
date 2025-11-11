package com.patina.codebloom.db.lobby;

// CHECKSTYLE:OFF
import static org.junit.jupiter.api.Assertions.*;
// CHECKSTYLE:ON

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
import com.patina.codebloom.common.db.models.user.User;
import com.patina.codebloom.common.db.repos.lobby.LobbyRepository;
import com.patina.codebloom.common.db.repos.lobby.player.LobbyPlayerRepository;
import com.patina.codebloom.common.db.repos.user.UserRepository;
import com.patina.codebloom.common.time.StandardizedOffsetDateTime;
import com.patina.codebloom.db.BaseRepositoryTest;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
@Slf4j
public class LobbyRepositoryTest extends BaseRepositoryTest {
    private LobbyRepository lobbyRepository;
    private LobbyPlayerRepository lobbyPlayerRepository;
    private UserRepository userRepository;
    private Lobby testLobby;
    private User testUser;
    private String mockJoinCode = "TEST-" + UUID.randomUUID().toString().substring(0, 8);
    private String mockWinnerId = UUID.randomUUID().toString();

    @Autowired
    public LobbyRepositoryTest(final LobbyRepository lobbyRepository,
                    final LobbyPlayerRepository lobbyPlayerRepository,
                    final UserRepository userRepository) {
        this.lobbyRepository = lobbyRepository;
        this.lobbyPlayerRepository = lobbyPlayerRepository;
        this.userRepository = userRepository;
    }

    @BeforeAll
    void setup() {
        testUser = User.builder()
                        .id(UUID.randomUUID().toString())
                        .discordId(String.valueOf(System.currentTimeMillis()))
                        .discordName("TestUser")
                        .admin(false)
                        .verifyKey(UUID.randomUUID().toString())
                        .build();

        userRepository.createUser(testUser);

        testLobby = Lobby.builder()
                        .joinCode(mockJoinCode)
                        .status(LobbyStatus.AVAILABLE)
                        .expiresAt(StandardizedOffsetDateTime.now().plusHours(1))
                        .playerCount(1)
                        .winnerId(null)
                        .build();

        lobbyRepository.createLobby(testLobby);

        LobbyPlayer lobbyPlayer = LobbyPlayer.builder()
                        .id(UUID.randomUUID().toString())
                        .lobbyId(testLobby.getId())
                        .playerId(testUser.getId())
                        .points(0)
                        .build();

        lobbyPlayerRepository.createLobbyPlayer(lobbyPlayer);
    }

    @AfterAll
    void cleanup() {
        boolean lobbyDeleted = lobbyRepository.deleteLobbyById(testLobby.getId());

        if (!lobbyDeleted) {
            fail("Failed to delete test lobby");
        }

        boolean userDeleted = userRepository.deleteUserById(testUser.getId());

        if (!userDeleted) {
            fail("Failed to delete test user");
        }
    }

    @Test
    @Order(1)
    void testFindLobbyById() {
        Lobby foundLobby = lobbyRepository.findLobbyById(testLobby.getId());
        assertNotNull(foundLobby);
        assertEquals(foundLobby, testLobby);
    }

    @Test
    @Order(2)
    void testFindLobbyByJoinCode() {
        Lobby foundLobby = lobbyRepository.findLobbyByJoinCode(mockJoinCode);
        assertNotNull(foundLobby);
        assertEquals(testLobby.getId(), foundLobby.getId());
        assertEquals(mockJoinCode, foundLobby.getJoinCode());
    }

    @Test
    @Order(3)
    void testFindLobbiesByStatus() {
        List<Lobby> availableLobbies = lobbyRepository.findLobbiesByStatus(LobbyStatus.AVAILABLE);
        assertNotNull(availableLobbies);
        assertTrue(availableLobbies.contains(testLobby));
    }

    @Test
    @Order(4)
    void testFindAvailableLobbies() {
        List<Lobby> availableLobbies = lobbyRepository.findAvailableLobbies();
        assertNotNull(availableLobbies);
        assertTrue(availableLobbies.contains(testLobby));
    }

    @Test
    @Order(5)
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

    @Test
    @Order(6)
    void testFindActiveLobbyByLobbyPlayerId() {
        Lobby activeLobby = lobbyRepository.findActiveLobbyByLobbyPlayerId(testUser.getId());
        assertNotNull(activeLobby);
        assertEquals(testLobby.getId(), activeLobby.getId());
        assertEquals(LobbyStatus.ACTIVE, activeLobby.getStatus());
        assertEquals(mockJoinCode, activeLobby.getJoinCode());
    }

    @Test
    @Order(7)
    void testFindAvailableLobbyByLobbyPlayerIdEmpty() {
        Lobby activeLobby = lobbyRepository.findAvailableLobbyByLobbyPlayerId(testUser.getId());
        assertNull(activeLobby);
    }

    @Test
    @Order(7)
    void testFindAvailableLobbyByLobbyPlayerIdMocked() {
        Lobby activeLobby = lobbyRepository.findAvailableLobbyByLobbyPlayerId(testUser.getId());
        assertNull(activeLobby);
    }
}
