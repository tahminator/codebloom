package com.patina.codebloom.api.duel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.javafaker.Faker;
import com.patina.codebloom.api.duel.body.JoinLobbyBody;
import com.patina.codebloom.common.components.duel.DuelException;
import com.patina.codebloom.common.components.duel.DuelManager;
import com.patina.codebloom.common.components.duel.PartyManager;
import com.patina.codebloom.common.db.models.lobby.Lobby;
import com.patina.codebloom.common.db.models.lobby.LobbyStatus;
import com.patina.codebloom.common.db.models.user.User;
import com.patina.codebloom.common.db.repos.lobby.LobbyRepository;
import com.patina.codebloom.common.dto.ApiResponder;
import com.patina.codebloom.common.dto.lobby.DuelData;
import com.patina.codebloom.common.env.Env;
import com.patina.codebloom.common.security.AuthenticationObject;
import com.patina.codebloom.common.time.StandardizedOffsetDateTime;
import com.patina.codebloom.common.utils.duel.PartyCodeGenerator;
import com.patina.codebloom.common.utils.sse.SseWrapper;
import com.patina.codebloom.scheduled.pg.handler.LobbyNotifyHandler;
import com.patina.codebloom.utilities.exception.ValidationException;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class DuelControllerTest {

    private final DuelController duelController;
    private final Faker faker;

    private DuelManager duelManager = mock(DuelManager.class);
    private PartyManager partyManager = mock(PartyManager.class);
    private LobbyRepository lobbyRepository = mock(LobbyRepository.class);
    private Env env = mock(Env.class);
    private LobbyNotifyHandler lobbyNotifyHandler = mock(LobbyNotifyHandler.class);

    public DuelControllerTest() {
        this.duelController = new DuelController(env, duelManager, partyManager, lobbyRepository, lobbyNotifyHandler);
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
    @DisplayName("Join lobby - invalid code length")
    void joinPartyIncorrectLengthCode() {
        when(env.isProd()).thenReturn(false);

        var joinPartyBody = JoinLobbyBody.builder().partyCode("ABC12").build();

        User user = createRandomUser();
        AuthenticationObject authObj = createAuthenticationObject(user);

        var ex = assertThrows(ValidationException.class, () -> {
            duelController.joinParty(authObj, joinPartyBody);
        });

        assertEquals("Lobby code must be exactly 6 characters.", ex.getMessage());
    }

    @Test
    @DisplayName("Join lobby - empty code")
    void joinLobbyEmptyCode() {
        when(env.isProd()).thenReturn(false);

        var joinPartyBody = JoinLobbyBody.builder().partyCode("").build();

        User user = createRandomUser();
        AuthenticationObject authObj = createAuthenticationObject(user);

        var ex = assertThrows(ValidationException.class, () -> {
            duelController.joinParty(authObj, joinPartyBody);
        });

        assertEquals("Lobby code may not be null or empty.", ex.getMessage());
    }

    @Test
    @DisplayName("Join lobby - null code")
    void joinLobbyNullCode() {
        when(env.isProd()).thenReturn(false);

        var joinPartyBody = JoinLobbyBody.builder().partyCode(null).build();

        User user = createRandomUser();
        AuthenticationObject authObj = createAuthenticationObject(user);

        var ex = assertThrows(ValidationException.class, () -> {
            duelController.joinParty(authObj, joinPartyBody);
        });

        assertEquals("Lobby code may not be null or empty.", ex.getMessage());
    }

    @Test
    @DisplayName("Join lobby - fails in production environment")
    void joinLobbyFailsInProduction() {
        when(env.isProd()).thenReturn(true);

        var joinPartyBody = JoinLobbyBody.builder().partyCode("ABC123").build();

        User user = createRandomUser();
        AuthenticationObject authObj = createAuthenticationObject(user);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            duelController.joinParty(authObj, joinPartyBody);
        });

        assertEquals(HttpStatus.FORBIDDEN.value(), exception.getStatusCode().value());
        assertEquals("Endpoint is currently non-functional", exception.getReason());

        try {
            verify(partyManager, times(0)).joinParty(any(), any());
        } catch (DuelException e) {
            fail(e);
        }
    }

    @Test
    void testJoinPartyPartyManagerFailed() {
        when(env.isProd()).thenReturn(false);

        var joinPartyBody = JoinLobbyBody.builder().partyCode("ABC123").build();

        User user = createRandomUser();
        AuthenticationObject authObj = createAuthenticationObject(user);

        try {
            doThrow(new DuelException(HttpStatus.NOT_FOUND, "The party with the given code cannot be found."))
                    .when(partyManager)
                    .joinParty(eq(user.getId()), eq("ABC123"));
        } catch (DuelException _) {
        }

        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class, () -> duelController.joinParty(authObj, joinPartyBody));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("The party with the given code cannot be found.", exception.getReason());

        try {
            verify(partyManager, times(1)).joinParty(eq(user.getId()), eq("ABC123"));
        } catch (DuelException e) {
            fail(e);
        }
    }

    @Test
    void testJoinPartyHappyPath() {
        when(env.isProd()).thenReturn(false);

        var joinPartyBody = JoinLobbyBody.builder().partyCode("ABC123").build();

        User user = createRandomUser();
        AuthenticationObject authObj = createAuthenticationObject(user);

        try {
            doNothing().when(partyManager).joinParty(eq(user.getId()), eq("ABC123"));
        } catch (DuelException _) {
        }

        var response = duelController.joinParty(authObj, joinPartyBody);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        var apiResponder = response.getBody();
        assertTrue(apiResponder.isSuccess());
        assertEquals("Party successfully joined!", apiResponder.getMessage());

        try {
            verify(partyManager, times(1)).joinParty(eq(user.getId()), eq("ABC123"));
        } catch (DuelException e) {
            fail(e);
        }
    }

    @Test
    void testLeavePartyFailureInProductionEnvironment() {
        when(env.isProd()).thenReturn(true);

        User user = createRandomUser();
        AuthenticationObject authObj = createAuthenticationObject(user);

        org.springframework.web.server.ResponseStatusException exception = assertThrows(
                org.springframework.web.server.ResponseStatusException.class, () -> duelController.leaveParty(authObj));

        assertEquals(403, exception.getStatusCode().value());
        assertEquals("Endpoint is currently non-functional", exception.getReason());

        try {
            verify(partyManager, times(0)).leaveParty(any());
        } catch (DuelException e) {
            fail(e);
        }
    }

    @Test
    void testLeavePartyPartyManagerFailed() {
        when(env.isProd()).thenReturn(false);

        User user = createRandomUser();
        AuthenticationObject authObj = createAuthenticationObject(user);

        try {
            doThrow(new DuelException(HttpStatus.NOT_FOUND, "You are not currently in a lobby."))
                    .when(partyManager)
                    .leaveParty(eq(user.getId()));
        } catch (DuelException _) {
        }

        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class, () -> duelController.leaveParty(authObj));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("You are not currently in a lobby.", exception.getReason());

        try {
            verify(partyManager, times(1)).leaveParty(eq(user.getId()));
        } catch (DuelException e) {
            fail(e);
        }
    }

    @Test
    void testLeavePartyHappyPath() {
        when(env.isProd()).thenReturn(false);

        User user = createRandomUser();
        AuthenticationObject authObj = createAuthenticationObject(user);

        try {
            doNothing().when(partyManager).leaveParty(eq(user.getId()));
        } catch (DuelException _) {
        }

        var response = duelController.leaveParty(authObj);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        var apiResponder = response.getBody();
        assertTrue(apiResponder.isSuccess());
        assertEquals("Successfully left the party.", apiResponder.getMessage());

        try {
            verify(partyManager, times(1)).leaveParty(eq(user.getId()));
        } catch (DuelException e) {
            fail(e);
        }
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

        try {
            verify(partyManager, times(0)).createParty(any());
        } catch (DuelException e) {
            fail(e);
        }
    }

    @Test
    void testCreatePartyPartyManagerFailed() {
        when(env.isProd()).thenReturn(false);

        User user = createRandomUser();
        AuthenticationObject authObj = createAuthenticationObject(user);

        try {
            doThrow(new DuelException(
                            HttpStatus.CONFLICT,
                            "You are already in a lobby. Please leave your current lobby before creating a new one."))
                    .when(partyManager)
                    .createParty(eq(user.getId()));
        } catch (DuelException _) {
        }

        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class, () -> duelController.createParty(authObj));

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        assertEquals(
                "You are already in a lobby. Please leave your current lobby before creating a new one.",
                exception.getReason());

        try {
            verify(partyManager, times(1)).createParty(eq(user.getId()));
        } catch (DuelException e) {
            fail(e);
        }
    }

    @Test
    void testCreatePartyHappyPath() {
        when(env.isProd()).thenReturn(false);

        User user = createRandomUser();
        AuthenticationObject authObj = createAuthenticationObject(user);

        try {
            when(partyManager.createParty(eq(user.getId()))).thenReturn("ABC123");
        } catch (DuelException _) {
        }

        var response = duelController.createParty(authObj);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        var apiResponder = response.getBody();
        assertTrue(apiResponder.isSuccess());
        assertEquals("Lobby created successfully!", apiResponder.getMessage());
        assertNotNull(apiResponder.getPayload());
        assertEquals("ABC123", apiResponder.getPayload().getCode());

        try {
            verify(partyManager, times(1)).createParty(eq(user.getId()));
        } catch (DuelException e) {
            fail(e);
        }
    }

    @Test
    @DisplayName("SSE endpoint - fails in production environment")
    void getDuelDataFailsInProduction() {
        when(env.isProd()).thenReturn(true);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            duelController.getDuelData(null);
        });

        assertEquals(HttpStatus.FORBIDDEN.value(), exception.getStatusCode().value());
        assertEquals("Endpoint is currently non-functional", exception.getReason());

        verify(lobbyRepository, times(0)).findActiveLobbyByLobbyPlayerPlayerId(any());
        verify(lobbyRepository, times(0)).findAvailableLobbyByLobbyPlayerPlayerId(any());
        verify(lobbyNotifyHandler, times(0)).register(any(), any());
    }

    @Test
    @DisplayName("SSE endpoint - lobby does not exist")
    void getDuelDataLobbyDoesNotExist() {
        when(env.isProd()).thenReturn(false);

        when(lobbyRepository.findActiveLobbyByJoinCode(any())).thenReturn(Optional.empty());
        when(lobbyRepository.findAvailableLobbyByJoinCode(any())).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            duelController.getDuelData(null);
        });

        assertEquals(HttpStatus.NOT_FOUND.value(), exception.getStatusCode().value());
        assertEquals("A duel/party with the given code cannot be found.", exception.getReason());

        verify(lobbyRepository, times(1)).findActiveLobbyByJoinCode(any());
        verify(lobbyRepository, times(1)).findAvailableLobbyByJoinCode(any());
        verify(duelManager, times(0)).generateDuelData(any());
        verify(lobbyNotifyHandler, times(0)).register(any(), any());
    }

    @Test
    @DisplayName("SSE endpoint - active lobby exists")
    void getDuelDataPlayerInActiveLobby() {
        when(env.isProd()).thenReturn(false);

        String lobbyId = randomUUID();
        Lobby activeLobby = Lobby.builder()
                .id(lobbyId)
                .joinCode(PartyCodeGenerator.generateCode())
                .expiresAt(StandardizedOffsetDateTime.now().plus(1, ChronoUnit.HOURS))
                .status(LobbyStatus.ACTIVE)
                .playerCount(2)
                .build();

        when(lobbyRepository.findActiveLobbyByJoinCode(eq(activeLobby.getJoinCode())))
                .thenReturn(Optional.of(activeLobby));
        doNothing().when(lobbyNotifyHandler).register(eq(lobbyId), any());

        SseWrapper<ApiResponder<DuelData>> result = duelController.getDuelData(activeLobby.getJoinCode());

        assertNotNull(result);
        verify(lobbyRepository, times(1)).findActiveLobbyByJoinCode(activeLobby.getJoinCode());
        verify(lobbyRepository, times(0)).findAvailableLobbyByJoinCode(activeLobby.getJoinCode());
        verify(lobbyNotifyHandler, times(1)).register(eq(lobbyId), any());
    }

    @Test
    @DisplayName("SSE endpoint - available lobby exists")
    void getDuelDataAvailableLobbyExists() {
        when(env.isProd()).thenReturn(false);

        String lobbyId = randomUUID();
        Lobby availableLobby = Lobby.builder()
                .id(lobbyId)
                .joinCode(PartyCodeGenerator.generateCode())
                .expiresAt(StandardizedOffsetDateTime.now().plus(1, ChronoUnit.HOURS))
                .status(LobbyStatus.AVAILABLE)
                .playerCount(1)
                .build();

        when(lobbyRepository.findActiveLobbyByJoinCode(eq(availableLobby.getJoinCode())))
                .thenReturn(Optional.empty());
        when(lobbyRepository.findAvailableLobbyByJoinCode(eq(availableLobby.getJoinCode())))
                .thenReturn(Optional.of(availableLobby));
        doNothing().when(lobbyNotifyHandler).register(eq(lobbyId), any());

        SseWrapper<ApiResponder<DuelData>> result = duelController.getDuelData(availableLobby.getJoinCode());

        assertNotNull(result);
        verify(lobbyRepository, times(1)).findActiveLobbyByJoinCode(availableLobby.getJoinCode());
        verify(lobbyRepository, times(1)).findAvailableLobbyByJoinCode(availableLobby.getJoinCode());
        verify(lobbyNotifyHandler, times(1)).register(eq(lobbyId), any());
    }

    @Test
    void testStartDuelIsInProd() {
        User user = createRandomUser();
        AuthenticationObject authObj = createAuthenticationObject(user);

        when(env.isProd()).thenReturn(true);

        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class, () -> duelController.startDuel(authObj));

        assertEquals(403, exception.getStatusCode().value());
        assertEquals("Endpoint is currently non-functional", exception.getReason());

        try {
            verify(duelManager, times(0)).startDuel(any(), eq(false));
        } catch (DuelException e) {
            fail(e);
        }
    }

    @Test
    void testStartDuelDuelManagerFailed() {
        User user = createRandomUser();
        AuthenticationObject authObj = createAuthenticationObject(user);

        when(env.isProd()).thenReturn(false);

        try {
            doThrow(new DuelException(HttpStatus.INTERNAL_SERVER_ERROR, "This is an example duel exception."))
                    .when(duelManager)
                    .startDuel(eq(user.getId()), eq(user.isAdmin()));
        } catch (DuelException _) {
        }

        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class, () -> duelController.startDuel(authObj));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
        assertEquals("This is an example duel exception.", exception.getReason());

        try {
            verify(duelManager, times(1)).startDuel(any(), eq(false));
        } catch (DuelException e) {
            fail(e);
        }
    }

    @Test
    void testStartDuelHappyPath() {
        User user = createRandomUser();
        AuthenticationObject authObj = createAuthenticationObject(user);

        when(env.isProd()).thenReturn(false);

        try {
            doNothing().when(duelManager).startDuel(eq(user.getId()), eq(user.isAdmin()));
        } catch (DuelException _) {
        }

        var response = duelController.startDuel(authObj);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        var apiResponder = response.getBody();
        assertTrue(apiResponder.isSuccess());
    }

    @Test
    void testEndDuelIsInProd() {
        User user = createRandomUser();
        AuthenticationObject authObj = createAuthenticationObject(user);

        when(env.isProd()).thenReturn(true);

        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class, () -> duelController.endDuel(authObj));

        assertEquals(403, exception.getStatusCode().value());
        assertEquals("Endpoint is currently non-functional", exception.getReason());

        verify(lobbyRepository, times(0)).findActiveLobbyByLobbyPlayerPlayerId(any());

        try {
            verify(duelManager, times(0)).endDuel(any(), eq(user.isAdmin()));
        } catch (DuelException e) {
            fail(e);
        }
    }

    @Test
    void testEndDuelActivePartyForPlayerCannotBeFound() {
        User user = createRandomUser();
        AuthenticationObject authObj = createAuthenticationObject(user);

        when(env.isProd()).thenReturn(false);
        when(lobbyRepository.findActiveLobbyByLobbyPlayerPlayerId(user.getId())).thenReturn(Optional.empty());

        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class, () -> duelController.endDuel(authObj));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Player is not currently in a duel.", exception.getReason());

        verify(lobbyRepository, times(1)).findActiveLobbyByLobbyPlayerPlayerId(eq(user.getId()));

        try {
            verify(duelManager, times(0)).endDuel(any(), eq(user.isAdmin()));
        } catch (DuelException e) {
            fail(e);
        }
    }

    @Test
    void testEndDuelDuelManagerFailed() {
        User user = createRandomUser();
        AuthenticationObject authObj = createAuthenticationObject(user);

        Lobby lobby = Lobby.builder()
                .id(UUID.randomUUID().toString())
                .joinCode(PartyCodeGenerator.generateCode())
                .status(LobbyStatus.AVAILABLE)
                .expiresAt(OffsetDateTime.now().plusMinutes(30))
                .playerCount(3)
                .build();

        when(env.isProd()).thenReturn(false);
        when(lobbyRepository.findActiveLobbyByLobbyPlayerPlayerId(user.getId())).thenReturn(Optional.of(lobby));

        try {
            doThrow(new DuelException(HttpStatus.INTERNAL_SERVER_ERROR, "This is an example duel exception."))
                    .when(duelManager)
                    .endDuel(eq(lobby.getId()), eq(user.isAdmin()));
        } catch (DuelException _) {
        }

        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class, () -> duelController.endDuel(authObj));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
        assertEquals("This is an example duel exception.", exception.getReason());

        verify(lobbyRepository, times(1)).findActiveLobbyByLobbyPlayerPlayerId(eq(user.getId()));

        try {
            verify(duelManager, times(1)).endDuel(any(), eq(user.isAdmin()));
        } catch (DuelException e) {
            fail(e);
        }
    }
}
