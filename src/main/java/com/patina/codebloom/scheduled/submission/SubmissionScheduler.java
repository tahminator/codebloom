package com.patina.codebloom.scheduled.submission;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.patina.codebloom.common.db.models.user.User;
import com.patina.codebloom.common.db.repos.user.UserRepository;
import com.patina.codebloom.common.leetcode.LeetcodeApiHandler;
import com.patina.codebloom.common.leetcode.models.LeetcodeSubmission;
import com.patina.codebloom.common.submissions.SubmissionsHandler;

@Component
public class SubmissionScheduler {
    private static final Logger LOGGER = LoggerFactory.getLogger(SubmissionScheduler.class);

    private final UserRepository userRepository;
    private final LeetcodeApiHandler leetcodeApiHandler;
    private final SubmissionsHandler submissionsHandler;

    public SubmissionScheduler(final UserRepository userRepository, final LeetcodeApiHandler leetcodeApiHandler,
                    final SubmissionsHandler submissionsHandler) {
        this.userRepository = userRepository;
        this.leetcodeApiHandler = leetcodeApiHandler;
        this.submissionsHandler = submissionsHandler;
    }

    // Cron runs every 30 minutes
    @Scheduled(cron = "0 */30 * * * *")
    public void handleAllUserSubmissions() {
        LOGGER.info("Beginning the scheduled task to handle all user submissions now:");
        ArrayList<User> users = userRepository.getAllUsers();

        for (User user : users) {
            if (user.getLeetcodeUsername() == null) {
                LOGGER.info("User with id of {} does not have a leetcode username set.", user.getId());
                continue;
            }

            ArrayList<LeetcodeSubmission> leetcodeSubmissions = leetcodeApiHandler
                            .findSubmissionsByUsername(user.getLeetcodeUsername());

            submissionsHandler.handleSubmissions(leetcodeSubmissions, user);
            LOGGER.info("User with id of {} has been completed", user.getId());
        }

        LOGGER.info("Scheduled task complete");
    }
}
