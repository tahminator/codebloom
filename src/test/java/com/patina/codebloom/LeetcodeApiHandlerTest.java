package com.patina.codebloom;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import com.patina.codebloom.common.db.models.question.QuestionDifficulty;
import static com.patina.codebloom.common.db.models.question.QuestionDifficulty.Hard;
import com.patina.codebloom.common.leetcode.models.Lang;
import com.patina.codebloom.common.leetcode.models.LeetcodeDetailedQuestion;
import com.patina.codebloom.common.leetcode.models.LeetcodeQuestion;
import com.patina.codebloom.common.leetcode.models.LeetcodeSubmission;
import com.patina.codebloom.common.leetcode.models.POTD;
import com.patina.codebloom.common.leetcode.models.UserProfile;

class LeetcodeApiHandlerTest {
    @Test
    void userSubmission() {
        List<LeetcodeSubmission> submissionsList = new ArrayList<>();
        LeetcodeSubmission submission1 = new LeetcodeSubmission(
            1,
            "Trapping Rain Water",
            "trapping-rain-water",
            LocalDateTime.now(),
            "Accepted"
        );

        submissionsList.add(submission1);
        assertEquals(1, submissionsList.size());
    }

    @Test
    void submissionID() {
        int runtime = 5;
        String runtimeDisplay = "O(1)";
        float runtimePercentile = 50.0f;
        int memory = 1;
        String memoryDisplay = "Fully displayed.";
        float memoryPercentile = 50.0f;
        String code = "return water";
        Lang lang = new Lang("java", "Java");

        LeetcodeDetailedQuestion submission = new LeetcodeDetailedQuestion(runtime, runtimeDisplay, runtimePercentile, memory, memoryDisplay, memoryPercentile, code, lang);

        assertEquals(runtime, submission.getRuntime());
        assertEquals(runtimeDisplay, submission.getRuntimeDisplay());
        assertEquals(runtimePercentile, submission.getRuntimePercentile());
        assertEquals(memory, submission.getMemory());
        assertEquals(memoryDisplay, submission.getMemoryDisplay());
        assertEquals(memoryPercentile, submission.getMemoryPercentile());
        assertEquals(code, submission.getCode());
        assertEquals(lang, submission.getLang());
    }

    @Test
    void questionSlug() {
        String link = "https://leetcode.com/problems/trapping-rain-water/description/";
        int questionId = 423;
        String questionTitle = "Trapping Rain Water";
        String titleSlug = "trapping-rain-water";
        String difficulty = "Hard";
        String question = "Given n non-negative integers representing an elevation map where the width of each bar is 1, compute how much water it can trap after raining.";
        float acceptanceRate = 64.0f;

        LeetcodeQuestion questionDetails = new LeetcodeQuestion(link, questionId, questionTitle, titleSlug, difficulty, question, acceptanceRate);
        assertEquals(link, questionDetails.getLink());
        assertEquals(questionId, questionDetails.getQuestionId());
        assertEquals(questionTitle, questionDetails.getQuestionTitle());
        assertEquals(titleSlug, questionDetails.getTitleSlug());
        assertEquals(difficulty, questionDetails.getDifficulty());
        assertEquals(question, questionDetails.getQuestion());
        assertEquals(acceptanceRate, questionDetails.getAcceptanceRate());
    }

    @Test
    void potd() {
        String title = "Trapping Rain Water";
        String titleSlug = "trapping-rain-water";
        QuestionDifficulty difficulty = Hard;

        POTD potdDetails = new POTD(title, titleSlug, difficulty);

        assertEquals(title, potdDetails.getTitle());
        assertEquals(titleSlug, potdDetails.getTitleSlug());
        assertEquals(difficulty, potdDetails.getDifficulty());
    }

    @Test
    void userProfileData() {
        String username = "tahminator";
        String ranking = "The Best";
        String userAvatar = "userAvatar.png";
        String realName = "Tahmid Ahmed";
        String aboutMe = "I love Leetcode.";

        UserProfile userProfile = new UserProfile(username, ranking, userAvatar, realName, aboutMe);

        assertEquals(username, userProfile.getUsername());
        assertEquals(ranking, userProfile.getRanking());
        assertEquals(userAvatar, userProfile.getUserAvatar());
        assertEquals(realName, userProfile.getRealName());
        assertEquals(aboutMe, userProfile.getAboutMe());
    }
}
