package com.patina.codebloom.api.submission;

import java.util.ArrayList;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.patina.codebloom.api.submission.body.LeetcodeUsernameObject;
import com.patina.codebloom.common.db.models.question.Question;
import com.patina.codebloom.common.db.models.user.User;
import com.patina.codebloom.common.db.repos.question.QuestionRepository;
import com.patina.codebloom.common.db.repos.user.UserRepository;
import com.patina.codebloom.common.dto.ApiResponder;
import com.patina.codebloom.common.dto.autogen.__DO_NOT_USE_UNLESS_YOU_KNOW_WHAT_YOU_ARE_DOING_EMPTY_SUCCESS_RESPONSE;
import com.patina.codebloom.common.dto.autogen.__DO_NOT_USE_UNLESS_YOU_KNOW_WHAT_YOU_ARE_DOING_EXAMPLE_SUBMISSION_CHECK_SUCCESS_RESPONSE;
import com.patina.codebloom.common.dto.autogen.__DO_NOT_USE_UNLESS_YOU_KNOW_WHAT_YOU_ARE_DOING_GENERIC_FAILURE_RESPONSE;
import com.patina.codebloom.common.dto.autogen.__DO_NOT_USE_UNLESS_YOU_KNOW_WHAT_YOU_ARE_DOING_RATE_LIMIT_FAILURE_RESPONSE;
import com.patina.codebloom.common.kv.KeyValueStore;
import com.patina.codebloom.common.leetcode.LeetcodeApiHandler;
import com.patina.codebloom.common.leetcode.models.LeetcodeSubmission;
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
        // 5 Minute rate limit to avoid abuse.
        // TODO - Change this from 5 seconds back to 5 minutes once done testing.
        private double SECONDS_TO_WAIT = 0.1 * 60;

        private final UserRepository userRepository;
        private final Protector protector;
        private final KeyValueStore keyValueStore;
        private final LeetcodeApiHandler leetcodeApiHandler;
        private final SubmissionsHandler submissionsHandler;
        private final QuestionRepository questionRepository;

        public SubmissionController(UserRepository userRepository,
                        Protector protector,
                        KeyValueStore keyValueStore, LeetcodeApiHandler leetcodeApiHandler,
                        SubmissionsHandler submissionsHandler, QuestionRepository questionRepository) {
                this.userRepository = userRepository;
                this.protector = protector;
                this.keyValueStore = keyValueStore;
                this.leetcodeApiHandler = leetcodeApiHandler;
                this.submissionsHandler = submissionsHandler;
                this.questionRepository = questionRepository;
        }

        @Operation(summary = "Set a Leetcode username for the current user", description = "Protected endpoint that allows a user to submit a JSON with the leetcode username they would like to add. Cannot re-use this endpoint once a name is set.", responses = {
                        @ApiResponse(responseCode = "200", description = "Name has been set successfully", content = @Content(schema = @Schema(implementation = __DO_NOT_USE_UNLESS_YOU_KNOW_WHAT_YOU_ARE_DOING_EMPTY_SUCCESS_RESPONSE.class))),
                        @ApiResponse(responseCode = "409", description = "Attempt to set name that has already been set", content = @Content(schema = @Schema(implementation = __DO_NOT_USE_UNLESS_YOU_KNOW_WHAT_YOU_ARE_DOING_GENERIC_FAILURE_RESPONSE.class))),
                        @ApiResponse(responseCode = "400", description = "Invalid username", content = @Content(schema = @Schema(implementation = __DO_NOT_USE_UNLESS_YOU_KNOW_WHAT_YOU_ARE_DOING_GENERIC_FAILURE_RESPONSE.class)))
        })
        @PostMapping("/set")
        public ResponseEntity<ApiResponder<Void>> setLeetcodeUsername(HttpServletRequest request,
                        @Valid @RequestBody LeetcodeUsernameObject leetcodeUsernameObject) {
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
                        @ApiResponse(responseCode = "200", description = "The check was completed successfuly", content = @Content(schema = @Schema(implementation = __DO_NOT_USE_UNLESS_YOU_KNOW_WHAT_YOU_ARE_DOING_EXAMPLE_SUBMISSION_CHECK_SUCCESS_RESPONSE.class))),
                        @ApiResponse(responseCode = "412", description = "Leetcode username hasn't been set", content = @Content(schema = @Schema(implementation = __DO_NOT_USE_UNLESS_YOU_KNOW_WHAT_YOU_ARE_DOING_GENERIC_FAILURE_RESPONSE.class))),
                        @ApiResponse(responseCode = "429", description = "Rate limited", content = @Content(schema = @Schema(implementation = __DO_NOT_USE_UNLESS_YOU_KNOW_WHAT_YOU_ARE_DOING_RATE_LIMIT_FAILURE_RESPONSE.class))),
                        @ApiResponse(responseCode = "400", description = "Invalid username", content = @Content(schema = @Schema(implementation = __DO_NOT_USE_UNLESS_YOU_KNOW_WHAT_YOU_ARE_DOING_GENERIC_FAILURE_RESPONSE.class)))
        })
        @PostMapping("/check")
        public ResponseEntity<ApiResponder<ArrayList<AcceptedSubmission>>> checkLatestSubmissions(
                        HttpServletRequest request) {
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

        @Operation()
        @GetMapping("/all")
        public ResponseEntity<ApiResponder<ArrayList<Question>>> getAllQuestionsForUser(HttpServletRequest request,
                        @Parameter(description = "Pagination start index", example = "0") @RequestParam(required = false, defaultValue = "0") int start,
                        @Parameter(description = "Pagination end index", example = "5") @RequestParam(required = false, defaultValue = "5") int end) {
                AuthenticationObject authenticationObject = protector.validateSession(request);
                User user = authenticationObject.getUser();

                ArrayList<Question> questions = questionRepository.getQuestionsByUserId(user.getId(), start, end);

                return ResponseEntity.ok().body(ApiResponder.success("All questions have been fetched!", questions));
        }
}
