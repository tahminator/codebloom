package com.patina.codebloom.component;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.ResponseEntity;

import com.github.javafaker.Faker;
import com.patina.codebloom.api.duel.DuelController;
import com.patina.codebloom.common.db.models.lobby.Lobby;
import com.patina.codebloom.common.db.models.lobby.LobbyStatus;
import com.patina.codebloom.common.db.models.lobby.player.LobbyPlayer;
import com.patina.codebloom.common.db.models.user.User;
import com.patina.codebloom.common.db.repos.lobby.LobbyRepository;
import com.patina.codebloom.common.db.repos.lobby.player.LobbyPlayerRepository;
import com.patina.codebloom.common.dto.ApiResponder;
import com.patina.codebloom.common.dto.Empty;
import com.patina.codebloom.common.env.Env;
import com.patina.codebloom.common.security.AuthenticationObject;
import com.patina.codebloom.common.utils.duel.PartyCodeGenerator;

public class DuelManagerTest {
    private final DuelController duelController;
    private final Faker faker;

    private LobbyRepository lobbyRepository = mock(LobbyRepository.class);
    private LobbyPlayerRepository lobbyPlayerRepository = mock(LobbyPlayerRepository.class);
    private Env env = mock(Env.class);

    public DuelManagerTest() {
        this.duelController = new DuelController(env, null, lobbyRepository, lobbyPlayerRepository);
        this.faker = Faker.instance();
    }

    private String randomUUID() {
        return UUID.randomUUID().toString();
    }

    private User createRandomUser() {
        return User.builder()
                        .id(randomUUID())
                        .discordId(String.valueOf(faker.number().randomNumber(18, true)))
                        .discordName(faker.name().username())
                        .leetcodeUsername(faker.name().username())
                        .admin(false)
                        .verifyKey(faker.crypto().md5())
                        .build();
    }

    private AuthenticationObject createAuthenticationObject(final User user) {
        return new AuthenticationObject(user, null);
    }

    @Test
    void testLeavePartySuccessWhenUserIsAloneInLobby() {
        when(env.isProd()).thenReturn(false);

        User user = createRandomUser();
        AuthenticationObject authObj = createAuthenticationObject(user);

        String lobbyId = randomUUID();
        LobbyPlayer existingLobbyPlayer = LobbyPlayer.builder()
                        .id(randomUUID())
                        .lobbyId(lobbyId)
                        .playerId(user.getId())
                        .points(0)
                        .build();

        Lobby lobby = Lobby.builder()
                        .id(lobbyId)
                        .joinCode(PartyCodeGenerator.generateCode())
                        .status(LobbyStatus.AVAILABLE)
                        .expiresAt(OffsetDateTime.now().plusMinutes(30))
                        .playerCount(1)
                        .build();

        when(lobbyPlayerRepository.findLobbyPlayerByPlayerId(user.getId()))
                        .thenReturn(existingLobbyPlayer);
        when(lobbyPlayerRepository.deleteLobbyPlayerById(existingLobbyPlayer.getId()))
                        .thenReturn(true);
        when(lobbyRepository.findLobbyById(lobbyId))
                        .thenReturn(lobby);

        ResponseEntity<ApiResponder<Empty>> response = duelController.leaveParty(authObj);

        assertEquals(200, response.getStatusCode().value());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Successfully left the lobby.", response.getBody().getMessage());

        verify(lobbyPlayerRepository, times(1)).deleteLobbyPlayerById(existingLobbyPlayer.getId());
        verify(lobbyRepository, times(1)).deleteLobbyById(lobbyId);
        verify(lobbyRepository, times(0)).updateLobby(any());
    }

