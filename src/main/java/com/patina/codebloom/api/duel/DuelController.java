package com.patina.codebloom.api.duel;

import com.patina.codebloom.api.duel.body.JoinLobbyBody;
import com.patina.codebloom.common.components.DuelManager;
import com.patina.codebloom.common.db.models.lobby.Lobby;
import com.patina.codebloom.common.db.models.lobby.LobbyQuestion;
import com.patina.codebloom.common.db.models.lobby.LobbyStatus;
import com.patina.codebloom.common.db.models.lobby.player.LobbyPlayer;
import com.patina.codebloom.common.db.models.lobby.player.LobbyPlayerQuestion;
import com.patina.codebloom.common.db.models.question.Question;
import com.patina.codebloom.common.db.models.question.bank.QuestionBank;
import com.patina.codebloom.common.db.models.user.User;
import com.patina.codebloom.common.db.repos.lobby.LobbyQuestionRepository;
import com.patina.codebloom.common.db.repos.lobby.LobbyRepository;
import com.patina.codebloom.common.db.repos.lobby.player.LobbyPlayerRepository;
import com.patina.codebloom.common.db.repos.lobby.player.question.LobbyPlayerQuestionRepository;
import com.patina.codebloom.common.db.repos.question.QuestionRepository;
import com.patina.codebloom.common.db.repos.question.questionbank.QuestionBankRepository;
import com.patina.codebloom.common.dto.ApiResponder;
import com.patina.codebloom.common.dto.Empty;
import com.patina.codebloom.common.dto.autogen.UnsafeGenericFailureResponse;
import com.patina.codebloom.common.dto.lobby.DuelData;
import com.patina.codebloom.common.env.Env;
import com.patina.codebloom.common.leetcode.models.LeetcodeSubmission;
import com.patina.codebloom.common.leetcode.score.ScoreCalculator;
import com.patina.codebloom.common.leetcode.throttled.ThrottledLeetcodeClient;
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
import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@Profile("!ci")
@Slf4j
public class DuelController {

    private static final int MAX_PLAYER_COUNT = 2;

    private final Env env;
    private final DuelManager duelManager;
    private final LobbyRepository lobbyRepository;
    private final LobbyPlayerRepository lobbyPlayerRepository;
    private final LobbyNotifyHandler lobbyNotifyHandler;
    private final QuestionBankRepository questionBankRepository;
    private final LobbyPlayerQuestionRepository lobbyPlayerQuestionRepository;
    private final QuestionRepository questionRepository;
    private final LobbyQuestionRepository lobbyQuestionRepository;
    private final ThrottledLeetcodeClient throttledLeetcodeClient;

    public DuelController(
            final Env env,
            final DuelManager duelManager,
            final LobbyRepository lobbyRepository,
            final LobbyPlayerRepository lobbyPlayerRepository,
            final LobbyNotifyHandler lobbyNotifyHandler,
            final QuestionBankRepository questionBankRepository,
            final LobbyPlayerQuestionRepository lobbyPlayerQuestionRepository,
            final QuestionRepository questionRepository,
            final LobbyQuestionRepository lobbyQuestionRepository,
            final ThrottledLeetcodeClient throttledLeetcodeClient) {
        this.env = env;
        this.duelManager = duelManager;
        this.lobbyRepository = lobbyRepository;
        this.lobbyPlayerRepository = lobbyPlayerRepository;
        this.lobbyNotifyHandler = lobbyNotifyHandler;
        this.questionBankRepository = questionBankRepository;
        this.lobbyPlayerQuestionRepository = lobbyPlayerQuestionRepository;
        this.questionRepository = questionRepository;
        this.lobbyQuestionRepository = lobbyQuestionRepository;
        this.throttledLeetcodeClient = throttledLeetcodeClient;
    }

    private void validatePlayerNotInLobby(final String playerId) {
        var availableLobby = lobbyRepository.findAvailableLobbyByLobbyPlayerPlayerId(playerId);

        if (availableLobby.isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "You are already in a party. Please leave the party, then try again.");
        }

        var activeLobby = lobbyRepository.findActiveLobbyByLobbyPlayerPlayerId(playerId);

