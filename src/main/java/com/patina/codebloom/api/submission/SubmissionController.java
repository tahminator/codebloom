package com.patina.codebloom.api.submission;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.patina.codebloom.api.submission.body.LeetcodeUsernameObject;
import com.patina.codebloom.common.db.models.potd.POTD;
import com.patina.codebloom.common.db.models.question.Question;
import com.patina.codebloom.common.db.models.question.QuestionWithUser;
import com.patina.codebloom.common.db.models.user.User;
import com.patina.codebloom.common.db.repos.potd.POTDRepository;
import com.patina.codebloom.common.db.repos.question.QuestionRepository;
import com.patina.codebloom.common.db.repos.user.UserRepository;
import com.patina.codebloom.common.dto.ApiResponder;
import com.patina.codebloom.common.dto.Empty;
import com.patina.codebloom.common.dto.autogen.UnsafeEmptySuccessResponse;
import com.patina.codebloom.common.dto.autogen.UnsafeGenericFailureResponse;
import com.patina.codebloom.common.dto.autogen.UnsafeRateLimitResponse;
import com.patina.codebloom.common.dto.question.QuestionDto;
import com.patina.codebloom.common.lag.FakeLag;
import com.patina.codebloom.common.leetcode.LeetcodeClient;
import com.patina.codebloom.common.leetcode.models.LeetcodeSubmission;
import com.patina.codebloom.common.leetcode.models.UserProfile;
import com.patina.codebloom.common.leetcode.throttled.ThrottledLeetcodeClient;
import com.patina.codebloom.common.security.AuthenticationObject;
import com.patina.codebloom.common.security.Protector;
import com.patina.codebloom.common.simpleredis.SimpleRedis;
import com.patina.codebloom.common.submissions.SubmissionsHandler;
import com.patina.codebloom.common.submissions.object.AcceptedSubmission;
import com.patina.codebloom.common.time.StandardizedLocalDateTime;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@Tag(name = "LeetCode Routes", description = """
                This route is responsible for the actual submission logic,
                such as setting LeetCode username, fetching the problem of the day, retreiving submission data, and more.
                """)
@RequestMapping("/api/leetcode")
public class SubmissionController {
    // 5 Minute rate limit to avoid abuse.
    private static final double SECONDS_TO_WAIT = 5 * 60;

    private final UserRepository userRepository;
    private final Protector protector;
    private final SimpleRedis simpleRedis;
    private final LeetcodeClient leetcodeClient;
    private final SubmissionsHandler submissionsHandler;
    private final QuestionRepository questionRepository;
    private final POTDRepository potdRepository;

    /**
     * This checks if the different is 24 hours, instead of checking whether they
     * are actually part of the "same day".
     */
    private boolean isSameDay(final LocalDateTime createdAt) {
        LocalDateTime now = StandardizedLocalDateTime.now();
        Duration duration = Duration.between(createdAt, now);

        return duration.toHours() < 24;
    }

    public SubmissionController(final UserRepository userRepository, final Protector protector, final SimpleRedis simpleRedis,
                    final ThrottledLeetcodeClient throttledLeetcodeClient,
                    final SubmissionsHandler submissionsHandler, final QuestionRepository questionRepository,
                    final POTDRepository potdRepository) {
        this.userRepository = userRepository;
        this.protector = protector;
        this.simpleRedis = simpleRedis;
        this.leetcodeClient = throttledLeetcodeClient;
        this.submissionsHandler = submissionsHandler;
        this.questionRepository = questionRepository;
        this.potdRepository = potdRepository;
    }

