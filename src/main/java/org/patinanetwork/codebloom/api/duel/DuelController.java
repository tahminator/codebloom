package org.patinanetwork.codebloom.api.duel;

import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.patinanetwork.codebloom.api.duel.body.JoinLobbyBody;
import org.patinanetwork.codebloom.api.duel.body.PartyCodeBody;
import org.patinanetwork.codebloom.common.components.duel.DuelException;
import org.patinanetwork.codebloom.common.components.duel.DuelManager;
import org.patinanetwork.codebloom.common.components.duel.PartyManager;
import org.patinanetwork.codebloom.common.db.models.lobby.Lobby;
import org.patinanetwork.codebloom.common.db.models.user.User;
import org.patinanetwork.codebloom.common.db.repos.lobby.LobbyRepository;
import org.patinanetwork.codebloom.common.dto.ApiResponder;
import org.patinanetwork.codebloom.common.dto.Empty;
import org.patinanetwork.codebloom.common.dto.autogen.UnsafeGenericFailureResponse;
import org.patinanetwork.codebloom.common.dto.lobby.DuelData;
import org.patinanetwork.codebloom.common.env.Env;
import org.patinanetwork.codebloom.common.security.AuthenticationObject;
import org.patinanetwork.codebloom.common.security.annotation.Protected;
import org.patinanetwork.codebloom.common.utils.sse.SseWrapper;
import org.patinanetwork.codebloom.scheduled.pg.handler.LobbyNotifyHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@Tag(name = "Live duel routes", description = """
    This controller houses the logic for live Leetcode duels. """)
@RequestMapping("/api/duel")
@Slf4j
@Timed(value = "controller.execution")
public class DuelController {

    private final Env env;
    private final DuelManager duelManager;
    private final PartyManager partyManager;
    private final LobbyRepository lobbyRepository;
    private final LobbyNotifyHandler lobbyNotifyHandler;

    public DuelController(
            final Env env,
            final DuelManager duelManager,
            final PartyManager partyManager,
            final LobbyRepository lobbyRepository,
            final LobbyNotifyHandler lobbyNotifyHandler) {
        this.env = env;
        this.duelManager = duelManager;
        this.partyManager = partyManager;
        this.lobbyRepository = lobbyRepository;
        this.lobbyNotifyHandler = lobbyNotifyHandler;
    }