    @Test
    void testLeavePartySuccessWhenUserInPartyAndLobbyHasMultiplePlayers() {
        when(env.isProd()).thenReturn(false);

        User user = createRandomUser();
        AuthenticationObject authObj = createAuthenticationObject(user);

        String lobbyId = randomUUID();
        LobbyPlayer existingLobbyPlayer = LobbyPlayer.builder()
                        .id(randomUUID())
                        .lobbyId(lobbyId)
                        .playerId(user.getId())
                        .points(50)
                        .build();

        Lobby lobby = Lobby.builder()
                        .id(lobbyId)
                        .joinCode(PartyCodeGenerator.generateCode())
                        .status(LobbyStatus.AVAILABLE)
                        .expiresAt(OffsetDateTime.now().plusMinutes(30))
                        .playerCount(3)
                        .build();

        when(lobbyPlayerRepository.findLobbyPlayerByPlayerId(user.getId()))
                        .thenReturn(existingLobbyPlayer);
        when(lobbyPlayerRepository.deleteLobbyPlayerById(existingLobbyPlayer.getId()))
                        .thenReturn(true);
        when(lobbyRepository.findLobbyById(lobbyId))
                        .thenReturn(lobby);

        ResponseEntity<ApiResponder<Empty>> response = duelController.leaveParty(authObj);

        assertEquals(200, response.getStatusCode().value());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Successfully left the lobby.", response.getBody().getMessage());

        verify(lobbyPlayerRepository, times(1)).deleteLobbyPlayerById(existingLobbyPlayer.getId());
        verify(lobbyRepository, times(1)).updateLobby(any(Lobby.class));
        verify(lobbyRepository, times(0)).deleteLobbyById(any());
    }

    @Test
    void testLeavePartySuccessWhenLobbyDoesNotExists() {
        when(env.isProd()).thenReturn(false);

        User user = createRandomUser();
        AuthenticationObject authObj = createAuthenticationObject(user);

        String lobbyId = randomUUID();
        LobbyPlayer existingLobbyPlayer = LobbyPlayer.builder()
                        .id(randomUUID())
                        .lobbyId(lobbyId)
                        .playerId(user.getId())
                        .points(50)
                        .build();

        when(lobbyPlayerRepository.findLobbyPlayerByPlayerId(user.getId()))
                        .thenReturn(existingLobbyPlayer);
        when(lobbyPlayerRepository.deleteLobbyPlayerById(existingLobbyPlayer.getId()))
                        .thenReturn(true);
        when(lobbyRepository.findLobbyById(lobbyId))
                        .thenReturn(null);

        ResponseEntity<ApiResponder<Empty>> response = duelController.leaveParty(authObj);

        assertEquals(200, response.getStatusCode().value());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Successfully left the lobby.", response.getBody().getMessage());

        verify(lobbyPlayerRepository, times(1)).deleteLobbyPlayerById(existingLobbyPlayer.getId());
        verify(lobbyRepository, times(0)).updateLobby(any());
        verify(lobbyRepository, times(0)).deleteLobbyById(any());
    }

    @Test
    void testLeavePartyDecreasesPlayerCountCorrectly() {
        when(env.isProd()).thenReturn(false);

        User user = createRandomUser();
        AuthenticationObject authObj = createAuthenticationObject(user);

        String lobbyId = randomUUID();
        LobbyPlayer existingLobbyPlayer = LobbyPlayer.builder()
                        .id(randomUUID())
                        .lobbyId(lobbyId)
                        .playerId(user.getId())
                        .points(0)
                        .build();

        Lobby lobby = Lobby.builder()
                        .id(lobbyId)
                        .joinCode(PartyCodeGenerator.generateCode())
                        .status(LobbyStatus.AVAILABLE)
                        .expiresAt(OffsetDateTime.now().plusMinutes(30))
                        .playerCount(3)
                        .build();

        when(lobbyPlayerRepository.findLobbyPlayerByPlayerId(user.getId()))
                        .thenReturn(existingLobbyPlayer);
        when(lobbyPlayerRepository.deleteLobbyPlayerById(existingLobbyPlayer.getId()))
                        .thenReturn(true);
        when(lobbyRepository.findLobbyById(lobbyId))
                        .thenReturn(lobby);

        ResponseEntity<ApiResponder<Empty>> response = duelController.leaveParty(authObj);

        assertEquals(200, response.getStatusCode().value());
        assertTrue(response.getBody().isSuccess());

        ArgumentCaptor<Lobby> lobbyCaptor = ArgumentCaptor.forClass(Lobby.class);
        verify(lobbyRepository, times(1)).updateLobby(lobbyCaptor.capture());

        Lobby updatedLobby = lobbyCaptor.getValue();
        assertEquals(2, updatedLobby.getPlayerCount());

        verify(lobbyRepository, times(0)).deleteLobbyById(any());
    }

