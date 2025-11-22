package com.patina.codebloom.api.duel;

import java.time.OffsetDateTime;
import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

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
import com.patina.codebloom.common.dto.autogen.UnsafeGenericFailureResponse;
import com.patina.codebloom.common.dto.lobby.DuelData;
import com.patina.codebloom.common.env.Env;
import com.patina.codebloom.common.security.AuthenticationObject;
import com.patina.codebloom.common.security.annotation.Protected;
import com.patina.codebloom.common.time.StandardizedOffsetDateTime;
import com.patina.codebloom.common.utils.duel.PartyCodeGenerator;
import com.patina.codebloom.common.utils.sse.SseWrapper;
import com.patina.codebloom.scheduled.pg.handler.LobbyNotifyHandler;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@RestController
@Tag(name = "Live duel routes", description = """
                This controller houses the logic for live Leetcode duels. """)
@RequestMapping("/api/duel")
@Profile("!ci")
@Slf4j
public class DuelController {
    private static final int MAX_PLAYER_COUNT = 2;

    private final Env env;
    private final DuelManager duelManager;
    private final LobbyRepository lobbyRepository;
    private final LobbyPlayerRepository lobbyPlayerRepository;
    private final LobbyNotifyHandler lobbyNotifyHandler;

    public DuelController(final Env env, final DuelManager duelManager, final LobbyRepository lobbyRepository,
                    final LobbyPlayerRepository lobbyPlayerRepository, final LobbyNotifyHandler lobbyNotifyHandler) {
        this.env = env;
        this.duelManager = duelManager;
        this.lobbyRepository = lobbyRepository;
        this.lobbyPlayerRepository = lobbyPlayerRepository;
        this.lobbyNotifyHandler = lobbyNotifyHandler;
    }

    private void validatePlayerNotInLobby(final String playerId) {
        var availableLobby = lobbyRepository.findAvailableLobbyByLobbyPlayerPlayerId(playerId);

        if (availableLobby.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "You are already in a party. Please leave the party, then try again.");
        }

        var activeLobby = lobbyRepository.findActiveLobbyByLobbyPlayerPlayerId(playerId);