        if (activeLobby.isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "You are currently in a duel. Please forfeit the duel, then try again.");
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
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "This lobby already has the maximum number of players");
        }
    }

    @Operation(summary = "Join lobby", description = "Join a lobby by providing the lobby code.")
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
                        responseCode = "409",
                        description = """
                There is a conflict with the request; check message""",
                        content = @Content(schema = @Schema(implementation = UnsafeGenericFailureResponse.class))),
                @ApiResponse(responseCode = "200", description = "Lobby has been successfully joined!"),
            })
    @PostMapping("/lobby/join")
    public ResponseEntity<ApiResponder<Empty>> joinLobby(
            @Protected final AuthenticationObject authenticationObject,
            @RequestBody final JoinLobbyBody joinPartyBody) {
        if (env.isProd()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Endpoint is currently non-functional");
        }

        joinPartyBody.validate();

        var user = authenticationObject.getUser();

        var lobby = lobbyRepository
                .findAvailableLobbyByJoinCode(joinPartyBody.getPartyCode())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "The party with the given code cannot be found."));

        validateLobby(lobby);
        validatePlayerNotInLobby(user.getId());

        lobbyPlayerRepository.createLobbyPlayer(LobbyPlayer.builder()
                .lobbyId(lobby.getId())
                .playerId(user.getId())
                .build());

        lobby.setPlayerCount(lobby.getPlayerCount() + 1);
        boolean isSuccessful = lobbyRepository.updateLobby(lobby);

        if (!isSuccessful) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Failed to join party. Please try again later.");
        }

        return ResponseEntity.ok(ApiResponder.success("Party successfully joined!", Empty.of()));
    }

    @Operation(summary = "Start lobby", description = "Start a lobby")
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
                @ApiResponse(responseCode = "200", description = "Lobby has been successfully started!"),
            })
    @PostMapping("/lobby/start")
    public ResponseEntity<ApiResponder<Empty>> startLobby(@Protected final AuthenticationObject authenticationObject) {
        if (env.isProd()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Endpoint is currently non-functional");
        }

        var user = authenticationObject.getUser();

        LobbyPlayer player = lobbyPlayerRepository
                .findLobbyPlayerByPlayerId(user.getId())
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "You are not currently in a lobby!"));

        Lobby lobby = lobbyRepository
                .findLobbyById(player.getLobbyId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Could not find lobby!"));

        if (lobby.getStatus() != LobbyStatus.AVAILABLE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Lobby is not available!");
        }

        if (lobby.getPlayerCount() < 2) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You must have at least 2 players!");
        }

        lobby.setStatus(LobbyStatus.ACTIVE);
        lobbyRepository.updateLobby(lobby);

        QuestionBank randomQuestion = questionBankRepository.getRandomQuestion();

        LobbyQuestion lobbyQuestion = LobbyQuestion.builder()
                .lobbyId(lobby.getId())
                .questionBankId(randomQuestion.getId())
                .userSolvedCount(0)
                .build();

        lobbyQuestionRepository.createLobbyQuestion(lobbyQuestion);

        return ResponseEntity.ok(ApiResponder.success("Party successfully started!", Empty.of()));
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

        LobbyPlayer existingLobbyPlayer = lobbyPlayerRepository
                .findLobbyPlayerByPlayerId(playerId)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "You are not currently in a lobby."));

        String lobbyId = existingLobbyPlayer.getLobbyId();

        boolean isLobbyPlayerDeleted = lobbyPlayerRepository.deleteLobbyPlayerById(existingLobbyPlayer.getId());
        if (!isLobbyPlayerDeleted) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponder.failure("Failed to leave the lobby. Please try again."));
        }

        Lobby lobby = lobbyRepository
                .findLobbyById(lobbyId)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong."));

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
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "You are already in a lobby. Please leave your current lobby before creating a new one.");
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

        return ResponseEntity.ok(ApiResponder.success(
                "Lobby created successfully! Share the join code: " + lobby.getJoinCode(), Empty.of()));
    }

    // CHECKSTYLE:OFF
    @Operation(summary = "Submit question", description = "Submit a question for the current duel")
    @ApiResponse(responseCode = "200", description = "Question has been successfully submitted!")
    @ApiResponse(responseCode = "400", description = "lobby player not found")
    @ApiResponse(responseCode = "401", description = "User not authenticated")
    @ApiResponse(responseCode = "403", description = "Endpoint is currently non-functional")
    @ApiResponse(responseCode = "404", description = "Player is not in a duel")
    @ApiResponse(
            responseCode = "500",
            description = "Failed to retrieve LeetCode submissions or database update failed")
    @PostMapping("/question/submit")
    public ResponseEntity<ApiResponder<Empty>> submitQuestion(
            @Protected final AuthenticationObject authenticationObject) {
        if (env.isProd()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Endpoint is currently non-functional");
        }

        User user = authenticationObject.getUser();
        String playerId = user.getId();

        if (user.getLeetcodeUsername() == null || user.getLeetcodeUsername().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "You have not linked a LeetCode username to your profile");
        }

        LobbyPlayer lobbyPlayer = lobbyPlayerRepository
                .findLobbyPlayerByPlayerId(playerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Lobby player not found"));

        Lobby lobby = lobbyRepository
                .findActiveLobbyByLobbyPlayerPlayerId(playerId)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Player is not currently in an active duel"));

        List<LeetcodeSubmission> recentSubmissions;
        try {
            recentSubmissions = throttledLeetcodeClient.findSubmissionsByUsername(user.getLeetcodeUsername());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to reach LeetCode service");
        }

        if (recentSubmissions == null || recentSubmissions.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "No recent submissions found on your LeetCode profile");
        }

        int start = Math.max(0, recentSubmissions.size() - 5);
        List<LeetcodeSubmission> lastFiveSubmissions = recentSubmissions.subList(start, recentSubmissions.size());

        List<LobbyQuestion> lobbyQuestions = lobbyQuestionRepository.findLobbyQuestionsByLobbyId(lobby.getId());

        if (lobbyQuestions.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No questions assigned to this lobby");
        }

        LeetcodeSubmission matchedSubmission = null;
        LobbyQuestion matchedLobbyQuestion = null;
        QuestionBank matchedQuestionBank = null;

        for (int i = lastFiveSubmissions.size() - 1; i >= 0; i--) {
            LeetcodeSubmission submission = lastFiveSubmissions.get(i);

            String submissionId = String.valueOf(submission.getId());

            boolean submissionAlreadyExists = questionRepository.questionExistsBySubmissionId(submissionId);

            if (submissionAlreadyExists) {
                continue;
            }

            for (LobbyQuestion lobbyQuestion : lobbyQuestions) {
                QuestionBank questionBank = questionBankRepository.getQuestionById(lobbyQuestion.getQuestionBankId());

                if (questionBank != null && submission.getTitleSlug().equals(questionBank.getQuestionSlug())) {
                    List<LobbyPlayerQuestion> existingPlayerQuestions =
                            lobbyPlayerQuestionRepository.findQuestionsByLobbyPlayerId(lobbyPlayer.getId());

                    boolean alreadySubmitted = existingPlayerQuestions.stream()
                            .anyMatch(question -> question.getQuestionId().isPresent()
                                    && question.getQuestionId().get().equals(questionBank.getId()));

                    if (!alreadySubmitted) {
                        matchedSubmission = submission;
                        matchedLobbyQuestion = lobbyQuestion;
                        matchedQuestionBank = questionBank;
                        break;
                    }
                }
            }

            if (matchedSubmission != null) {
                break;
            }
        }

        if (matchedSubmission == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "No matching submission found in your last 5 submissions for any lobby question");
        }

        float multiplier = ScoreCalculator.calculateMultiplier(matchedQuestionBank.getQuestionDifficulty());
        int points = ScoreCalculator.calculateScore(
                matchedQuestionBank.getQuestionDifficulty(), matchedQuestionBank.getAcceptanceRate(), multiplier);

        Question question = Question.builder()
                .userId(user.getId())
                .questionSlug(matchedQuestionBank.getQuestionSlug())
                .questionTitle(matchedQuestionBank.getQuestionTitle())
                .questionDifficulty(matchedQuestionBank.getQuestionDifficulty())
                .questionNumber(matchedQuestionBank.getQuestionNumber())
                .questionLink(matchedQuestionBank.getQuestionLink())
                .description(matchedQuestionBank.getDescription())
                .pointsAwarded(points)
                .acceptanceRate(matchedQuestionBank.getAcceptanceRate())
                .submittedAt(matchedSubmission.getTimestamp())
                .submissionId(String.valueOf(matchedSubmission.getId()))
                .build();

        questionRepository.createQuestion(question);

        LobbyPlayerQuestion lobbyPlayerQuestion = LobbyPlayerQuestion.builder()
                .lobbyPlayerId(lobbyPlayer.getId())
                .questionId(Optional.of(matchedQuestionBank.getId()))
                .points(Optional.of(points))
                .build();

        lobbyPlayerQuestionRepository.createLobbyPlayerQuestion(lobbyPlayerQuestion);

        matchedLobbyQuestion.setUserSolvedCount(matchedLobbyQuestion.getUserSolvedCount() + 1);

        boolean lobbyQuestionUpdated = lobbyQuestionRepository.updateQuestionLobby(matchedLobbyQuestion);
        if (!lobbyQuestionUpdated) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update question submission");
        }

        Integer currentPointsObj = lobbyPlayer.getPoints();
        int currentPoints = (currentPointsObj != null) ? currentPointsObj : 0;
        lobbyPlayer.setPoints(currentPoints + points);

        boolean isPlayerUpdated = lobbyPlayerRepository.updateLobbyPlayer(lobbyPlayer);

        if (!isPlayerUpdated) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update player points");
        }

        try {
            log.info("Submitting question - lobby.getId() = {}", lobby.getId());
            lobbyNotifyHandler.handle(lobby.getId());
        } catch (IOException e) {
            log.error("Failed to notify lobby clients via SSE", e);
        }

        return ResponseEntity.ok(ApiResponder.success("Question has been successfully submitted!", Empty.of()));
    }

    // CHECKSTYLE:ON

    @Operation(summary = "SSE endpoint for duel data", description = """
                    Server-sent events endpoint for real-time duel updates

        NOTE - Our application runs on DigitalOcean, which does not allow SSE over GET requests. As a result, we are forced
        to use a non-standard SSE implementation over a POST method.
        See https://ideas.digitalocean.com/app-platform/p/http-response-streaming-in-app-platform-for-sse-support.
        """)
    @ApiResponse(responseCode = "200", description = "Sending live duel data")
    @ApiResponse(
            responseCode = "404",
            description = "Failed to establish SSE connection",
            content = @Content(schema = @Schema(implementation = UnsafeGenericFailureResponse.class)))
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
}