    @Test
    void testLeavePartyFailureWhenUserNotInLobby() {
        when(env.isProd()).thenReturn(false);

        User user = createRandomUser();
        AuthenticationObject authObj = createAuthenticationObject(user);

        when(lobbyPlayerRepository.findLobbyPlayerByPlayerId(user.getId()))
                        .thenReturn(null);

        ResponseEntity<ApiResponder<Empty>> response = duelController.leaveParty(authObj);

        assertEquals(400, response.getStatusCode().value());
        assertFalse(response.getBody().isSuccess());
        assertEquals("You are not currently in a lobby.", response.getBody().getMessage());

        verify(lobbyPlayerRepository, times(0)).deleteLobbyPlayerById(any());
        verify(lobbyRepository, times(0)).updateLobby(any());
        verify(lobbyRepository, times(0)).deleteLobbyById(any());
    }

    @Test
    void testLeavePartyFailureWhenDeletionFails() {
        when(env.isProd()).thenReturn(false);

        User user = createRandomUser();
        AuthenticationObject authObj = createAuthenticationObject(user);

        String lobbyId = randomUUID();
        LobbyPlayer existingLobbyPlayer = LobbyPlayer.builder()
                        .id(randomUUID())
                        .lobbyId(lobbyId)
                        .playerId(user.getId())
                        .points(50)
                        .build();

        when(lobbyPlayerRepository.findLobbyPlayerByPlayerId(user.getId()))
                        .thenReturn(existingLobbyPlayer);
        when(lobbyPlayerRepository.deleteLobbyPlayerById(existingLobbyPlayer.getId()))
                        .thenReturn(false);

        ResponseEntity<ApiResponder<Empty>> response = duelController.leaveParty(authObj);

        assertEquals(500, response.getStatusCode().value());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Failed to leave the lobby. Please try again.", response.getBody().getMessage());

        verify(lobbyPlayerRepository, times(1)).deleteLobbyPlayerById(existingLobbyPlayer.getId());
        verify(lobbyRepository, times(0)).findLobbyById(any());
        verify(lobbyRepository, times(0)).updateLobby(any());
        verify(lobbyRepository, times(0)).deleteLobbyById(any());
    }

    @Test
    void testLeavePartyFailureInProductionEnvironment() {
        when(env.isProd()).thenReturn(true);

        User user = createRandomUser();
        AuthenticationObject authObj = createAuthenticationObject(user);

        org.springframework.web.server.ResponseStatusException exception = assertThrows(
                        org.springframework.web.server.ResponseStatusException.class,
                        () -> duelController.leaveParty(authObj));

        assertEquals(403, exception.getStatusCode().value());
        assertEquals("Endpoint is currently non-functional", exception.getReason());

        verify(lobbyPlayerRepository, times(0)).findLobbyPlayerByPlayerId(any());
        verify(lobbyPlayerRepository, times(0)).deleteLobbyPlayerById(any());
        verify(lobbyRepository, times(0)).findLobbyById(any());
        verify(lobbyRepository, times(0)).updateLobby(any());
        verify(lobbyRepository, times(0)).deleteLobbyById(any());
    }

    @Test
    void testCreatePartySuccessWhenUserNotInParty() {
        when(env.isProd()).thenReturn(false);

        User user = createRandomUser();
        AuthenticationObject authObj = createAuthenticationObject(user);

        when(lobbyPlayerRepository.findLobbyPlayerByPlayerId(user.getId()))
                        .thenReturn(null);

        ResponseEntity<ApiResponder<Empty>> response = duelController.createParty(authObj);

        assertEquals(200, response.getStatusCode().value());
        assertTrue(response.getBody().isSuccess());
        assertTrue(response.getBody().getPayload() instanceof Empty);

        ArgumentCaptor<Lobby> lobbyCaptor = ArgumentCaptor.forClass(Lobby.class);
        ArgumentCaptor<LobbyPlayer> playerCaptor = ArgumentCaptor.forClass(LobbyPlayer.class);

        verify(lobbyRepository, times(1)).createLobby(lobbyCaptor.capture());
        verify(lobbyPlayerRepository, times(1)).createLobbyPlayer(playerCaptor.capture());

        Lobby createdLobby = lobbyCaptor.getValue();
        assertNotNull(createdLobby.getJoinCode());
        assertEquals(6, createdLobby.getJoinCode().length());
        assertEquals(LobbyStatus.AVAILABLE, createdLobby.getStatus());
        assertEquals(1, createdLobby.getPlayerCount());
        assertNull(createdLobby.getWinnerId());
        assertNotNull(createdLobby.getExpiresAt());

        LobbyPlayer createdPlayer = playerCaptor.getValue();
        assertEquals(user.getId(), createdPlayer.getPlayerId());
        assertEquals(0, createdPlayer.getPoints());
    }

