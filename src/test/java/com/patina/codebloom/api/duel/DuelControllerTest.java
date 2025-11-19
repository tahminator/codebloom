package com.patina.codebloom.api.duel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import com.github.javafaker.Faker;
import com.patina.codebloom.api.duel.body.JoinLobbyBody;
import com.patina.codebloom.common.components.DuelManager;
import com.patina.codebloom.common.db.models.lobby.Lobby;
import com.patina.codebloom.common.db.models.lobby.LobbyStatus;
import com.patina.codebloom.common.db.models.lobby.player.LobbyPlayer;
import com.patina.codebloom.common.db.models.user.User;
import com.patina.codebloom.common.db.repos.lobby.LobbyRepository;
import com.patina.codebloom.common.db.repos.lobby.player.LobbyPlayerRepository;
import com.patina.codebloom.common.dto.ApiResponder;
import com.patina.codebloom.common.dto.Empty;
import com.patina.codebloom.common.dto.lobby.DuelData;
import com.patina.codebloom.common.env.Env;
import com.patina.codebloom.common.security.AuthenticationObject;
import com.patina.codebloom.common.utils.duel.PartyCodeGenerator;
import com.patina.codebloom.common.utils.sse.SseWrapper;
import com.patina.codebloom.scheduled.pg.handler.LobbyNotifyHandler;
import com.patina.codebloom.utilities.exception.ValidationException;

public class DuelControllerTest {
    private final DuelController duelController;
    private final Faker faker;

    private DuelManager duelManager = mock(DuelManager.class);
    private LobbyRepository lobbyRepository = mock(LobbyRepository.class);
    private LobbyPlayerRepository lobbyPlayerRepository = mock(LobbyPlayerRepository.class);
    private Env env = mock(Env.class);
    private LobbyNotifyHandler lobbyNotifyHandler = mock(LobbyNotifyHandler.class);

    public DuelControllerTest() {
        this.duelController = new DuelController(env, duelManager, lobbyRepository, lobbyPlayerRepository, lobbyNotifyHandler);
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
        verify(lobbyRepository, times(0)).deleteLobbyById(any());

        ArgumentCaptor<Lobby> lobbyCaptor = ArgumentCaptor.forClass(Lobby.class);
        verify(lobbyRepository, times(1)).updateLobby(lobbyCaptor.capture());

        Lobby updatedLobby = lobbyCaptor.getValue();
        assertEquals(2, updatedLobby.getPlayerCount());
        assertEquals(LobbyStatus.AVAILABLE, updatedLobby.getStatus());
    }

