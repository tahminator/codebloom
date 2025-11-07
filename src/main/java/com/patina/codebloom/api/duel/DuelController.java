package com.patina.codebloom.api.duel;

import java.time.OffsetDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.patina.codebloom.common.components.DuelManager;
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
import com.patina.codebloom.common.security.annotation.Protected;
import com.patina.codebloom.common.time.StandardizedOffsetDateTime;
import com.patina.codebloom.common.util.PartyCodeGenerator;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Live duel routes", description = """
                This controller houses the logic for live Leetcode duels. """)
@RequestMapping("/api/duel")
public class DuelController {

    private final Env env;
    private final DuelManager duelManager;
    private final LobbyRepository lobbyRepository;
    private final LobbyPlayerRepository lobbyPlayerRepository;

    public DuelController(final Env env, final DuelManager duelManager, final LobbyRepository lobbyRepository,
                    final LobbyPlayerRepository lobbyPlayerRepository) {
        this.env = env;
        this.duelManager = duelManager;
        this.lobbyRepository = lobbyRepository;
        this.lobbyPlayerRepository = lobbyPlayerRepository;
    }

    @Operation(summary = "Join party", description = "WIP")
    @ApiResponse(responseCode = "403", description = "Endpoint is currently non-functional")
    @PostMapping("/party/join")
    public ResponseEntity<ApiResponder<Empty>> joinParty() {
        if (env.isProd()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Endpoint is currently non-functional");
        }

        return ResponseEntity.ok(ApiResponder.success("ok", Empty.of()));
    }

    @Operation(summary = "Leave party", description = "WIP")
    @ApiResponse(responseCode = "403", description = "Endpoint is currently non-functional")
    @PostMapping("/party/leave")
    public ResponseEntity<ApiResponder<Empty>> leaveParty() {
        if (env.isProd()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Endpoint is currently non-functional");
        }

        return ResponseEntity.ok(ApiResponder.success("ok", Empty.of()));
    }

    @Operation(summary = "Create party", description = "Create a new lobby and become the host")
    @ApiResponse(responseCode = "200", description = "Lobby created successfully")
    @ApiResponse(responseCode = "400", description = "Player is already in a lobby")
    @ApiResponse(responseCode = "401", description = "User not authenticated")
    @Protected
    @PostMapping("/party/create")
    public ResponseEntity<ApiResponder<Empty>> createParty(final AuthenticationObject authenticationObject) {
        if (env.isProd()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Endpoint is currently non-functional");
        }

        User user = authenticationObject.getUser();
        String playerId = user.getId();

        LobbyPlayer existingLobbyPlayer = lobbyPlayerRepository.findLobbyPlayerByPlayerId(playerId);
        if (existingLobbyPlayer != null) {
            return ResponseEntity.badRequest()
                            .body(ApiResponder.failure("You are already in a lobby. Please leave your current lobby before creating a new one."));
        }

        String joinCode = PartyCodeGenerator.generateUniqueCode(code -> lobbyRepository.findLobbyByJoinCode(code) != null);

        OffsetDateTime expiresAt = StandardizedOffsetDateTime.now().plusMinutes(30);

        Lobby lobby = Lobby.builder()
                        .joinCode(joinCode)
                        .status(LobbyStatus.AVAILABLE)
                        .expiresAt(expiresAt)
                        .playerCount(1)
                        .winnerId(null)
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
}