    @Operation(summary = "Returns the currently authenticated user's verification key.", description = """
                            Protected endpoint that returns the currently authenticated user's verification key. In order to set their Leetcode username,
                            users must change their About Me in order to pass validation.
                    """, responses = {
            @ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content(schema = @Schema(implementation = UnsafeGenericFailureResponse.class))),
            @ApiResponse(responseCode = "200", description = "Successfully retrieved key") })
    @GetMapping("/key")
    public ResponseEntity<ApiResponder<String>> getVerificationKey(final HttpServletRequest request) {
        FakeLag.sleep(350);

        AuthenticationObject authenticationObject = protector.validateSession(request);
        User user = authenticationObject.getUser();

        return ResponseEntity.ok().body(ApiResponder.success("Successfully retreived authentication key", user.getVerifyKey()));

    }

    @Operation(summary = "Set a Leetcode username for the current user", description = """
                    Protected endpoint that allows a user to submit a JSON with the leetcode username they would like to add.
                    Cannot re-use this endpoint once a name is set.
                    """, responses = {
            @ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content(schema = @Schema(implementation = UnsafeGenericFailureResponse.class))),
            @ApiResponse(responseCode = "200", description = "Name has been set successfully", content = @Content(schema = @Schema(implementation = UnsafeEmptySuccessResponse.class))),
            @ApiResponse(responseCode = "409", description = "Attempt to set name that's already been set", content = @Content(schema = @Schema(implementation = UnsafeGenericFailureResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid username", content = @Content(schema = @Schema(implementation = UnsafeGenericFailureResponse.class))) })
    @PostMapping("/set")
    public ResponseEntity<ApiResponder<Empty>> setLeetcodeUsername(final HttpServletRequest request,
                    @Valid @RequestBody final LeetcodeUsernameObject leetcodeUsernameObject) {
        FakeLag.sleep(350);

        AuthenticationObject authenticationObject = protector.validateSession(request);
        User user = authenticationObject.getUser();

        if (user.getLeetcodeUsername() != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                            "User has already set a username previously. You cannot change your name anymore. Please contact support if there are any issues.");
        }

        UserProfile leetcodeUserProfile = leetcodeClient.getUserProfile(leetcodeUsernameObject.getLeetcodeUsername());
        String aboutMe = leetcodeUserProfile.getAboutMe();
        if (aboutMe == null || !aboutMe.equals(user.getVerifyKey())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                            "The verification key did not match the user's about me or the about me did not exist.");
        }

        if (userRepository.userExistsByLeetcodeUsername(leetcodeUsernameObject.getLeetcodeUsername())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                            "This username has already been taken. If this is a mistake, please get in touch with us so we can attempt to rectify it.");
        }

        user.setLeetcodeUsername(leetcodeUsernameObject.getLeetcodeUsername());
        user.setProfileUrl(leetcodeUserProfile.getUserAvatar());
        userRepository.updateUser(user);

        return ResponseEntity.ok().body(ApiResponder.success("Leetcode username has been set!", Empty.of()));

    }

    @Operation(summary = "Check the current user's LeetCode submissions and update leaderboard", description = """
                    Protected endpoint that handles the logic of checking the most recent submissions,
                    as well as updating the current leaderboard with any new points the user has accumulated.
                    There is a rate limit on the route to prevent abuse (currently: 5 minutes).
                    """, responses = {
            @ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content(schema = @Schema(implementation = UnsafeGenericFailureResponse.class))),
            @ApiResponse(responseCode = "200", description = "The check was completed successfuly"),
            @ApiResponse(responseCode = "412", description = "Leetcode username hasn't been set", content = @Content(schema = @Schema(implementation = UnsafeGenericFailureResponse.class))),
            @ApiResponse(responseCode = "429", description = "Rate limited", content = @Content(schema = @Schema(implementation = UnsafeRateLimitResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid username", content = @Content(schema = @Schema(implementation = UnsafeGenericFailureResponse.class))) })
    @PostMapping("/check")
    public ResponseEntity<ApiResponder<ArrayList<AcceptedSubmission>>> checkLatestSubmissions(final HttpServletRequest request) {
        AuthenticationObject authenticationObject = protector.validateSession(request);
        User user = authenticationObject.getUser();

        if (user.getLeetcodeUsername() == null) {
            throw new ResponseStatusException(HttpStatus.PRECONDITION_FAILED,
                            "You cannot access this resource without setting a Leetcode username first.");
        }

        if (simpleRedis.containsKey(0, user.getId())) {
            long timeThen = (long) simpleRedis.get(0, user.getId());
            long timeNow = System.currentTimeMillis();
            long difference = (timeNow - timeThen) / 1000;

            if (difference < SECONDS_TO_WAIT) {
                long remainingTime = (long) SECONDS_TO_WAIT - difference;
                throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, Long.toString(remainingTime));
            }
        }

        simpleRedis.put(0, user.getId(), System.currentTimeMillis());

        List<LeetcodeSubmission> leetcodeSubmissions = leetcodeClient.findSubmissionsByUsername(user.getLeetcodeUsername());

        return ResponseEntity.ok().body(ApiResponder.success("Successfully checked all recent submissions!",
                        submissionsHandler.handleSubmissions(leetcodeSubmissions, user)));
    }

    @Operation(summary = "Returns current problem of the day.", description = """
                    Returns the current problem of the day, as long as there is a problem of the day set and the user hasn't completed the problem already.
                    """, responses = {
            @ApiResponse(responseCode = "200", description = "POTD found"),
            @ApiResponse(responseCode = "404", description = "POTD not found", content = @Content(schema = @Schema(implementation = UnsafeGenericFailureResponse.class))),
            @ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content(schema = @Schema(implementation = UnsafeGenericFailureResponse.class))) })
    @GetMapping("/potd")
    public ResponseEntity<ApiResponder<POTD>> getCurrentPotd(final HttpServletRequest request) {
        FakeLag.sleep(750);

        AuthenticationObject authenticationObject = protector.validateSession(request);
        User user = authenticationObject.getUser();

        POTD potd = potdRepository.getCurrentPOTD();

        if (potd == null || !isSameDay(potd.getCreatedAt())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponder.failure("Sorry, no problem of the day today!"));
        }

        Question completedQuestion = questionRepository.getQuestionBySlugAndUserId(potd.getSlug(), user.getId());

        if (completedQuestion != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponder
                            .failure("Nice, you have already completed the problem of the day! Come back tomorrow for a new one!"));
        }

        return ResponseEntity.ok().body(ApiResponder.success("Problem of the day has been fetched!", potd));
    }

    @Operation(summary = "Returns submission data.", description = """
                    Returns the submission data from any user, as long as the user making the request is authenticated.
                    This includes the scraped LeetCode description, which is HTML that has been sanitized by the server,
                    so it is safe to use on the frontend.
                    """, responses = {
            @ApiResponse(responseCode = "200", description = "Question found"),
            @ApiResponse(responseCode = "404", description = "Question not found", content = @Content(schema = @Schema(implementation = UnsafeGenericFailureResponse.class))),
            @ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content(schema = @Schema(implementation = UnsafeGenericFailureResponse.class))) })
    @GetMapping("/submission/{submissionId}")
    public ResponseEntity<ApiResponder<QuestionDto>> getSubmissionBySubmissionId(final HttpServletRequest request,
                    @PathVariable final String submissionId) {
        FakeLag.sleep(750);

        QuestionWithUser question = questionRepository.getQuestionWithUserById(submissionId);

        if (question == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponder.failure("Sorry, submission could not be found."));
        }

        return ResponseEntity.ok().body(ApiResponder.success("Problem of the day has been fetched!", QuestionDto.fromQuestion(question)));
    }
}