    @Test
    void testLeavePartySuccessWhenLobbyDoesNotExist() {
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
        assertEquals(LobbyStatus.AVAILABLE, updatedLobby.getStatus());

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
    void testLeavePartySetsLobbyToClosedWhenLastPlayerLeaves() {
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

        ArgumentCaptor<Lobby> lobbyCaptor = ArgumentCaptor.forClass(Lobby.class);
        verify(lobbyRepository, times(1)).updateLobby(lobbyCaptor.capture());

        Lobby updatedLobby = lobbyCaptor.getValue();
        assertEquals(0, updatedLobby.getPlayerCount());
        assertEquals(LobbyStatus.CLOSED, updatedLobby.getStatus());

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

        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
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

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
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

        assertEquals(HttpStatus.FORBIDDEN.value(), exception.getStatusCode().value());
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

        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());

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

        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());

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

        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        assertTrue(response.getBody().isSuccess());
        assertTrue(response.getBody().getMessage().contains("Lobby created successfully"),
                        "Message should indicate successful lobby creation");
        assertTrue(response.getBody().getMessage().contains("join code"),
                        "Message should mention join code");
    }

    @Test
    @DisplayName("Join lobby - cannot find lobby by join code")
    void joinPartyCannotFindLobbyByJoinCode() {
        when(env.isProd()).thenReturn(false);

        var joinPartyBody = JoinLobbyBody.builder()
                        .partyCode("ABC123")
                        .build();

        User user = createRandomUser();
        AuthenticationObject authObj = createAuthenticationObject(user);

        when(lobbyRepository.findAvailableLobbyByJoinCode(
                        argThat(joinPartyBody.getPartyCode()::equals))).thenReturn(null);

        assertThrows(ResponseStatusException.class, () -> {
            duelController.joinLobby(authObj, joinPartyBody);
        });
    }

    @Test
    @DisplayName("Join lobby - invalid code length")
    void joinPartyIncorrectLengthCode() {
        when(env.isProd()).thenReturn(false);

        var joinPartyBody = JoinLobbyBody.builder()
                        .partyCode("ABC12")
                        .build();

        User user = createRandomUser();
        AuthenticationObject authObj = createAuthenticationObject(user);

        var ex = assertThrows(ValidationException.class, () -> {
            duelController.joinLobby(authObj, joinPartyBody);
        });

        assertEquals("Lobby code must be exactly 6 characters.", ex.getMessage());
    }

    @Test
    @DisplayName("Join lobby - empty code")
    void joinLobbyEmptyCode() {
        when(env.isProd()).thenReturn(false);

        var joinPartyBody = JoinLobbyBody.builder()
                        .partyCode("")
                        .build();

        User user = createRandomUser();
        AuthenticationObject authObj = createAuthenticationObject(user);

        var ex = assertThrows(ValidationException.class, () -> {
            duelController.joinLobby(authObj, joinPartyBody);
        });

        assertEquals("Lobby code may not be null or empty.", ex.getMessage());
    }

    @Test
    @DisplayName("Join lobby - null code")
    void joinLobbyNullCode() {
        when(env.isProd()).thenReturn(false);

        var joinPartyBody = JoinLobbyBody.builder()
                        .partyCode(null)
                        .build();

        User user = createRandomUser();
        AuthenticationObject authObj = createAuthenticationObject(user);

        var ex = assertThrows(ValidationException.class, () -> {
            duelController.joinLobby(authObj, joinPartyBody);
        });

        assertEquals("Lobby code may not be null or empty.", ex.getMessage());
    }

    @Test
    @DisplayName("Join lobby - successful join")
    void joinLobbySuccess() {
        when(env.isProd()).thenReturn(false);

        var joinPartyBody = JoinLobbyBody.builder()
                        .partyCode("ABC123")
                        .build();

        User user = createRandomUser();
        AuthenticationObject authObj = createAuthenticationObject(user);

        Lobby mockLobby = Lobby.builder()
                        .id(randomUUID())
                        .joinCode("ABC123")
                        .status(LobbyStatus.AVAILABLE)
                        .playerCount(1)
                        .build();

        when(lobbyRepository.findAvailableLobbyByJoinCode("ABC123")).thenReturn(mockLobby);
        when(lobbyRepository.findAvailableLobbyByLobbyPlayerId(user.getId())).thenReturn(null);
        when(lobbyRepository.findActiveLobbyByLobbyPlayerId(user.getId())).thenReturn(null);
        when(lobbyRepository.updateLobby(any(Lobby.class))).thenReturn(true);

        ResponseEntity<ApiResponder<Empty>> response = duelController.joinLobby(authObj, joinPartyBody);

        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Party successfully joined!", response.getBody().getMessage());

        verify(lobbyPlayerRepository, times(1)).createLobbyPlayer(any(LobbyPlayer.class));
        verify(lobbyRepository, times(1)).updateLobby(argThat(lobby -> lobby.getPlayerCount() == 2));
    }

    @Test
    @DisplayName("Join lobby - lobby at max capacity")
    void joinLobbyMaxCapacity() {
        when(env.isProd()).thenReturn(false);

        var joinPartyBody = JoinLobbyBody.builder()
                        .partyCode("ABC123")
                        .build();

        User user = createRandomUser();
        AuthenticationObject authObj = createAuthenticationObject(user);

        Lobby mockLobby = Lobby.builder()
                        .id(randomUUID())
                        .joinCode("ABC123")
                        .status(LobbyStatus.AVAILABLE)
                        .playerCount(2)
                        .build();

        when(lobbyRepository.findAvailableLobbyByJoinCode("ABC123")).thenReturn(mockLobby);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            duelController.joinLobby(authObj, joinPartyBody);
        });

        assertEquals(HttpStatus.CONFLICT.value(), exception.getStatusCode().value());
        assertEquals("This lobby already has the maximum number of players", exception.getReason());

        verify(lobbyPlayerRepository, times(0)).createLobbyPlayer(any());
        verify(lobbyRepository, times(0)).updateLobby(any());
    }

    @Test
    @DisplayName("Join lobby - player already in available lobby")
    void joinLobbyPlayerInAvailableLobby() {
        when(env.isProd()).thenReturn(false);

        var joinPartyBody = JoinLobbyBody.builder()
                        .partyCode("ABC123")
                        .build();

        User user = createRandomUser();
        AuthenticationObject authObj = createAuthenticationObject(user);

        Lobby mockLobby = Lobby.builder()
                        .id(randomUUID())
                        .joinCode("ABC123")
                        .status(LobbyStatus.AVAILABLE)
                        .playerCount(1)
                        .build();

        Lobby existingLobby = Lobby.builder()
                        .id(randomUUID())
                        .status(LobbyStatus.AVAILABLE)
                        .build();

        when(lobbyRepository.findAvailableLobbyByJoinCode("ABC123")).thenReturn(mockLobby);
        when(lobbyRepository.findAvailableLobbyByLobbyPlayerId(user.getId())).thenReturn(existingLobby);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            duelController.joinLobby(authObj, joinPartyBody);
        });

        assertEquals(HttpStatus.CONFLICT.value(), exception.getStatusCode().value());
        assertEquals("You are already in a party. Please leave the party, then try again.", exception.getReason());

        verify(lobbyPlayerRepository, times(0)).createLobbyPlayer(any());
        verify(lobbyRepository, times(0)).updateLobby(any());
    }

    @Test
    @DisplayName("Join lobby - player already in active lobby")
    void joinLobbyPlayerInActiveLobby() {
        when(env.isProd()).thenReturn(false);

        var joinPartyBody = JoinLobbyBody.builder()
                        .partyCode("ABC123")
                        .build();

        User user = createRandomUser();
        AuthenticationObject authObj = createAuthenticationObject(user);

        Lobby mockLobby = Lobby.builder()
                        .id(randomUUID())
                        .joinCode("ABC123")
                        .status(LobbyStatus.AVAILABLE)
                        .playerCount(1)
                        .build();

        Lobby activeLobby = Lobby.builder()
                        .id(randomUUID())
                        .status(LobbyStatus.ACTIVE)
                        .build();

        when(lobbyRepository.findAvailableLobbyByJoinCode("ABC123")).thenReturn(mockLobby);
        when(lobbyRepository.findAvailableLobbyByLobbyPlayerId(user.getId())).thenReturn(null);
        when(lobbyRepository.findActiveLobbyByLobbyPlayerId(user.getId())).thenReturn(activeLobby);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            duelController.joinLobby(authObj, joinPartyBody);
        });

        assertEquals(HttpStatus.CONFLICT.value(), exception.getStatusCode().value());
        assertEquals("You are currently in a duel. Please forfeit the duel, then try again.", exception.getReason());

        verify(lobbyPlayerRepository, times(0)).createLobbyPlayer(any());
        verify(lobbyRepository, times(0)).updateLobby(any());
    }

    @Test
    @DisplayName("Join lobby - lobby update failure")
    void joinLobbyUpdateFailure() {
        when(env.isProd()).thenReturn(false);

        var joinPartyBody = JoinLobbyBody.builder()
                        .partyCode("ABC123")
                        .build();

        User user = createRandomUser();
        AuthenticationObject authObj = createAuthenticationObject(user);

        Lobby mockLobby = Lobby.builder()
                        .id(randomUUID())
                        .joinCode("ABC123")
                        .status(LobbyStatus.AVAILABLE)
                        .playerCount(1)
                        .build();

        when(lobbyRepository.findAvailableLobbyByJoinCode("ABC123")).thenReturn(mockLobby);
        when(lobbyRepository.findAvailableLobbyByLobbyPlayerId(user.getId())).thenReturn(null);
        when(lobbyRepository.findActiveLobbyByLobbyPlayerId(user.getId())).thenReturn(null);
        when(lobbyRepository.updateLobby(any(Lobby.class))).thenReturn(false);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            duelController.joinLobby(authObj, joinPartyBody);
        });

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), exception.getStatusCode().value());
        assertEquals("Failed to join party. Please try again later.", exception.getReason());

        verify(lobbyPlayerRepository, times(1)).createLobbyPlayer(any(LobbyPlayer.class));
        verify(lobbyRepository, times(1)).updateLobby(any(Lobby.class));
    }

    @Test
    @DisplayName("Join lobby - fails in production environment")
    void joinLobbyFailsInProduction() {
        when(env.isProd()).thenReturn(true);

        var joinPartyBody = JoinLobbyBody.builder()
                        .partyCode("ABC123")
                        .build();

        User user = createRandomUser();
        AuthenticationObject authObj = createAuthenticationObject(user);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            duelController.joinLobby(authObj, joinPartyBody);
        });

        assertEquals(HttpStatus.FORBIDDEN.value(), exception.getStatusCode().value());
        assertEquals("Endpoint is currently non-functional", exception.getReason());

        verify(lobbyRepository, times(0)).findAvailableLobbyByJoinCode(any());
        verify(lobbyPlayerRepository, times(0)).createLobbyPlayer(any());
        verify(lobbyRepository, times(0)).updateLobby(any());
    }

    @Test
    @DisplayName("Join lobby - lobby has expired")
    void joinLobbyExpiredLobby() {
        when(env.isProd()).thenReturn(false);

        var joinPartyBody = JoinLobbyBody.builder()
                        .partyCode("ABC123")
                        .build();

        User user = createRandomUser();
        AuthenticationObject authObj = createAuthenticationObject(user);

        OffsetDateTime pastTime = OffsetDateTime.now().minusMinutes(5);
        Lobby expiredLobby = Lobby.builder()
                        .id(randomUUID())
                        .joinCode("ABC123")
                        .status(LobbyStatus.AVAILABLE)
                        .playerCount(1)
                        .expiresAt(pastTime)
                        .build();

        when(lobbyRepository.findAvailableLobbyByJoinCode("ABC123")).thenReturn(expiredLobby);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            duelController.joinLobby(authObj, joinPartyBody);
        });

        assertEquals(HttpStatus.GONE.value(), exception.getStatusCode().value());
        assertEquals("The lobby has expired and cannot be joined.", exception.getReason());

        verify(lobbyPlayerRepository, times(0)).createLobbyPlayer(any());
        verify(lobbyRepository, times(0)).updateLobby(any());
    }

    @Test
    @DisplayName("New Question endpoint - getting new question fails in production")
    void getNewQuestionFailsInProduction() throws IOException {
        when(env.isProd()).thenReturn(true);

        User user = createRandomUser();
        AuthenticationObject authObj = createAuthenticationObject(user);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            duelController.getNewQuestion(authObj);
        });

        assertEquals(HttpStatus.FORBIDDEN.value(), exception.getStatusCode().value());
        assertEquals("Endpoint is currently non-functional", exception.getReason());

        verify(lobbyPlayerRepository, times(0)).findLobbyPlayerByPlayerId(any());
        verify(lobbyRepository, times(0)).findLobbyById(any());
        verify(duelManager, times(0)).assignNewQuestionToLobby(any());
        verify(lobbyNotifyHandler, times(0)).handle(any());
    }

    @Test
    @DisplayName("New Question endpoint - succeeds when all players completed current question")
    void getNewQuestionSuccessAllPlayersCompleted() throws Exception {
        when(env.isProd()).thenReturn(false);

        User user = createRandomUser();
        AuthenticationObject authObj = createAuthenticationObject(user);

        String lobbyId = randomUUID();
        LobbyPlayer lobbyPlayer = LobbyPlayer.builder()
                        .id(randomUUID())
                        .lobbyId(lobbyId)
                        .playerId(user.getId())
                        .points(50)
                        .build();

        Lobby lobby = Lobby.builder()
                        .id(lobbyId)
                        .joinCode(PartyCodeGenerator.generateCode())
                        .status(LobbyStatus.ACTIVE)
                        .playerCount(2)
                        .build();

        LobbyPlayer player1 = LobbyPlayer.builder()
                        .id(randomUUID())
                        .lobbyId(lobbyId)
                        .playerId(user.getId())
                        .points(50)
                        .build();

        LobbyPlayer player2 = LobbyPlayer.builder()
                        .id(randomUUID())
                        .lobbyId(lobbyId)
                        .playerId(randomUUID())
                        .points(100)
                        .build();

        when(lobbyPlayerRepository.findLobbyPlayerByPlayerId(user.getId())).thenReturn(lobbyPlayer);
        when(lobbyRepository.findLobbyById(lobbyId)).thenReturn(lobby);
        when(lobbyPlayerRepository.findPlayersByLobbyId(lobbyId)).thenReturn(List.of(player1, player2));
        doNothing().when(duelManager).assignNewQuestionToLobby(lobbyId);
        doNothing().when(lobbyNotifyHandler).handle(lobbyId);

        ResponseEntity<ApiResponder<Empty>> response = duelController.getNewQuestion(authObj);

        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        assertTrue(response.getBody().isSuccess());
        assertEquals("New question assigned to all players.", response.getBody().getMessage());

        verify(lobbyPlayerRepository, times(1)).findLobbyPlayerByPlayerId(user.getId());
        verify(lobbyRepository, times(1)).findLobbyById(lobbyId);
        verify(lobbyPlayerRepository, times(1)).findPlayersByLobbyId(lobbyId);
        verify(duelManager, times(1)).assignNewQuestionToLobby(lobbyId);
        verify(lobbyNotifyHandler, times(1)).handle(lobbyId);
    }

    @Test
    @DisplayName("New Question endpoint - lobby not found")
    void getNewQuestionLobbyNotFound() throws IOException {
        when(env.isProd()).thenReturn(false);

        User user = createRandomUser();
        AuthenticationObject authObj = createAuthenticationObject(user);

        String lobbyId = randomUUID();
        LobbyPlayer lobbyPlayer = LobbyPlayer.builder()
                        .id(randomUUID())
                        .lobbyId(lobbyId)
                        .playerId(user.getId())
                        .points(100)
                        .build();

        when(lobbyPlayerRepository.findLobbyPlayerByPlayerId(user.getId())).thenReturn(lobbyPlayer);
        when(lobbyRepository.findLobbyById(lobbyId)).thenReturn(null);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            duelController.getNewQuestion(authObj);
        });

        assertEquals(HttpStatus.NOT_FOUND.value(), exception.getStatusCode().value());
        assertEquals("Lobby not found.", exception.getReason());

        verify(lobbyPlayerRepository, times(1)).findLobbyPlayerByPlayerId(user.getId());
        verify(lobbyRepository, times(1)).findLobbyById(lobbyId);
        verify(duelManager, times(0)).assignNewQuestionToLobby(any());
        verify(lobbyNotifyHandler, times(0)).handle(any());
    }

    @Test
    @DisplayName("New Question endpoint - player is not in lobby")
    void getNewQuestionPlayerNotInLobby() throws IOException {
        when(env.isProd()).thenReturn(false);

        User user = createRandomUser();
        AuthenticationObject authObj = createAuthenticationObject(user);

        when(lobbyPlayerRepository.findLobbyPlayerByPlayerId(user.getId())).thenReturn(null);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            duelController.getNewQuestion(authObj);
        });

        assertEquals(HttpStatus.NOT_FOUND.value(), exception.getStatusCode().value());
        assertEquals("You are not currently in a lobby.", exception.getReason());

        verify(lobbyPlayerRepository, times(1)).findLobbyPlayerByPlayerId(user.getId());
        verify(lobbyRepository, times(0)).findLobbyById(any());
        verify(duelManager, times(0)).assignNewQuestionToLobby(any());
        verify(lobbyNotifyHandler, times(0)).handle(any());
    }

    @Test
    @DisplayName("New Question endpoint - not all players completed current question")
    void getNewQuestionNotAllPlayersCompleted() throws IOException {
        when(env.isProd()).thenReturn(false);

        User user = createRandomUser();
        AuthenticationObject authObj = createAuthenticationObject(user);

        String lobbyId = randomUUID();
        LobbyPlayer lobbyPlayer = LobbyPlayer.builder()
                        .id(randomUUID())
                        .lobbyId(lobbyId)
                        .playerId(user.getId())
                        .points(100)
                        .build();

        Lobby lobby = Lobby.builder()
                        .id(lobbyId)
                        .joinCode(PartyCodeGenerator.generateCode())
                        .status(LobbyStatus.ACTIVE)
                        .playerCount(2)
                        .build();

        LobbyPlayer player1 = LobbyPlayer.builder()
                        .id(randomUUID())
                        .lobbyId(lobbyId)
                        .playerId(user.getId())
                        .points(100)
                        .build();

        LobbyPlayer player2 = LobbyPlayer.builder()
                        .id(randomUUID())
                        .lobbyId(lobbyId)
                        .playerId(randomUUID())
                        .points(null)
                        .build();

        when(lobbyPlayerRepository.findLobbyPlayerByPlayerId(user.getId())).thenReturn(lobbyPlayer);
        when(lobbyRepository.findLobbyById(lobbyId)).thenReturn(lobby);
        when(lobbyPlayerRepository.findPlayersByLobbyId(lobbyId)).thenReturn(List.of(player1, player2));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            duelController.getNewQuestion(authObj);
        });

        assertEquals(HttpStatus.CONFLICT.value(), exception.getStatusCode().value());
        assertEquals("Not all players have completed the current question.", exception.getReason());

        verify(lobbyPlayerRepository, times(1)).findLobbyPlayerByPlayerId(user.getId());
        verify(lobbyRepository, times(1)).findLobbyById(lobbyId);
        verify(lobbyPlayerRepository, times(1)).findPlayersByLobbyId(lobbyId);
        verify(duelManager, times(0)).assignNewQuestionToLobby(any());
        verify(lobbyNotifyHandler, times(0)).handle(any());
    }

    @Test
    @DisplayName("New Question endpoint - SSE notification failure for new question")
    void getNewQuestionSSENotificationFailure() throws Exception {
        when(env.isProd()).thenReturn(false);

        User user = createRandomUser();
        AuthenticationObject authObj = createAuthenticationObject(user);

        String lobbyId = randomUUID();
        LobbyPlayer lobbyPlayer = LobbyPlayer.builder()
                        .id(randomUUID())
                        .lobbyId(lobbyId)
                        .playerId(user.getId())
                        .points(100)
                        .build();

        Lobby lobby = Lobby.builder()
                        .id(lobbyId)
                        .joinCode(PartyCodeGenerator.generateCode())
                        .status(LobbyStatus.ACTIVE)
                        .playerCount(2)
                        .build();

        LobbyPlayer player1 = LobbyPlayer.builder()
                        .id(randomUUID())
                        .lobbyId(lobbyId)
                        .playerId(user.getId())
                        .points(150)
                        .build();

        LobbyPlayer player2 = LobbyPlayer.builder()
                        .id(randomUUID())
                        .lobbyId(lobbyId)
                        .playerId(randomUUID())
                        .points(100)
                        .build();

        when(lobbyPlayerRepository.findLobbyPlayerByPlayerId(user.getId())).thenReturn(lobbyPlayer);
        when(lobbyRepository.findLobbyById(lobbyId)).thenReturn(lobby);
        when(lobbyPlayerRepository.findPlayersByLobbyId(lobbyId)).thenReturn(List.of(player1, player2));
        doNothing().when(duelManager).assignNewQuestionToLobby(lobbyId);
        doThrow(new IOException("SSE connection failed")).when(lobbyNotifyHandler).handle(lobbyId);

        ResponseEntity<ApiResponder<Empty>> response = duelController.getNewQuestion(authObj);

        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        assertTrue(response.getBody().isSuccess());
        assertEquals("New question assigned to all players.", response.getBody().getMessage());

        verify(duelManager, times(1)).assignNewQuestionToLobby(lobbyId);
        verify(lobbyNotifyHandler, times(1)).handle(lobbyId);
    }

    @Test
    @DisplayName("SSE endpoint - fails in production environment")
    void getDuelDataFailsInProduction() {
        when(env.isProd()).thenReturn(true);

        User user = createRandomUser();
        AuthenticationObject authObj = createAuthenticationObject(user);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            duelController.getDuelData(authObj);
        });

        assertEquals(HttpStatus.FORBIDDEN.value(), exception.getStatusCode().value());
        assertEquals("Endpoint is currently non-functional", exception.getReason());

        verify(lobbyRepository, times(0)).findActiveLobbyByLobbyPlayerId(any());
        verify(lobbyRepository, times(0)).findAvailableLobbyByLobbyPlayerId(any());
        verify(duelManager, times(0)).generateDuelData(any());
        verify(lobbyNotifyHandler, times(0)).register(any(), any());
    }

    @Test
    @DisplayName("SSE endpoint - player not in any lobby")
    void getDuelDataPlayerNotInAnyLobby() {
        when(env.isProd()).thenReturn(false);

        User user = createRandomUser();
        AuthenticationObject authObj = createAuthenticationObject(user);

        when(lobbyRepository.findActiveLobbyByLobbyPlayerId(user.getId())).thenReturn(null);
        when(lobbyRepository.findAvailableLobbyByLobbyPlayerId(user.getId())).thenReturn(null);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            duelController.getDuelData(authObj);
        });

        assertEquals(HttpStatus.NOT_FOUND.value(), exception.getStatusCode().value());
        assertEquals("Player is not currently in a lobby and/or duel.", exception.getReason());

        verify(lobbyRepository, times(1)).findActiveLobbyByLobbyPlayerId(user.getId());
        verify(lobbyRepository, times(1)).findAvailableLobbyByLobbyPlayerId(user.getId());
        verify(duelManager, times(0)).generateDuelData(any());
        verify(lobbyNotifyHandler, times(0)).register(any(), any());
    }

    @Test
    @DisplayName("SSE endpoint - player in active lobby")
    void getDuelDataPlayerInActiveLobby() {
        when(env.isProd()).thenReturn(false);

        User user = createRandomUser();
        AuthenticationObject authObj = createAuthenticationObject(user);

        String lobbyId = randomUUID();
        Lobby activeLobby = Lobby.builder()
                        .id(lobbyId)
                        .joinCode(PartyCodeGenerator.generateCode())
                        .status(LobbyStatus.ACTIVE)
                        .playerCount(2)
                        .build();

        DuelData mockDuelData = DuelData.builder().build();
        when(lobbyRepository.findActiveLobbyByLobbyPlayerId(user.getId())).thenReturn(activeLobby);
        when(duelManager.generateDuelData(lobbyId)).thenReturn(mockDuelData);
        doNothing().when(lobbyNotifyHandler).register(eq(lobbyId), any());

        SseWrapper<ApiResponder<DuelData>> result = duelController.getDuelData(authObj);

        assertNotNull(result);
        verify(lobbyRepository, times(1)).findActiveLobbyByLobbyPlayerId(user.getId());
        verify(lobbyRepository, times(0)).findAvailableLobbyByLobbyPlayerId(user.getId());
        verify(duelManager, times(1)).generateDuelData(lobbyId);
        verify(lobbyNotifyHandler, times(1)).register(eq(lobbyId), any());
    }

    @Test
    @DisplayName("SSE endpoint - player in available lobby ")
    void getDuelDataPlayerInAvailableLobby() {
        when(env.isProd()).thenReturn(false);

        User user = createRandomUser();
        AuthenticationObject authObj = createAuthenticationObject(user);

        String lobbyId = randomUUID();
        Lobby availableLobby = Lobby.builder()
                        .id(lobbyId)
                        .joinCode(PartyCodeGenerator.generateCode())
                        .status(LobbyStatus.AVAILABLE)
                        .playerCount(1)
                        .build();

        DuelData mockDuelData = DuelData.builder().build();
        when(lobbyRepository.findActiveLobbyByLobbyPlayerId(user.getId())).thenReturn(null);
        when(lobbyRepository.findAvailableLobbyByLobbyPlayerId(user.getId())).thenReturn(availableLobby);
        when(duelManager.generateDuelData(lobbyId)).thenReturn(mockDuelData);
        doNothing().when(lobbyNotifyHandler).register(eq(lobbyId), any());

        SseWrapper<ApiResponder<DuelData>> result = duelController.getDuelData(authObj);

        assertNotNull(result);
        verify(lobbyRepository, times(1)).findActiveLobbyByLobbyPlayerId(user.getId());
        verify(lobbyRepository, times(1)).findAvailableLobbyByLobbyPlayerId(user.getId());
        verify(duelManager, times(1)).generateDuelData(lobbyId);
        verify(lobbyNotifyHandler, times(1)).register(eq(lobbyId), any());
    }

}
