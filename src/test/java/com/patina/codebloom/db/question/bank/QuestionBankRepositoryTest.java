package com.patina.codebloom.db.question.bank;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.patina.codebloom.common.db.models.question.QuestionDifficulty;
import com.patina.codebloom.common.db.models.question.bank.QuestionBank;
import com.patina.codebloom.common.db.models.question.topic.LeetcodeTopicEnum;
import com.patina.codebloom.common.db.repos.question.questionbank.QuestionBankRepository;
import com.patina.codebloom.db.BaseRepositoryTest;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
@Slf4j
public class QuestionBankRepositoryTest extends BaseRepositoryTest {
    private QuestionBankRepository questionBankRepository;
    private QuestionBank testQuestionBank;

    @Autowired
    public QuestionBankRepositoryTest(final QuestionBankRepository questionBankRepository) {
        this.questionBankRepository = questionBankRepository;
    }

    @BeforeAll
    void createQuestion() {

        testQuestionBank = QuestionBank.builder()
                        .questionSlug("two-sum")
                        .questionTitle("Two Sum")
                        .questionDifficulty(QuestionDifficulty.Easy)
                        .questionNumber(1)
                        .questionLink("https://leetcode.com/problems/two-sum/")
                        .description("Given an array of integers nums and an integer target, return indices of the two numbers such that they add up to target.")
                        .acceptanceRate(0.8f)
                        .build();

        testQuestionBank = questionBankRepository.createQuestion(testQuestionBank);
        assertNotNull(testQuestionBank.getId(), "Question Bank ID should be generated");
        assertNotNull(testQuestionBank.getCreatedAt(), "Question createdAt should be set");

        log.info("Created test question with ID: {}", testQuestionBank.getId());
    }

    @AfterAll
    void cleanUp() {
        if (testQuestionBank != null && testQuestionBank.getId() != null) {
            boolean isSuccessful = questionBankRepository.deleteQuestionById(testQuestionBank.getId());
            if (!isSuccessful) {
                fail("Failed to delete test question");
            }
        }
    }

    @Test
    @Order(1)
    void testGetQuestionById() {
        QuestionBank possibleTestQuestion = questionBankRepository.getQuestionById(testQuestionBank.getId());

        assertNotNull(possibleTestQuestion, "Retrieved question should not be null");
        assertEquals(testQuestionBank.getId(), possibleTestQuestion.getId(), "Question IDs should match");
        assertEquals(testQuestionBank.getQuestionSlug(), possibleTestQuestion.getQuestionSlug(), "Question slugs should match");
        assertEquals(testQuestionBank.getQuestionTitle(), possibleTestQuestion.getQuestionTitle(), "Question titles should match");

        log.info("Successfully found question by ID '{}'", testQuestionBank.getQuestionSlug());
    }

    @Test
    @Order(2)
    void testGetQuestionBySlug() {
        QuestionBank possibleTestQuestion = questionBankRepository.getQuestionBySlug(testQuestionBank.getQuestionSlug());

        assertNotNull(possibleTestQuestion, "Retrieved question should not be null");
        assertEquals(testQuestionBank.getId(), possibleTestQuestion.getId(), "Question IDs should match");
        assertEquals(testQuestionBank.getQuestionSlug(), possibleTestQuestion.getQuestionSlug(), "Question slugs should match");
        assertEquals(testQuestionBank.getQuestionTitle(), possibleTestQuestion.getQuestionTitle(), "Question titles should match");

        log.info("Successfully found question by slug '{}'", testQuestionBank.getQuestionSlug());
    }

    @Test
    @Order(3)
    void testUpdateQuestion() {
        String originalTitle = testQuestionBank.getQuestionTitle();

        testQuestionBank.setQuestionTitle("Updated Two Sum");
        
        QuestionBank updatedResult = questionBankRepository.updateQuestion(testQuestionBank);

        assertNotNull(updatedResult, "Question should be successfully updated");

        String questionId = testQuestionBank.getId();
        QuestionBank updatedQuestion = questionBankRepository.getQuestionById(questionId);
        assertNotNull(updatedQuestion, "Updated question should not be null");
        assertEquals("Updated Two Sum", updatedQuestion.getQuestionTitle(), "Question title should be updated");

        testQuestionBank.setQuestionTitle(originalTitle);
        questionBankRepository.updateQuestion(testQuestionBank);
        
        log.info("Successfully updated question with ID: {}", testQuestionBank.getId());
    }

    @Test
    @Order(4)
    void testGetRandomQuestion() {
        QuestionBank randomQuestion = questionBankRepository.getRandomQuestion();

        assertNotNull(randomQuestion, "Random question should not be null");
        assertNotNull(randomQuestion.getId(), "Random question ID should not be null");
        assertNotNull(randomQuestion.getQuestionSlug(), "Random question slug should not be null");
        assertNotNull(randomQuestion.getQuestionTitle(), "Random question title should not be null");

        log.info("Successfully retrieved random question: {}", randomQuestion.getQuestionTitle());
    }

    @Test
    @Order(5)
    void testGetQuestionsByTopic() {
        LeetcodeTopicEnum topic = LeetcodeTopicEnum.ARRAY;

        List<QuestionBank> questions = questionBankRepository.getQuestionsByTopic(topic);

        assertNotNull(questions, "Questions list should not be null");

        log.info("Successfully retrieved {} question(s) by topic: {}", questions.size(), topic.getLeetcodeEnum());
    }

    @Test
    @Order(6)
    void testGetQuestionsByDifficulty() {
        QuestionDifficulty difficulty = QuestionDifficulty.Easy;
        List<QuestionBank> questions = questionBankRepository.getQuestionsByDifficulty(difficulty);

        assertNotNull(questions, "Questions list should not be null");
        assertFalse(questions.isEmpty(), "Questions list should not be empty");

        boolean foundTestQuestion = false;
        for (QuestionBank question : questions) {
            if (question.getId().equals(testQuestionBank.getId())) {
                foundTestQuestion = true;
                assertEquals(testQuestionBank.getQuestionSlug(), question.getQuestionSlug(), "Question slugs should match");
                assertEquals(testQuestionBank.getQuestionTitle(), question.getQuestionTitle(), "Question titles should match");
                break;
            }
        }

        assertTrue(foundTestQuestion, "Test question should be found in the list of questions with EASY difficulty");
        log.info("Successfully retrieved {} question(s) by difficulty: EASY, including test question", questions.size());
    }
}
