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

        questionBankRepository.createQuestion(testQuestionBank);
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

        QuestionBank updatedQuestionBank = QuestionBank.builder()
                .id(testQuestionBank.getId())
                .questionSlug(testQuestionBank.getQuestionSlug())
                .questionTitle("Updated Two Sum")
                .questionDifficulty(testQuestionBank.getQuestionDifficulty())
                .questionNumber(testQuestionBank.getQuestionNumber())
                .questionLink(testQuestionBank.getQuestionLink())
                .description(testQuestionBank.getDescription())
                .acceptanceRate(testQuestionBank.getAcceptanceRate())
                .build();

        boolean result = questionBankRepository.updateQuestion(updatedQuestionBank);

        if (!result) {
            fail("Failed to update question");
        }

        testQuestionBank = questionBankRepository.getQuestionById(testQuestionBank.getId());
        assertNotNull(testQuestionBank, "Updated question should not be null");
        assertEquals("Updated Two Sum", testQuestionBank.getQuestionTitle(), "Question title should be updated");
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

    @Test
    @Order(7)
    void testGetAllQuestions() {
        List<QuestionBank> questions = questionBankRepository.getAllQuestions();
        assertTrue(questions.size() > 0, "There should at least be one question retrieved");

        log.info("Successfully retrieved {} question(s)", questions.size());
    }
}
