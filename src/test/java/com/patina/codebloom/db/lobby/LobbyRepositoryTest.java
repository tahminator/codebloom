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
    private LobbyPlayer testLobbyPlayer;
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
                        .discordId(String.valueOf(System.currentTimeMillis()))
                        .discordName("TestUser")
                        .build();

        userRepository.createUser(testUser);

        testLobby = Lobby.builder()
                        .joinCode(mockJoinCode)
                        .status(LobbyStatus.AVAILABLE)
                        .expiresAt(StandardizedOffsetDateTime.now().plusHours(1))
                        .playerCount(1)
                        .build();

        lobbyRepository.createLobby(testLobby);

        testLobbyPlayer = LobbyPlayer.builder()
                        .lobbyId(testLobby.getId())
                        .playerId(testUser.getId())
                        .build();

        lobbyPlayerRepository.createLobbyPlayer(testLobbyPlayer);
    }

    @AfterAll
    void cleanup() {
        assertTrue(lobbyPlayerRepository.deleteLobbyPlayerById(testLobbyPlayer.getId()));

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
        var foundLobby = lobbyRepository.findLobbyById(testLobby.getId()).orElseThrow();
        assertEquals(foundLobby, testLobby);
    }

    @Test
    @Order(2)
    void testfindAvailableLobbyByJoinCode() {
        Lobby foundLobby = lobbyRepository.findAvailableLobbyByJoinCode(mockJoinCode).orElseThrow();
        assertEquals(testLobby, foundLobby);
    }

    @Test
    @Order(3)
    void testFindActiveLobbyByJoinCode() {
        Lobby newActiveLobby = Lobby
                        .builder()
                        .joinCode("ABC123")
                        .status(LobbyStatus.ACTIVE)
                        .expiresAt(StandardizedOffsetDateTime.now())
                        .build();

        lobbyRepository.createLobby(newActiveLobby);

        Lobby foundLobby = lobbyRepository.findActiveLobbyByJoinCode("ABC123").orElseThrow();
        assertEquals(newActiveLobby, foundLobby);

        assertTrue(lobbyRepository.deleteLobbyById(newActiveLobby.getId()));
    }

    @Test
    @Order(4)
    void testFindLobbiesByStatus() {
        List<Lobby> availableLobbies = lobbyRepository.findLobbiesByStatus(LobbyStatus.AVAILABLE);
        assertNotNull(availableLobbies);
        assertTrue(availableLobbies.contains(testLobby));
    }

    @Test
    @Order(5)
    void testFindAvailableLobbies() {
        List<Lobby> availableLobbies = lobbyRepository.findAvailableLobbies();
        assertNotNull(availableLobbies);
        assertTrue(availableLobbies.contains(testLobby));
    }

    @Test
    @Order(6)
    void testUpdateLobby() {
        testLobby.setStatus(LobbyStatus.ACTIVE);
        testLobby.setPlayerCount(2);
        testLobby.setExpiresAt(StandardizedOffsetDateTime.now().plusHours(2));

        boolean updateResult = lobbyRepository.updateLobby(testLobby);
        assertTrue(updateResult);

        Lobby updatedLobby = lobbyRepository.findLobbyById(testLobby.getId()).orElseThrow();
        assertEquals(testLobby, updatedLobby);
    }

    @Test
    @Order(7)
    void testFindActiveLobbyByLobbyPlayerId() {
        var activeLobby = lobbyRepository.findActiveLobbyByLobbyPlayerPlayerId(testUser.getId()).orElseThrow();
        assertEquals(testLobby, activeLobby);
    }

    @Test
    @Order(8)
    void testFindAvailableLobbyByLobbyPlayerIdEmpty() {
        var activeLobby = lobbyRepository.findAvailableLobbyByLobbyPlayerPlayerId(testUser.getId());
        assertTrue(activeLobby.isEmpty());
    }

    @Test
    @Order(9)
    void testFindAvailableLobbyByLobbyPlayerIdMocked() {
        var u = User.builder()
                        .discordId(String.valueOf(System.currentTimeMillis()))
                        .discordName("TestUser2")
                        .build();

        userRepository.createUser(u);

        var l = Lobby.builder()
                        .joinCode("ABC123")
                        .status(LobbyStatus.AVAILABLE)
                        .expiresAt(StandardizedOffsetDateTime.now().plusHours(1))
                        .playerCount(1)
                        .build();

        lobbyRepository.createLobby(l);

        var lp = LobbyPlayer.builder()
                        .lobbyId(l.getId())
                        .playerId(u.getId())
                        .build();

        lobbyPlayerRepository.createLobbyPlayer(lp);

        var activeLobby = lobbyRepository.findAvailableLobbyByLobbyPlayerPlayerId(u.getId()).orElseThrow();
        assertEquals(l, activeLobby);
    }
}
