package com.patina.codebloom.leetcode;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.patina.codebloom.common.leetcode.LeetcodeClient;
import com.patina.codebloom.common.leetcode.models.LeetcodeDetailedQuestion;
import com.patina.codebloom.common.leetcode.models.LeetcodeQuestion;
import com.patina.codebloom.common.leetcode.models.LeetcodeSubmission;
import com.patina.codebloom.common.leetcode.models.LeetcodeTopicTag;
import com.patina.codebloom.common.leetcode.models.POTD;
import com.patina.codebloom.common.leetcode.models.UserProfile;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
public class LeetcodeClientTest {
    private final LeetcodeClient leetcodeClient;

    @Autowired
    public LeetcodeClientTest(final LeetcodeClient leetcodeClient) {
        this.leetcodeClient = leetcodeClient;
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
        List<LeetcodeSubmission> userList = leetcodeClient.findSubmissionsByUsername("az2924");

        assertTrue(userList != null);

        assertNotNull(userList, "Expecting a non-zero list of submissions");
    }

    @Test
    void getAllTopicTags() {
        Set<LeetcodeTopicTag> topicTags = leetcodeClient.getAllTopicTags();

        // if this value is no longer true, make a new ticket on Notion to update the
        // enums stored in the database, THEN update this count.
        int expectedTagsCount = 72;
        assertEquals(expectedTagsCount, topicTags.size());

        System.out.println(topicTags);
    }

    // @Test
    // void getProblemDetails() {
    // log.info("heyyyyyyyy");
    // var x = leetcodeClient.findQuestionBySlug("trapping-rain-water");
    // log.info(x.toString());
    // System.out.println(x);
    // }
    //
}
