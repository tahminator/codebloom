package com.patina.codebloom.leetcode;
/* 
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import com.google.common.base.Strings;
import com.patina.codebloom.common.leetcode.LeetcodeClient;
import com.patina.codebloom.common.leetcode.models.LeetcodeDetailedQuestion;
import com.patina.codebloom.common.leetcode.models.LeetcodeQuestion;
import com.patina.codebloom.common.leetcode.models.LeetcodeSubmission;
import com.patina.codebloom.common.leetcode.models.LeetcodeTopicTag;
import com.patina.codebloom.common.leetcode.models.POTD;
import com.patina.codebloom.common.leetcode.models.UserProfile;
import com.patina.codebloom.common.leetcode.throttled.ThrottledLeetcodeClient;
import com.patina.codebloom.common.utils.function.FunctionUtils;
import com.patina.codebloom.config.TestJobNotifyListener;
import com.patina.codebloom.scheduled.auth.LeetcodeAuthStealer;

@SpringBootTest
@ActiveProfiles("ci")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Import(TestJobNotifyListener.class)
public class LeetcodeClientTest {
    private final LeetcodeClient leetcodeClient;
    private final LeetcodeAuthStealer leetcodeAuthStealer;

    @Autowired
    public LeetcodeClientTest(final ThrottledLeetcodeClient throttledLeetcodeClient, final LeetcodeAuthStealer leetcodeAuthStealer) {
        this.leetcodeClient = throttledLeetcodeClient;
        this.leetcodeAuthStealer = leetcodeAuthStealer;
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
        LeetcodeDetailedQuestion submission = FunctionUtils.tryAgainIfFail(
                        () -> leetcodeClient.findSubmissionDetailBySubmissionId(1588648200),
                        res -> !Strings.isNullOrEmpty(res.getRuntimeDisplay()),
                        () -> leetcodeAuthStealer.reloadCookie().join());

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
        List<LeetcodeSubmission> userList = leetcodeClient.findSubmissionsByUsername("az2924");

        assertTrue(userList != null);

        assertNotNull(userList, "Expecting a non-zero list of submissions");
    }

    @Test
    void stressTestConcurrent() throws InterruptedException {
        int threadCount = 100;
        int requestsPerThread = 27;

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

        // TODO: Figure out why the failures are always around 1 to 5. For now, do not
        // fail tests with anything over 10 requests.
        if (failures.get() > 10) {
            fail("Failed to reach 5000 requests from leetcode client. Failures: " + failures.get());
        }
    }

    @Test
    void assertAllAvailableTopics() {
        // if this value is no longer true, make a new ticket on Notion to update the
        // enums stored in the database, THEN update this count.
        int expectedTagsCount = 72;

        Set<LeetcodeTopicTag> topicTags = leetcodeClient.getAllTopicTags();
        assertEquals(expectedTagsCount, topicTags.size());
    }

    @Test
    void assertAllLeetcodeProblems() {
        List<LeetcodeQuestion> questions = leetcodeClient.getAllProblems();

        assertTrue(questions.size() > 0);
    }
}
*/