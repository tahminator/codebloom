package com.patina.codebloom.leetcode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.patina.codebloom.common.leetcode.LeetcodeClient;
import com.patina.codebloom.common.leetcode.models.LeetcodeDetailedQuestion;
import com.patina.codebloom.common.leetcode.models.LeetcodeQuestion;
import com.patina.codebloom.common.leetcode.models.LeetcodeSubmission;
import com.patina.codebloom.common.leetcode.models.POTD;
import com.patina.codebloom.common.leetcode.models.UserProfile;
import com.patina.codebloom.common.leetcode.throttled.ThrottledLeetcodeClient;

@SpringBootTest
public class LeetcodeClientTest {
    private final LeetcodeClient leetcodeClient;

    @Autowired
    public LeetcodeClientTest(final ThrottledLeetcodeClient throttledLeetcodeClient) {
        this.leetcodeClient = throttledLeetcodeClient;
    }

    @Test
    void questionSlugValid() {
        LeetcodeQuestion question = leetcodeClient.findQuestionBySlug("trapping-rain-water");

        assertTrue(question != null);

        assertTrue(question.getLink() != null);
        assertTrue(question.getLink().length() != 0);

        assertTrue(question.getQuestionId() != 0);

        assertTrue(question.getQuestionTitle() != null);
        assertTrue(question.getQuestionTitle().length() != 0);

        assertTrue(question.getTitleSlug() != null);
        assertTrue(question.getTitleSlug().length() != 0);

        assertTrue(question.getDifficulty() != null);
        assertTrue(question.getDifficulty().length() != 0);

        assertTrue(question.getQuestion() != null);
        assertTrue(question.getQuestion().length() != 0);

        assertTrue(question.getAcceptanceRate() != 0.0f);
    }

    @Test
    void submissionIdValid() {
        LeetcodeDetailedQuestion submission = leetcodeClient.findSubmissionDetailBySubmissionId(1588648200);

        assertTrue(submission != null);

        assertTrue(submission.getRuntimeDisplay() != null);
        assertTrue(submission.getRuntimeDisplay().length() != 0);

        assertTrue(submission.getRuntimePercentile() != 0.0f);

        assertTrue(submission.getMemoryDisplay() != null);
        assertTrue(submission.getMemoryDisplay().length() != 0);

        assertTrue(submission.getMemoryPercentile() != 0.0f);

        assertTrue(submission.getCode() != null);
        assertTrue(submission.getCode().length() != 0);

        assertTrue(submission.getLang() != null);
    }

    @Test
    void potdValid() {
        POTD potd = leetcodeClient.getPotd();

        assertTrue(potd != null);

        assertTrue(potd.getTitle() != null);
        assertTrue(potd.getTitle().length() != 0);

        assertTrue(potd.getTitleSlug() != null);
        assertTrue(potd.getTitleSlug().length() != 0);

        assertTrue(potd.getDifficulty() != null);
    }

    @Test
    void userProfileValid() {
        UserProfile profile = leetcodeClient.getUserProfile("az2924");

        assertTrue(profile != null);

        assertTrue(profile.getUsername() != null);
        assertTrue(profile.getUsername().length() != 0);

        assertTrue(profile.getRanking() != null);
        assertTrue(profile.getRanking().length() != 0);

        assertTrue(profile.getUserAvatar() != null);
        assertTrue(profile.getUserAvatar().length() != 0);

        assertTrue(profile.getRealName() != null);
        assertTrue(profile.getRealName().length() != 0);

        assertTrue(profile.getAboutMe() != null);
        assertTrue(profile.getAboutMe().length() != 0);
    }

    @Test
    void userListValid() {
        ArrayList<LeetcodeSubmission> userList = leetcodeClient.findSubmissionsByUsername("az2924");

        assertTrue(userList != null);

        assertNotNull(userList, "Expecting a non-zero list of submissions");
    }

    @Test
    void stressTestConcurrent() throws InterruptedException {
        int threadCount = 100;
        int requestsPerThread = 100;

        Thread[] threads = new Thread[threadCount];
        AtomicInteger tries = new AtomicInteger();
        AtomicInteger failures = new AtomicInteger();

        for (int t = 0; t < threadCount; t++) {
            threads[t] = new Thread(() -> {
                for (int i = 0; i < requestsPerThread; i++) {
                    try {
                        if (tries.get() % 100 == 0) {
                            System.out.println("tries (ongoing): " + tries.get());
                        }
                        tries.incrementAndGet();
                        List<LeetcodeSubmission> userList = leetcodeClient.findSubmissionsByUsername("az2924");
                        assertNotNull(userList);
                    } catch (Exception e) {
                        System.out.println("tries (failed): " + tries.get());
                        failures.incrementAndGet();
                    }
                }
            });
            threads[t].start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        if (failures.get() > 0) {
            fail("Failed to reach 5000 requests from leetcode client. Failures: " + failures.get());
        }
    }
}
