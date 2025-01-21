package com.patina.codebloom.api.submission;

import java.util.ArrayList;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.patina.codebloom.api.submission.body.LeetcodeUsernameObject;
import com.patina.codebloom.common.db.models.user.User;
import com.patina.codebloom.common.db.repos.user.UserRepository;
import com.patina.codebloom.common.dto.ApiResponder;
import com.patina.codebloom.common.kv.KeyValueStore;
import com.patina.codebloom.common.leetcode.LeetcodeApiHandler;
import com.patina.codebloom.common.leetcode.models.LeetcodeSubmission;
import com.patina.codebloom.common.security.AuthenticationObject;
import com.patina.codebloom.common.security.Protector;
import com.patina.codebloom.common.submissions.SubmissionsHandler;
import com.patina.codebloom.common.submissions.object.AcceptedSubmissions;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
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

        public SubmissionController(UserRepository userRepository,
                        Protector protector,
                        KeyValueStore keyValueStore, LeetcodeApiHandler leetcodeApiHandler,
                        SubmissionsHandler submissionsHandler) {
                this.userRepository = userRepository;
                this.protector = protector;
                this.keyValueStore = keyValueStore;
                this.leetcodeApiHandler = leetcodeApiHandler;
                this.submissionsHandler = submissionsHandler;
        }

        @PostMapping("/set")
        public ResponseEntity<ApiResponder<Void>> setLeetcodeUsername(HttpServletRequest request,
                        @Valid @RequestBody LeetcodeUsernameObject leetcodeUsernameObject) {
                AuthenticationObject authenticationObject = protector.validateSession(request);
                User user = authenticationObject.getUser();

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

        @PostMapping("/check")
        public ResponseEntity<ApiResponder<AcceptedSubmissions>> checkLatestSubmissions(HttpServletRequest request) {
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
                                                "You have already scanned your submissions recently. You may try again in "
                                                                + remainingTime + " seconds.");
                        }
                }

                keyValueStore.put(user.getId(), System.currentTimeMillis());

                ArrayList<LeetcodeSubmission> leetcodeSubmissions = leetcodeApiHandler
                                .findSubmissionsByUsername(user.getLeetcodeUsername());

                return ResponseEntity.ok().body(ApiResponder.success("Successfully checked all recent submissions!",
                                submissionsHandler.handleSubmissions(leetcodeSubmissions, user)));
        }
}
