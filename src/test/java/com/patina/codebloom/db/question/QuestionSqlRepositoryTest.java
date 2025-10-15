package com.patina.codebloom.db.question;

import static org.junit.jupiter.api.Assertions.*;
import java.util.UUID;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.patina.codebloom.common.db.models.question.Question;
import com.patina.codebloom.common.db.models.question.QuestionDifficulty;
import com.patina.codebloom.common.db.models.question.QuestionWithUser;
import com.patina.codebloom.common.db.models.question.topic.LeetcodeTopicEnum;
import com.patina.codebloom.common.db.repos.question.QuestionRepository;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
@Slf4j
public class QuestionSqlRepositoryTest {
    private QuestionRepository questionRepository;
    private Question testQuestion;
    private String mockSuperUserId = "ed3bfe18-e42a-467f-b4fa-07e8da4d2555";

    @Autowired
    public QuestionSqlRepositoryTest(final QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    @BeforeAll
    void createQuestion() {

        testQuestion = Question.builder()
                        .userId(mockSuperUserId)
                        .questionSlug("test-question-slug-123")
                        .questionTitle("Two Sum Test")
                        .questionDifficulty(QuestionDifficulty.Easy)
                        .questionNumber(1)
                        .questionLink("https://leetcode.com/problems/two-sum/")
                        .description("Given an array of integers nums and an integer target, return indices of the two numbers such that they add up to target.")
                        .pointsAwarded(100)
                        .acceptanceRate(0.8f)
                        .submittedAt(java.time.LocalDateTime.now())
                        .runtime("3 ms")
                        .memory("14.2 MB")
                        .code("def twoSum(self, nums, target): # test code")
                        .language("python")
                        .submissionId("test-submission-123")
                        .build();

        testQuestion = questionRepository.createQuestion(testQuestion);
        assertNotNull(testQuestion.getId(), "Question ID should be generated");
        assertNotNull(testQuestion.getCreatedAt(), "Question createdAt should be set");

        log.info("Created test question with ID: {}", testQuestion.getId());
    }

    @AfterAll
    void cleanUp() {
        if (testQuestion != null && testQuestion.getId() != null) {
            boolean isSuccessful = questionRepository.deleteQuestionById(testQuestion.getId());
            if (!isSuccessful) {
                fail("Failed to delete test question");
            }
        }
    }

    @Test
    @Order(1)
    void testGetQuestionById() {
        Question possibleTestQuestion = questionRepository.getQuestionById(testQuestion.getId());

        assertNotNull(possibleTestQuestion, "Retrieved question should not be null");
        log.info("testQuestion: {}", testQuestion);
        log.info("possibleTestQuestion: {}", possibleTestQuestion);
        assertEquals(testQuestion.getId(), possibleTestQuestion.getId(), "Question IDs should match");
        assertEquals(testQuestion.getUserId(), possibleTestQuestion.getUserId(), "User IDs should match");
        assertEquals(testQuestion.getQuestionSlug(), possibleTestQuestion.getQuestionSlug(), "Question slugs should match");
        assertEquals(testQuestion.getQuestionTitle(), possibleTestQuestion.getQuestionTitle(), "Question titles should match");
    }

    @Test
    @Order(2)
    void testGetQuestionWithUserById() {
        QuestionWithUser questionWithUser = questionRepository.getQuestionWithUserById(testQuestion.getId());

        assertNotNull(questionWithUser, "QuestionWithUser should not be null");
        assertEquals(testQuestion.getId(), questionWithUser.getId(), "Question IDs should match");
        assertEquals(testQuestion.getUserId(), questionWithUser.getUserId(), "User IDs should match");

        assertNotNull(questionWithUser.getDiscordName(), "Discord name should be populated");
        assertNotNull(questionWithUser.getLeetcodeUsername(), "Leetcode username should be populated");
        assertNotNull(questionWithUser.getNickname(), "Nickname should be populated");

        log.info("Successfully retrieved question with user data for ID: {}", questionWithUser.getId());
    }

    @Test
    @Order(3)
    void testGetQuestionsByUserId() {
        ArrayList<Question> questions = questionRepository.getQuestionsByUserId(
                        testQuestion.getUserId(), 1, 20, "", false, new LeetcodeTopicEnum[]{});

        assertNotNull(questions, "Questions list should not be null");
        assertTrue(questions.size() > 0, "Questions list should contain at least one question");

        boolean foundTestQuestion = questions.stream()
                        .anyMatch(q -> q.getId().equals(testQuestion.getId()));
        assertTrue(foundTestQuestion, "Test question should be found in the user's questions");

        log.info("Successfully retrieved {} questions for user ID: {}", questions.size(), testQuestion.getUserId());
    }

    @Test
    @Order(4)
    void testUpdateQuestion() {
        String originalCode = testQuestion.getCode();
        String originalRuntime = testQuestion.getRuntime();

        testQuestion.setCode("def twoSum(self, nums, target): # updated test code");
        testQuestion.setRuntime("2 ms");

        Question updatedResult = questionRepository.updateQuestion(testQuestion);

        assertNotNull(updatedResult, "Question should be successfully updated");

        String questionId = testQuestion.getId();
        Question updatedQuestion = questionRepository.getQuestionById(questionId);
        assertNotNull(updatedQuestion, "Updated question should not be null");
        assertEquals("def twoSum(self, nums, target): # updated test code", updatedQuestion.getCode(), "Code should be updated");
        assertEquals("2 ms", updatedQuestion.getRuntime(), "Runtime should be updated");

        log.info("Successfully updated question with ID: {}", testQuestion.getId());

        testQuestion.setCode(originalCode);
        testQuestion.setRuntime(originalRuntime);
    }

    @Test
    @Order(5)
    void testGetQuestionBySlugAndUserId() {
        Question foundQuestion = questionRepository.getQuestionBySlugAndUserId(
                        testQuestion.getQuestionSlug(), testQuestion.getUserId());

        assertNotNull(foundQuestion, "Question should be found by slug and user ID");
        assertEquals(testQuestion.getId(), foundQuestion.getId(), "Question IDs should match");
        assertEquals(testQuestion.getQuestionSlug(), foundQuestion.getQuestionSlug(), "Question slugs should match");
        assertEquals(testQuestion.getUserId(), foundQuestion.getUserId(), "User IDs should match");

        log.info("Successfully found question by slug '{}' and user ID '{}'", testQuestion.getQuestionSlug(), testQuestion.getUserId());
    }

    @Test
    @Order(6)
    void testGetQuestionCountByUserId() {
        int count = questionRepository.getQuestionCountByUserId(testQuestion.getUserId(), "", false, Collections.emptySet());

        assertTrue(count > 0, "Question count should be greater than 0");

        log.info("Question count for user ID {}: {}", testQuestion.getUserId(), count);
    }

    @Test
    @Order(7)
    void testQuestionExistsBySubmissionId() {
        boolean exists = questionRepository.questionExistsBySubmissionId(testQuestion.getSubmissionId());

        assertTrue(exists, "Question should exist with the given submission ID");

        boolean notExists = questionRepository.questionExistsBySubmissionId("non-existent-submission-id");
        assertFalse(notExists, "Question should not exist with non-existent submission ID");

        log.info("Successfully verified question existence by submission ID: {}", testQuestion.getSubmissionId());
    }

    @Test
    @Order(8)
    void testGetQuestionsWithNoTopics() {
        List<Question> questions = questionRepository.getAllQuestionsWithNoTopics();

        assertNotNull(questions);
        assertTrue(questions.size() > 0);
    }
}