    @Test
    void testCreatePartyFailureWhenUserAlreadyInParty() {
        when(env.isProd()).thenReturn(false);

        User user = createRandomUser();
        AuthenticationObject authObj = createAuthenticationObject(user);

        LobbyPlayer existingLobbyPlayer = LobbyPlayer.builder()
                        .id(randomUUID())
                        .lobbyId(randomUUID())
                        .playerId(user.getId())
                        .points(100)
                        .build();

        when(lobbyPlayerRepository.findLobbyPlayerByPlayerId(user.getId()))
                        .thenReturn(existingLobbyPlayer);

        ResponseEntity<ApiResponder<Empty>> response = duelController.createParty(authObj);

        assertEquals(400, response.getStatusCode().value());
        assertFalse(response.getBody().isSuccess());
        assertEquals("You are already in a lobby. Please leave your current lobby before creating a new one.",
                        response.getBody().getMessage());

        verify(lobbyRepository, times(0)).createLobby(any());
        verify(lobbyPlayerRepository, times(0)).createLobbyPlayer(any());
    }

    @Test
    void testMultipleUsersCanCreatePartiesIndependently() {
        when(env.isProd()).thenReturn(false);

        User user1 = createRandomUser();
        User user2 = createRandomUser();
        AuthenticationObject authObj1 = createAuthenticationObject(user1);
        AuthenticationObject authObj2 = createAuthenticationObject(user2);

        when(lobbyPlayerRepository.findLobbyPlayerByPlayerId(user1.getId()))
                        .thenReturn(null);
        when(lobbyPlayerRepository.findLobbyPlayerByPlayerId(user2.getId()))
                        .thenReturn(null);

        ResponseEntity<ApiResponder<Empty>> response1 = duelController.createParty(authObj1);
        ResponseEntity<ApiResponder<Empty>> response2 = duelController.createParty(authObj2);

        assertEquals(200, response1.getStatusCode().value());
        assertEquals(200, response2.getStatusCode().value());
        assertTrue(response1.getBody().isSuccess());
        assertTrue(response2.getBody().isSuccess());

        ArgumentCaptor<Lobby> lobbyCaptor = ArgumentCaptor.forClass(Lobby.class);
        ArgumentCaptor<LobbyPlayer> playerCaptor = ArgumentCaptor.forClass(LobbyPlayer.class);

        verify(lobbyRepository, times(2)).createLobby(lobbyCaptor.capture());
        verify(lobbyPlayerRepository, times(2)).createLobbyPlayer(playerCaptor.capture());

        var lobbyPlayers = playerCaptor.getAllValues();
        assertEquals(user1.getId(), lobbyPlayers.get(0).getPlayerId());
        assertEquals(user2.getId(), lobbyPlayers.get(1).getPlayerId());
    }

    @Test
    void testEachPartyHasUniqueJoinCode() {
        when(env.isProd()).thenReturn(false);

        User user1 = createRandomUser();
        User user2 = createRandomUser();
        AuthenticationObject authObj1 = createAuthenticationObject(user1);
        AuthenticationObject authObj2 = createAuthenticationObject(user2);

        when(lobbyPlayerRepository.findLobbyPlayerByPlayerId(user1.getId()))
                        .thenReturn(null);
        when(lobbyPlayerRepository.findLobbyPlayerByPlayerId(user2.getId()))
                        .thenReturn(null);

        duelController.createParty(authObj1);
        duelController.createParty(authObj2);

        ArgumentCaptor<Lobby> lobbyCaptor = ArgumentCaptor.forClass(Lobby.class);
        verify(lobbyRepository, times(2)).createLobby(lobbyCaptor.capture());

        var lobbies = lobbyCaptor.getAllValues();
        assertNotEquals(lobbies.get(0).getJoinCode(), lobbies.get(1).getJoinCode());
    }

