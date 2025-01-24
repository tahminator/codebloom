package com.patina.codebloom.api.submission;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
import com.patina.codebloom.common.dto.autogen.__DO_NOT_USE_UNLESS_YOU_KNOW_WHAT_YOU_ARE_DOING_EMPTY_SUCCESS_RESPONSE;
import com.patina.codebloom.common.dto.autogen.__DO_NOT_USE_UNLESS_YOU_KNOW_WHAT_YOU_ARE_DOING_EXAMPLE_SUBMISSION_CHECK_SUCCESS_RESPONSE;
import com.patina.codebloom.common.dto.autogen.__DO_NOT_USE_UNLESS_YOU_KNOW_WHAT_YOU_ARE_DOING_GENERIC_FAILURE_RESPONSE;
import com.patina.codebloom.common.dto.autogen.__DO_NOT_USE_UNLESS_YOU_KNOW_WHAT_YOU_ARE_DOING_RATE_LIMIT_FAILURE_RESPONSE;
import com.patina.codebloom.common.dto.autogen.__DO_NOT_USE_UNLESS_YOU_KNOW_WHAT_YOU_ARE_DOING_SUBMISSION_SUCCESS_RESPONSE;
import com.patina.codebloom.common.kv.KeyValueStore;
import com.patina.codebloom.common.lag.FakeLag;
import com.patina.codebloom.common.leetcode.LeetcodeApiHandler;
import com.patina.codebloom.common.leetcode.models.LeetcodeSubmission;
import com.patina.codebloom.common.page.Page;
import com.patina.codebloom.common.security.AuthenticationObject;
import com.patina.codebloom.common.security.Protector;
import com.patina.codebloom.common.submissions.SubmissionsHandler;
import com.patina.codebloom.common.submissions.object.AcceptedSubmission;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@Tag(name = "LeetCode Submission Routes")
@RequestMapping("/api/leetcode")
public class SubmissionController {
        /* Page size for submissions */
        private final int submissionsPageSize = 10;

        // 5 Minute rate limit to avoid abuse.
        // TODO - Change this from 5 seconds back to 5 minutes once done testing.
        private double SECONDS_TO_WAIT = 1 * 60;

        private final UserRepository userRepository;
        private final Protector protector;
        private final KeyValueStore keyValueStore;
        private final LeetcodeApiHandler leetcodeApiHandler;
        private final SubmissionsHandler submissionsHandler;
        private final QuestionRepository questionRepository;
        private final POTDRepository potdRepository;

        private boolean isSameDay(LocalDateTime createdAt) {
                LocalDate createdAtDate = createdAt.toLocalDate();
                LocalDate today = LocalDate.now();

                return createdAtDate.equals(today);
        }

        public SubmissionController(UserRepository userRepository,
                        Protector protector,
                        KeyValueStore keyValueStore, LeetcodeApiHandler leetcodeApiHandler,
                        SubmissionsHandler submissionsHandler, QuestionRepository questionRepository,
                        POTDRepository potdRepository) {
                this.userRepository = userRepository;
                this.protector = protector;
                this.keyValueStore = keyValueStore;
                this.leetcodeApiHandler = leetcodeApiHandler;
                this.submissionsHandler = submissionsHandler;
                this.questionRepository = questionRepository;
                this.potdRepository = potdRepository;
        }

