package com.patina.codebloom;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.patina.codebloom.common.leetcode.LeetcodeApiHandler;
import com.patina.codebloom.common.leetcode.models.LeetcodeDetailedQuestion;
import com.patina.codebloom.common.leetcode.models.LeetcodeQuestion;
import com.patina.codebloom.common.leetcode.models.LeetcodeSubmission;
import com.patina.codebloom.common.leetcode.models.POTD;
import com.patina.codebloom.common.leetcode.models.UserProfile;

@SpringBootTest
public class LeetcodeApiHandlerTest {
    private final LeetcodeApiHandler leetcodeApiHandler;

    @Autowired
    public LeetcodeApiHandlerTest(final LeetcodeApiHandler leetcodeApiHandler) {
        this.leetcodeApiHandler = leetcodeApiHandler;
    }

    @Test
    void questionSlugValid() {
        LeetcodeQuestion question = leetcodeApiHandler.findQuestionBySlug("trapping-rain-water");

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
        LeetcodeDetailedQuestion submission = leetcodeApiHandler.findSubmissionDetailBySubmissionId(1578438567);
        // idk how to get a valid sample submissionId from the leetcode question so test
        // is failing

        assertTrue(submission != null);

        assertTrue(submission.getRuntime() != 0);

        assertTrue(submission.getRuntimeDisplay() != null);
        assertTrue(submission.getRuntimeDisplay().length() != 0);

        assertTrue(submission.getRuntimePercentile() != 0.0f);

        assertTrue(submission.getMemory() != 0);

        assertTrue(submission.getMemoryDisplay() != null);
        assertTrue(submission.getMemoryDisplay().length() != 0);

        assertTrue(submission.getMemoryPercentile() != 0.0f);

        assertTrue(submission.getCode() != null);
        assertTrue(submission.getCode().length() != 0);

        assertTrue(submission.getLang() != null);
    }

    @Test
    void potdValid() {
        POTD potd = leetcodeApiHandler.getPotd();

        assertTrue(potd != null);

        assertTrue(potd.getTitle() != null);
        assertTrue(potd.getTitle().length() != 0);

        assertTrue(potd.getTitleSlug() != null);
        assertTrue(potd.getTitleSlug().length() != 0);

        assertTrue(potd.getDifficulty() != null);
    }

    @Test
    void userProfileValid() {
        UserProfile profile = leetcodeApiHandler.getUserProfile("az2924");

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
        ArrayList<LeetcodeSubmission> userList = leetcodeApiHandler.findSubmissionsByUsername("az2924");

        assertTrue(userList != null);

        assertNotNull(userList, "Expecting a non-zero list of submissions");
    }
}