    @Test
    void testPartyExpirationTimeIsSet30MinutesInFuture() {
        when(env.isProd()).thenReturn(false);

        User user = createRandomUser();
        AuthenticationObject authObj = createAuthenticationObject(user);

        when(lobbyPlayerRepository.findLobbyPlayerByPlayerId(user.getId()))
                        .thenReturn(null);

        OffsetDateTime beforeCreation = OffsetDateTime.now();

        duelController.createParty(authObj);

        OffsetDateTime afterCreation = OffsetDateTime.now();

        ArgumentCaptor<Lobby> lobbyCaptor = ArgumentCaptor.forClass(Lobby.class);
        verify(lobbyRepository, times(1)).createLobby(lobbyCaptor.capture());

        Lobby createdLobby = lobbyCaptor.getValue();
        OffsetDateTime expiresAt = createdLobby.getExpiresAt();

        OffsetDateTime expectedExpiration = beforeCreation.plusMinutes(30);
        assertTrue(expiresAt.isAfter(expectedExpiration.minusSeconds(5)),
                        "Expiration time should be after expected 30 minute mark");
        assertTrue(expiresAt.isBefore(afterCreation.plusMinutes(30).plusSeconds(5)),
                        "Expiration time should be before expected 30 minute mark plus buffer");
    }

    @Test
    void testCreatePartyFailsInProductionEnvironment() {
        when(env.isProd()).thenReturn(true);

        User user = createRandomUser();
        AuthenticationObject authObj = createAuthenticationObject(user);

        org.springframework.web.server.ResponseStatusException exception = assertThrows(
                        org.springframework.web.server.ResponseStatusException.class,
                        () -> duelController.createParty(authObj));

        assertEquals(403, exception.getStatusCode().value());
        assertEquals("Endpoint is currently non-functional", exception.getReason());

        verify(lobbyPlayerRepository, times(0)).findLobbyPlayerByPlayerId(any());
        verify(lobbyRepository, times(0)).createLobby(any());
        verify(lobbyPlayerRepository, times(0)).createLobbyPlayer(any());
    }

    @Test
    void testNewLobbyPlayerStartsWithZeroPoints() {
        when(env.isProd()).thenReturn(false);

        User user = createRandomUser();
        AuthenticationObject authObj = createAuthenticationObject(user);

        when(lobbyPlayerRepository.findLobbyPlayerByPlayerId(user.getId()))
                        .thenReturn(null);

        ResponseEntity<ApiResponder<Empty>> response = duelController.createParty(authObj);

        assertEquals(200, response.getStatusCode().value());

        ArgumentCaptor<LobbyPlayer> playerCaptor = ArgumentCaptor.forClass(LobbyPlayer.class);
        verify(lobbyPlayerRepository, times(1)).createLobbyPlayer(playerCaptor.capture());

        LobbyPlayer createdPlayer = playerCaptor.getValue();
        assertEquals(0, createdPlayer.getPoints());
    }

    @Test
    void testCreatePartyInitializesLobbyWithoutWinner() {
        when(env.isProd()).thenReturn(false);

        User user = createRandomUser();
        AuthenticationObject authObj = createAuthenticationObject(user);

        when(lobbyPlayerRepository.findLobbyPlayerByPlayerId(user.getId()))
                        .thenReturn(null);

        ResponseEntity<ApiResponder<Empty>> response = duelController.createParty(authObj);

        assertEquals(200, response.getStatusCode().value());

        ArgumentCaptor<Lobby> lobbyCaptor = ArgumentCaptor.forClass(Lobby.class);
        verify(lobbyRepository, times(1)).createLobby(lobbyCaptor.capture());

        Lobby createdLobby = lobbyCaptor.getValue();
        assertNull(createdLobby.getWinnerId());
    }

    @Test
    void testSuccessResponseContainsJoinCode() {
        when(env.isProd()).thenReturn(false);

        User user = createRandomUser();
        AuthenticationObject authObj = createAuthenticationObject(user);

        when(lobbyPlayerRepository.findLobbyPlayerByPlayerId(user.getId()))
                        .thenReturn(null);

        ResponseEntity<ApiResponder<Empty>> response = duelController.createParty(authObj);

        assertEquals(200, response.getStatusCode().value());
        assertTrue(response.getBody().isSuccess());
        assertTrue(response.getBody().getMessage().contains("Lobby created successfully"),
                        "Message should indicate successful lobby creation");
        assertTrue(response.getBody().getMessage().contains("join code"),
                        "Message should mention join code");
    }

}