    @Operation(summary = "Join party", description = "Join a party by providing the lobby code.")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "403",
                        description = "Endpoint is currently non-functional",
                        content = @Content(schema = @Schema(implementation = UnsafeGenericFailureResponse.class))),
                @ApiResponse(
                        responseCode = "401",
                        description = "Unauthorized",
                        content = @Content(schema = @Schema(implementation = UnsafeGenericFailureResponse.class))),
                @ApiResponse(
                        responseCode = "404",
                        description = "Party with the given code cannot be found",
                        content = @Content(schema = @Schema(implementation = UnsafeGenericFailureResponse.class))),
                @ApiResponse(
                        responseCode = "410",
                        description = """
                There is an issue with the request; check message""",
                        content = @Content(schema = @Schema(implementation = UnsafeGenericFailureResponse.class))),
                @ApiResponse(
                        responseCode = "409",
                        description = """
                There is a conflict with the request; check message""",
                        content = @Content(schema = @Schema(implementation = UnsafeGenericFailureResponse.class))),
                @ApiResponse(responseCode = "200", description = "Party has been successfully joined!"),
            })
    @PostMapping("/party/join")
    public ResponseEntity<ApiResponder<Empty>> joinParty(
            @Protected final AuthenticationObject authenticationObject,
            @RequestBody final JoinLobbyBody joinPartyBody) {
        if (env.isProd()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Endpoint is currently non-functional");
        }

        joinPartyBody.validate();

        var user = authenticationObject.getUser();

        try {
            partyManager.joinParty(user.getId(), joinPartyBody.getPartyCode());
        } catch (DuelException e) {
            var httpStatus = e.getHttpStatus().orElse(HttpStatus.INTERNAL_SERVER_ERROR);

            throw new ResponseStatusException(httpStatus, e.getMessage());
        }

        return ResponseEntity.ok(ApiResponder.success("Party successfully joined!", Empty.of()));
    }

    @Operation(summary = "Start duel", description = "Start the duel, given that all conditions are met.")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "403",
                        description = "Endpoint is currently non-functional",
                        content = @Content(schema = @Schema(implementation = UnsafeGenericFailureResponse.class))),
                @ApiResponse(
                        responseCode = "401",
                        description = "Unauthorized",
                        content = @Content(schema = @Schema(implementation = UnsafeGenericFailureResponse.class))),
                @ApiResponse(
                        responseCode = "404",
                        description = "The user is not currently in a party.",
                        content = @Content(schema = @Schema(implementation = UnsafeGenericFailureResponse.class))),
                @ApiResponse(
                        responseCode = "409",
                        description = """
                There is a conflict with the request; check message""",
                        content = @Content(schema = @Schema(implementation = UnsafeGenericFailureResponse.class))),
                @ApiResponse(responseCode = "200", description = "Duel successfully started!"),
            })
    @PostMapping("/start")
    public ResponseEntity<ApiResponder<Empty>> startDuel(@Protected final AuthenticationObject authenticationObject) {
        if (env.isProd()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Endpoint is currently non-functional");
        }

        var user = authenticationObject.getUser();

        try {
            duelManager.startDuel(user.getId(), user.isAdmin());
        } catch (DuelException e) {
            var httpStatus = e.getHttpStatus().orElse(HttpStatus.INTERNAL_SERVER_ERROR);

            throw new ResponseStatusException(httpStatus, e.getMessage());
        }

        return ResponseEntity.ok(ApiResponder.success("Duel successfully started!", Empty.of()));
    }

    @Operation(summary = "Leave party", description = "Leave the current party")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "403",
                        description = "Endpoint is currently non-functional",
                        content = @Content(schema = @Schema(implementation = UnsafeGenericFailureResponse.class))),
                @ApiResponse(
                        responseCode = "401",
                        description = "Unauthorized",
                        content = @Content(schema = @Schema(implementation = UnsafeGenericFailureResponse.class))),
                @ApiResponse(
                        responseCode = "404",
                        description = "The user is not currently in a party.",
                        content = @Content(schema = @Schema(implementation = UnsafeGenericFailureResponse.class))),
                @ApiResponse(responseCode = "200", description = "Party left successfully"),
            })
    @PostMapping("/party/leave")
    public ResponseEntity<ApiResponder<Empty>> leaveParty(@Protected final AuthenticationObject authenticationObject) {
        if (env.isProd()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Endpoint is currently non-functional");
        }

        User user = authenticationObject.getUser();

        try {
            partyManager.leaveParty(user.getId());
        } catch (DuelException e) {
            var httpStatus = e.getHttpStatus().orElse(HttpStatus.INTERNAL_SERVER_ERROR);

            throw new ResponseStatusException(httpStatus, e.getMessage());
        }

        return ResponseEntity.ok(ApiResponder.success("Successfully left the party.", Empty.of()));
    }

    @Operation(
            summary = "End duel",
            description =
                    "This endpoint will end the current duel you are in. The duel will only end if it in a state that is endable.")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "403",
                        description = "Endpoint is currently non-functional",
                        content = @Content(schema = @Schema(implementation = UnsafeGenericFailureResponse.class))),
                @ApiResponse(
                        responseCode = "401",
                        description = "Unauthorized",
                        content = @Content(schema = @Schema(implementation = UnsafeGenericFailureResponse.class))),
                @ApiResponse(
                        responseCode = "404",
                        description = "The user is not currently in a duel.",
                        content = @Content(schema = @Schema(implementation = UnsafeGenericFailureResponse.class))),
                @ApiResponse(
                        responseCode = "409",
                        description = """
                There is a conflict with the request; check message""",
                        content = @Content(schema = @Schema(implementation = UnsafeGenericFailureResponse.class))),
                @ApiResponse(responseCode = "200", description = "Duel has been successfully ended!"),
            })
    @PostMapping("/end")
    public ResponseEntity<ApiResponder<Empty>> endDuel(@Protected final AuthenticationObject authenticationObject) {
        if (env.isProd()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Endpoint is currently non-functional");
        }

        User user = authenticationObject.getUser();

        var lobby = lobbyRepository
                .findActiveLobbyByLobbyPlayerPlayerId(user.getId())
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Player is not currently in a duel."));

        try {
            duelManager.endDuel(lobby.getId(), false);
        } catch (DuelException e) {
            var httpStatus = e.getHttpStatus().orElse(HttpStatus.INTERNAL_SERVER_ERROR);

            throw new ResponseStatusException(httpStatus, e.getMessage());
        }

        return ResponseEntity.ok(ApiResponder.success("Duel successfully ended!", Empty.of()));
    }

    @Operation(
            summary = "Create party",
            description =
                    "Create a new party. If successful, will return a party code which can be shared and distributed.")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "403",
                        description = "Endpoint is currently non-functional",
                        content = @Content(schema = @Schema(implementation = UnsafeGenericFailureResponse.class))),
                @ApiResponse(
                        responseCode = "401",
                        description = "Unauthorized",
                        content = @Content(schema = @Schema(implementation = UnsafeGenericFailureResponse.class))),
                @ApiResponse(
                        responseCode = "409",
                        description = """
                There is a conflict with the request; check message""",
                        content = @Content(schema = @Schema(implementation = UnsafeGenericFailureResponse.class))),
                @ApiResponse(responseCode = "200", description = "Party created successfully"),
            })
    @PostMapping("/party/create")
    public ResponseEntity<ApiResponder<PartyCodeBody>> createParty(
            @Protected final AuthenticationObject authenticationObject) {
        if (env.isProd()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Endpoint is currently non-functional");
        }

        User user = authenticationObject.getUser();

        String joinCode;
        try {
            joinCode = partyManager.createParty(user.getId());
        } catch (DuelException e) {
            var httpStatus = e.getHttpStatus().orElse(HttpStatus.INTERNAL_SERVER_ERROR);

            throw new ResponseStatusException(httpStatus, e.getMessage());
        }

        return ResponseEntity.ok(ApiResponder.success(
                "Lobby created successfully!",
                PartyCodeBody.builder().code(joinCode).build()));
    }

    @Operation(summary = "SSE endpoint for duel data", description = """
        Server-sent events endpoint for real-time duel updates

        NOTE - Our application runs on DigitalOcean, which does not allow SSE over GET requests. As a result, we are forced
        to use a non-standard SSE implementation over a POST method.
        See https://ideas.digitalocean.com/app-platform/p/http-response-streaming-in-app-platform-for-sse-support.
        """)
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "403",
                        description = "Endpoint is currently non-functional",
                        content = @Content(schema = @Schema(implementation = UnsafeGenericFailureResponse.class))),
                @ApiResponse(responseCode = "200", description = "Sending live duel data"),
                @ApiResponse(
                        responseCode = "404",
                        description = "A duel with the given code cannot be found.",
                        content = @Content(schema = @Schema(implementation = UnsafeGenericFailureResponse.class)))
            })
    @PostMapping(value = "/{lobbyCode}/sse")
    public SseWrapper<ApiResponder<DuelData>> getDuelData(@PathVariable final String lobbyCode) {
        if (env.isProd()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Endpoint is currently non-functional");
        }

        var lobby = lobbyRepository
                .findActiveLobbyByJoinCode(lobbyCode)
                .or(() -> lobbyRepository.findAvailableLobbyByJoinCode(lobbyCode))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "A duel/party with the given code cannot be found."));

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

    @Operation(summary = "Get current party or duel code for user", description = """
        If the user is authenticated, this endpoint will check if the user is
        in a party or duel and return the code associated to the game.
    """)
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "403",
                        description = "Endpoint is currently non-functional",
                        content = @Content(schema = @Schema(implementation = UnsafeGenericFailureResponse.class))),
                @ApiResponse(
                        responseCode = "401",
                        description = "Unauthorized",
                        content = @Content(schema = @Schema(implementation = UnsafeGenericFailureResponse.class))),
                @ApiResponse(
                        responseCode = "404",
                        description = "The user is not currently in a party or duel.",
                        content = @Content(schema = @Schema(implementation = UnsafeGenericFailureResponse.class))),
                @ApiResponse(responseCode = "200", description = "Party or duel code was successfully found!"),
            })
    @GetMapping("/current")
    public ResponseEntity<ApiResponder<PartyCodeBody>> getPartyOrDuelCodeForUser(
            @Protected final AuthenticationObject authenticationObject) {
        if (env.isProd()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Endpoint is currently non-functional");
        }

        var user = authenticationObject.getUser();

        Lobby lobby;
        try {
            lobby = duelManager.getLobbyByUserId(user.getId());
        } catch (DuelException e) {
            var httpStatus = e.getHttpStatus().orElse(HttpStatus.INTERNAL_SERVER_ERROR);

            throw new ResponseStatusException(httpStatus, e.getMessage());
        }

        return ResponseEntity.ok()
                .body(ApiResponder.success(
                        "Code found!",
                        PartyCodeBody.builder().code(lobby.getJoinCode()).build()));
    }

    @Operation(summary = "Process solved questions", description = """
        Process solved questions inside of a duel for a given user, if the given conditions are met.
    """)
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "403",
                        description = "Endpoint is currently non-functional",
                        content = @Content(schema = @Schema(implementation = UnsafeGenericFailureResponse.class))),
                @ApiResponse(
                        responseCode = "401",
                        description = "Unauthorized",
                        content = @Content(schema = @Schema(implementation = UnsafeGenericFailureResponse.class))),
                @ApiResponse(
                        responseCode = "404",
                        description = "The user is not currently in a duel.",
                        content = @Content(schema = @Schema(implementation = UnsafeGenericFailureResponse.class))),
                @ApiResponse(
                        responseCode = "200",
                        description =
                                "The user's solved questions were processed (could still mean no new points were awarded)"),
            })
    @PostMapping("/process")
    public ResponseEntity<ApiResponder<Empty>> processSolvedProblemsInDuel(
            @Protected final AuthenticationObject authenticationObject) {
        if (env.isProd()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Endpoint is currently non-functional");
        }

        var user = authenticationObject.getUser();

        Lobby duel;
        int questionsProcessed;
        try {
            duel = duelManager.getDuelByUserId(user.getId());
            questionsProcessed = duelManager.processSubmissions(user, duel);
        } catch (DuelException e) {
            var httpStatus = e.getHttpStatus().orElse(HttpStatus.INTERNAL_SERVER_ERROR);

            throw new ResponseStatusException(httpStatus, e.getMessage());
        }

        try {
            lobbyNotifyHandler.handle(duel.getId());
        } catch (IOException e) {
            log.error("Failed to trigger lobby notify handler", e);
        }

        return ResponseEntity.ok()
                .body(ApiResponder.success(questionsProcessed + " questions successfully processed!", Empty.of()));
    }
}