        if (activeLobby.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "You are currently in a duel. Please forfeit the duel, then try again.");
        }
    }

    private void validateLobby(final Lobby lobby) {
        var now = StandardizedOffsetDateTime.now();
        if (lobby.getExpiresAt().isBefore(now)) {
            // TODO: Could possibly invalidate this party here if it hasn't been invalidated
            // yet.
            throw new ResponseStatusException(HttpStatus.GONE, "The lobby has expired and cannot be joined.");
        }

        if (lobby.getPlayerCount() == MAX_PLAYER_COUNT) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This lobby already has the maximum number of players");
        }
    }

    @Operation(summary = "Join lobby", description = "Join a lobby by providing the lobby code.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "403", description = "Endpoint is currently non-functional", content = @Content(schema = @Schema(implementation = UnsafeGenericFailureResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(implementation = UnsafeGenericFailureResponse.class))),
            @ApiResponse(responseCode = "404", description = "Party with the given code cannot be found", content = @Content(schema = @Schema(implementation = UnsafeGenericFailureResponse.class))),
            @ApiResponse(responseCode = "409", description = """
                            There is a conflict with the request; check message""", content = @Content(schema = @Schema(implementation = UnsafeGenericFailureResponse.class))),
            @ApiResponse(responseCode = "200", description = "Lobby has been successfully joined!"),
    })
    @PostMapping("/lobby/join")
    public ResponseEntity<ApiResponder<Empty>> joinLobby(@Protected final AuthenticationObject authenticationObject,
                    @RequestBody final JoinLobbyBody joinPartyBody) {
        if (env.isProd()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Endpoint is currently non-functional");
        }

        joinPartyBody.validate();

        var user = authenticationObject.getUser();

        var lobby = lobbyRepository.findAvailableLobbyByJoinCode(joinPartyBody.getPartyCode())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "The party with the given code cannot be found."));

        validateLobby(lobby);
        validatePlayerNotInLobby(user.getId());

        lobbyPlayerRepository.createLobbyPlayer(
                        LobbyPlayer.builder()
                                        .lobbyId(lobby.getId())
                                        .playerId(user.getId())
                                        .build());

        lobby.setPlayerCount(lobby.getPlayerCount() + 1);
        boolean isSuccessful = lobbyRepository.updateLobby(lobby);

        if (!isSuccessful) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to join party. Please try again later.");
        }

        return ResponseEntity.ok(ApiResponder.success("Party successfully joined!", Empty.of()));
    }

    @Operation(summary = "Leave party", description = "Leave the current lobby")
    @ApiResponse(responseCode = "200", description = "Lobby left successfully")
    @ApiResponse(responseCode = "400", description = "Player is not in a lobby")
    @ApiResponse(responseCode = "500", description = "Failed to leave the lobby")
    @ApiResponse(responseCode = "401", description = "User not authenticated")
    @PostMapping("/party/leave")
    public ResponseEntity<ApiResponder<Empty>> leaveParty(@Protected final AuthenticationObject authenticationObject) {
        if (env.isProd()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Endpoint is currently non-functional");
        }

        User user = authenticationObject.getUser();
        String playerId = user.getId();

        LobbyPlayer existingLobbyPlayer = lobbyPlayerRepository.findLobbyPlayerByPlayerId(playerId)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "You are not currently in a lobby."));

        String lobbyId = existingLobbyPlayer.getLobbyId();

        boolean isLobbyPlayerDeleted = lobbyPlayerRepository.deleteLobbyPlayerById(existingLobbyPlayer.getId());
        if (!isLobbyPlayerDeleted) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(ApiResponder.failure("Failed to leave the lobby. Please try again."));
        }

        Lobby lobby = lobbyRepository.findLobbyById(lobbyId)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong."));

        int updatedPlayerCount = lobby.getPlayerCount() - 1;

        lobby.setPlayerCount(updatedPlayerCount);
        if (updatedPlayerCount == 0) {
            lobby.setStatus(LobbyStatus.CLOSED);
        } else {
            lobby.setStatus(LobbyStatus.AVAILABLE);
        }
        lobbyRepository.updateLobby(lobby);

        return ResponseEntity.ok(ApiResponder.success("Successfully left the lobby.", Empty.of()));
    }

    @Operation(summary = "Create party", description = "Create a new lobby and become the host")
    @ApiResponse(responseCode = "200", description = "Lobby created successfully")
    @ApiResponse(responseCode = "400", description = "Player is already in a lobby")
    @ApiResponse(responseCode = "401", description = "User not authenticated")
    @PostMapping("/party/create")
    public ResponseEntity<ApiResponder<Empty>> createParty(@Protected final AuthenticationObject authenticationObject) {
        if (env.isProd()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Endpoint is currently non-functional");
        }

        User user = authenticationObject.getUser();
        String playerId = user.getId();

        var existingLobbyPlayer = lobbyPlayerRepository.findLobbyPlayerByPlayerId(playerId);

        if (existingLobbyPlayer.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "You are already in a lobby. Please leave your current lobby before creating a new one.");
        }

        String joinCode = PartyCodeGenerator.generateCode();
        OffsetDateTime expiresAt = StandardizedOffsetDateTime.now().plusMinutes(30);

        Lobby lobby = Lobby.builder()
                        .joinCode(joinCode)
                        .status(LobbyStatus.AVAILABLE)
                        .expiresAt(expiresAt)
                        .playerCount(1)
                        .winnerId(Optional.empty())
                        .build();

        lobbyRepository.createLobby(lobby);

        LobbyPlayer lobbyPlayer = LobbyPlayer.builder()
                        .lobbyId(lobby.getId())
                        .playerId(playerId)
                        .points(0)
                        .build();

        lobbyPlayerRepository.createLobbyPlayer(lobbyPlayer);

        return ResponseEntity.ok(ApiResponder.success("Lobby created successfully! Share the join code: " + lobby.getJoinCode(), Empty.of()));
    }

    @Operation(summary = "SSE endpoint for duel data", description = """
                    Server-sent events endpoint for real-time duel updates

                    NOTE - Our application runs on DigitalOcean, which does not allow SSE over GET requests. As a result, we are forced
                    to use a non-standard SSE implementation over a POST method.
                    See https://ideas.digitalocean.com/app-platform/p/http-response-streaming-in-app-platform-for-sse-support.
                    """)
    @ApiResponse(responseCode = "200", description = "Sending live duel data")
    @ApiResponse(responseCode = "404", description = "Failed to establish SSE connection", content = @Content(schema = @Schema(implementation = UnsafeGenericFailureResponse.class)))
    @PostMapping(value = "/{lobbyCode}/sse")
    public SseWrapper<ApiResponder<DuelData>> getDuelData(
                    @PathVariable final String lobbyCode) {
        if (env.isProd()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Endpoint is currently non-functional");
        }

        var lobby = lobbyRepository.findActiveLobbyByJoinCode(lobbyCode)
                        .or(() -> lobbyRepository.findAvailableLobbyByJoinCode(lobbyCode))
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "A duel/party with the given code cannot be found."));

        SseWrapper<ApiResponder<DuelData>> emitter = new SseWrapper<>(1_800_000L);
        try {
            lobbyNotifyHandler.register(lobby.getId(), emitter);
        } catch (Exception e) {
            log.error("Failed to send SSE data", e);
            emitter.completeWithError(e);
            lobbyNotifyHandler.deregister(lobby.getId());
        }

        return emitter;
    }
}
