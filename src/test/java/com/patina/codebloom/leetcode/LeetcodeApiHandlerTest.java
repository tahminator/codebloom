package com.patina.codebloom.leetcode;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.patina.codebloom.common.leetcode.LeetcodeApiHandler;
import com.patina.codebloom.common.leetcode.models.LeetcodeDetailedQuestion;
import com.patina.codebloom.common.leetcode.models.LeetcodeQuestion;
import com.patina.codebloom.common.leetcode.models.LeetcodeSubmission;
import com.patina.codebloom.common.leetcode.models.POTD;
import com.patina.codebloom.common.leetcode.models.UserProfile;

import io.restassured.RestAssured;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LeetcodeApiHandlerTest {
    private final LeetcodeApiHandler leetcodeApiHandler;

    @Autowired
    public LeetcodeApiHandlerTest(final LeetcodeApiHandler leetcodeApiHandler) {
        this.leetcodeApiHandler = leetcodeApiHandler;
    }

    // We have to do this because
    // RestAssured is trying to use a port value
    // that is not valid for connecting to leetcode.com.
    //
    // This happens only because we override the port
    // in tests where we need to actually run the Spring
    // Boot server and run tests on specific endpoints.
    //
    // TODO - This should be fixed once Alfardil replaces
    // RestAssured with the default HttpClient
    @BeforeAll
    void hackyFixForBadPort() {
        RestAssured.port = 443;
    }

    @AfterAll
    void revertHackyFix() {
        RestAssured.port = RestAssured.DEFAULT_PORT;
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
        LeetcodeDetailedQuestion submission = leetcodeApiHandler.findSubmissionDetailBySubmissionId(1588648200);
        System.out.println(submission.getRuntime());
        System.out.println(submission.getRuntimeDisplay());
        System.out.println(submission.getRuntimePercentile());
        System.out.println(submission.getMemory());
        System.out.println(submission.getMemoryDisplay());
        System.out.println(submission.getMemoryPercentile());
        System.out.println(submission.getCode());
        System.out.println(submission.getLang().getName());
        System.out.println(submission.getLang().getVerboseName());
        System.out.println(submission.getLang());

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
