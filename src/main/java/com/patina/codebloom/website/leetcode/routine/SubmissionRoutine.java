package com.patina.codebloom.website.leetcode.routine;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.patina.codebloom.website.auth.model.User;
import com.patina.codebloom.website.auth.repo.UserRepository;
import com.patina.codebloom.website.leetcode.client.LeetcodeApiClient;
import com.patina.codebloom.website.leetcode.client.model.LeetcodeSubmission;
import com.patina.codebloom.website.leetcode.service.SubmissionService;

/**
 * This routine is used to automatically collect any new
 * submissions from our users every K minutes, the frequency
 * of which is determined by the cron string.
 */
@Component
public class SubmissionRoutine {
    private static final Logger LOGGER = LoggerFactory.getLogger(SubmissionRoutine.class);

    private final UserRepository userRepository;
    private final LeetcodeApiClient leetcodeApiClient;
    private final SubmissionService submissionService;

    public SubmissionRoutine(final UserRepository userRepository, final LeetcodeApiClient leetcodeApiClient,
                    final SubmissionService submissionService) {
        this.userRepository = userRepository;
        this.leetcodeApiClient = leetcodeApiClient;
        this.submissionService = submissionService;
    }

    // Cron runs every 30 minutes
    // TODO - Discuss this value
    @Scheduled(cron = "0 */30 * * * *")
    public void handleAllUserSubmissions() {
        LOGGER.info("Beginning the scheduled task to handle all user submissions now:");
        ArrayList<User> users = userRepository.getAllUsers();

        for (User user : users) {
            if (user.getLeetcodeUsername() == null) {
                LOGGER.error("User with id of {} does not have a leetcode username set.", user.getId());
                continue;
            }

            ArrayList<LeetcodeSubmission> leetcodeSubmissions = leetcodeApiClient.findSubmissionsByUsername(user.getLeetcodeUsername());

            submissionService.handleSubmissions(leetcodeSubmissions, user);
            LOGGER.info("User with id of {} has been completed", user.getId());
        }

        LOGGER.info("Scheduled task complete");
    }
}