        @Operation(summary = "Set a Leetcode username for the current user", description = "Protected endpoint that allows a user to submit a JSON with the leetcode username they would like to add. Cannot re-use this endpoint once a name is set.", responses = {
                        @ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content(schema = @Schema(implementation = __DO_NOT_USE_UNLESS_YOU_KNOW_WHAT_YOU_ARE_DOING_GENERIC_FAILURE_RESPONSE.class))),
                        @ApiResponse(responseCode = "200", description = "Name has been set successfully", content = @Content(schema = @Schema(implementation = __DO_NOT_USE_UNLESS_YOU_KNOW_WHAT_YOU_ARE_DOING_EMPTY_SUCCESS_RESPONSE.class))),
                        @ApiResponse(responseCode = "409", description = "Attempt to set name that has already been set", content = @Content(schema = @Schema(implementation = __DO_NOT_USE_UNLESS_YOU_KNOW_WHAT_YOU_ARE_DOING_GENERIC_FAILURE_RESPONSE.class))),
                        @ApiResponse(responseCode = "400", description = "Invalid username", content = @Content(schema = @Schema(implementation = __DO_NOT_USE_UNLESS_YOU_KNOW_WHAT_YOU_ARE_DOING_GENERIC_FAILURE_RESPONSE.class)))
        })
        @PostMapping("/set")
        public ResponseEntity<ApiResponder<Void>> setLeetcodeUsername(HttpServletRequest request,
                        @Valid @RequestBody LeetcodeUsernameObject leetcodeUsernameObject) {
                FakeLag.sleep(350);

                AuthenticationObject authenticationObject = protector.validateSession(request);
                User user = authenticationObject.getUser();

                if (user.getLeetcodeUsername() != null) {
                        throw new ResponseStatusException(HttpStatus.CONFLICT,
                                        "User has already set a username previously. You cannot change your name anymore. Please contact support if there are any issues.");
                }

                ArrayList<LeetcodeSubmission> leetcodeSubmissions = leetcodeApiHandler
                                .findSubmissionsByUsername(leetcodeUsernameObject.getLeetcodeUsername());

                if (leetcodeSubmissions.size() == 0) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                        "The username is not valid. Please make sure the LeetCode username is valid, and has completed atleast one problem before.");
                }

                user.setLeetcodeUsername(leetcodeUsernameObject.getLeetcodeUsername());
                userRepository.updateUser(user);

                return ResponseEntity.ok().body(ApiResponder.success("Leetcode username has been set!", null));

        }

        @Operation(summary = "Check the current user's LeetCode submissions and update leaderboard", description = "Protected endpoint that handles the logic of checking the most recent submissions as well as updating the current leaderboard with any new points the user has accumulated. There is a rate limit on the route to prevent abuse (currently: 5 minutes).", responses = {
                        @ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content(schema = @Schema(implementation = __DO_NOT_USE_UNLESS_YOU_KNOW_WHAT_YOU_ARE_DOING_GENERIC_FAILURE_RESPONSE.class))),
                        @ApiResponse(responseCode = "200", description = "The check was completed successfuly", content = @Content(schema = @Schema(implementation = __DO_NOT_USE_UNLESS_YOU_KNOW_WHAT_YOU_ARE_DOING_EXAMPLE_SUBMISSION_CHECK_SUCCESS_RESPONSE.class))),
                        @ApiResponse(responseCode = "412", description = "Leetcode username hasn't been set", content = @Content(schema = @Schema(implementation = __DO_NOT_USE_UNLESS_YOU_KNOW_WHAT_YOU_ARE_DOING_GENERIC_FAILURE_RESPONSE.class))),
                        @ApiResponse(responseCode = "429", description = "Rate limited", content = @Content(schema = @Schema(implementation = __DO_NOT_USE_UNLESS_YOU_KNOW_WHAT_YOU_ARE_DOING_RATE_LIMIT_FAILURE_RESPONSE.class))),
                        @ApiResponse(responseCode = "400", description = "Invalid username", content = @Content(schema = @Schema(implementation = __DO_NOT_USE_UNLESS_YOU_KNOW_WHAT_YOU_ARE_DOING_GENERIC_FAILURE_RESPONSE.class)))
        })
        @PostMapping("/check")
        public ResponseEntity<ApiResponder<ArrayList<AcceptedSubmission>>> checkLatestSubmissions(
                        HttpServletRequest request) {
                FakeLag.sleep(350);

                AuthenticationObject authenticationObject = protector.validateSession(request);
                User user = authenticationObject.getUser();

                if (user.getLeetcodeUsername() == null) {
                        throw new ResponseStatusException(HttpStatus.PRECONDITION_FAILED,
                                        "You cannot access this resource without setting a Leetcode username first.");
                }

                if (keyValueStore.containsKey(user.getId())) {
                        long timeThen = (long) keyValueStore.get(user.getId());
                        long timeNow = System.currentTimeMillis();
                        long difference = (timeNow - timeThen) / 1000;

                        if (difference < SECONDS_TO_WAIT) {
                                long remainingTime = (long) SECONDS_TO_WAIT - difference;
                                throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS,
                                                Long.toString(remainingTime));
                        }
                }

                keyValueStore.put(user.getId(), System.currentTimeMillis());

                ArrayList<LeetcodeSubmission> leetcodeSubmissions = leetcodeApiHandler
                                .findSubmissionsByUsername(user.getLeetcodeUsername());

                return ResponseEntity.ok().body(ApiResponder.success("Successfully checked all recent submissions!",
                                submissionsHandler.handleSubmissions(leetcodeSubmissions, user)));
        }

        @Operation(summary = "Returns a list of the questions successfully submitted by the user.", description = "Protected endpoint that returns the list of questions completed by the user. These questions are guaranteed to be completed by the user.", responses = {
                        @ApiResponse(responseCode = "200", description = "Successful"),
                        @ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content(schema = @Schema(implementation = __DO_NOT_USE_UNLESS_YOU_KNOW_WHAT_YOU_ARE_DOING_GENERIC_FAILURE_RESPONSE.class))) })
        @GetMapping("submission/u/{userId}")
        public ResponseEntity<ApiResponder<Page<ArrayList<QuestionWithUser>>>> getAllQuestionsForUser(
                        HttpServletRequest request,
                        @Parameter(description = "Page index", example = "1") @RequestParam(required = false, defaultValue = "1") int page,
                        @PathVariable String userId) {
                FakeLag.sleep(250);

                protector.validateSession(request);

                ArrayList<QuestionWithUser> questions = questionRepository.getQuestionsByUserId(userId, page,
                                submissionsPageSize);

                int totalQuestions = questionRepository.getQuestionCountByUserId(userId);
                int totalPages = (int) Math.ceil((double) totalQuestions / submissionsPageSize);
                boolean hasNextPage = page < totalPages;

                Page<ArrayList<QuestionWithUser>> createdPage = new Page<>(hasNextPage, questions, totalPages);

                return ResponseEntity.ok().body(ApiResponder.success("All questions have been fetched!", createdPage));
        }

        @Operation(summary = "Returns current problem of the day.", description = "Returns the current problem of the day, as long as there is a problem of the day set and the user hasn't completed the problem already.", responses = {
                        @ApiResponse(responseCode = "200", description = "POTD found"),
                        @ApiResponse(responseCode = "404", description = "POTD not found", content = @Content(schema = @Schema(implementation = __DO_NOT_USE_UNLESS_YOU_KNOW_WHAT_YOU_ARE_DOING_GENERIC_FAILURE_RESPONSE.class))),
                        @ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content(schema = @Schema(implementation = __DO_NOT_USE_UNLESS_YOU_KNOW_WHAT_YOU_ARE_DOING_GENERIC_FAILURE_RESPONSE.class))) })
        @GetMapping("/potd")
        public ResponseEntity<ApiResponder<POTD>> getCurrentPotd(HttpServletRequest request) {
                FakeLag.sleep(750);

                AuthenticationObject authenticationObject = protector.validateSession(request);
                User user = authenticationObject.getUser();

                POTD potd = potdRepository.getCurrentPOTD();

                if (potd == null || !isSameDay(potd.getCreatedAt())) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                        .body(ApiResponder.failure("Sorry, no problem of the day today!"));
                }

                Question completedQuestion = questionRepository.getQuestionBySlugAndUserId(potd.getSlug(),
                                user.getId());

                if (completedQuestion != null) {
                        return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponder.failure(
                                        "Nice, you have already completed the problem of the day! Come back tomorrow for a new one!"));
                }

                return ResponseEntity.ok().body(ApiResponder.success("Problem of the day has been fetched!", potd));
        }

        @Operation(summary = "Returns submission data.", description = "Returns the submission data from any user, as long as the user making the request is authenticated. This includes the scraped LeetCode description, which is HTML that has been sanitized by the server, so it is safe to use on the frontend.", responses = {
                        @ApiResponse(responseCode = "200", description = "Question found", content = @Content(schema = @Schema(implementation = __DO_NOT_USE_UNLESS_YOU_KNOW_WHAT_YOU_ARE_DOING_SUBMISSION_SUCCESS_RESPONSE.class))),
                        @ApiResponse(responseCode = "404", description = "Question not found", content = @Content(schema = @Schema(implementation = __DO_NOT_USE_UNLESS_YOU_KNOW_WHAT_YOU_ARE_DOING_GENERIC_FAILURE_RESPONSE.class))),
                        @ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content(schema = @Schema(implementation = __DO_NOT_USE_UNLESS_YOU_KNOW_WHAT_YOU_ARE_DOING_GENERIC_FAILURE_RESPONSE.class))) })
        @GetMapping("/submission/s/{submissionId}")
        public ResponseEntity<ApiResponder<Question>> getSubmissionBySubmissionId(HttpServletRequest request,
                        @PathVariable String submissionId) {
                FakeLag.sleep(750);

                protector.validateSession(request);
                QuestionWithUser question = questionRepository.getQuestionWithUserById(submissionId);

                if (question == null) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                        .body(ApiResponder.failure("Sorry, submission could not be found."));
                }

                return ResponseEntity.ok().body(ApiResponder.success("Problem of the day has been fetched!", question));
        }
}
