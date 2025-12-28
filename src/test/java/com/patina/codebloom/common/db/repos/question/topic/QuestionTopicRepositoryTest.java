package com.patina.codebloom.common.db.repos.question.topic;

import static org.junit.jupiter.api.Assertions.*;

import com.patina.codebloom.common.db.models.question.topic.LeetcodeTopicEnum;
import com.patina.codebloom.common.db.models.question.topic.QuestionTopic;
import com.patina.codebloom.common.db.repos.BaseRepositoryTest;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
@Slf4j
public class QuestionTopicRepositoryTest extends BaseRepositoryTest {

    private QuestionTopicRepository questionTopicRepository;
    private final String mockQuestionId = "c9857a8a-9d0b-4d2e-b73c-3af2425bdca6";
    private final String mockQuestionBankId = "165dd000-c310-11f0-8d3a-461b1b1abee8";

    private QuestionTopic testQuestionTopic;
    private QuestionTopic testQuestionBankTopic;

    @Autowired
    public QuestionTopicRepositoryTest(final QuestionTopicRepository questionTopicRepository) {
        this.questionTopicRepository = questionTopicRepository;
    }

    @BeforeAll
    void createQuestion() {
        testQuestionTopic = QuestionTopic.builder()
                .questionId(mockQuestionId)
                .topic(LeetcodeTopicEnum.ARRAY)
                .topicSlug("array")
                .build();

        testQuestionBankTopic = QuestionTopic.builder()
                .questionBankId(mockQuestionBankId)
                .topic(LeetcodeTopicEnum.ARRAY)
                .topicSlug("array")
                .build();

        questionTopicRepository.createQuestionTopic(testQuestionTopic);
        questionTopicRepository.createQuestionTopic(testQuestionBankTopic);
    }

    @AfterAll
    void cleanUp() {
        boolean isSuccessful = questionTopicRepository.deleteQuestionTopicById(testQuestionTopic.getId())
                && questionTopicRepository.deleteQuestionTopicById(testQuestionBankTopic.getId());
        if (!isSuccessful) {
            fail("Failed to delete test question");
        }
    }

    @Test
    @Order(1)
    void testFindQuestionTopicById() {
        QuestionTopic possibleQuestionTopic = questionTopicRepository.findQuestionTopicById(testQuestionTopic.getId());

        assertNotNull(possibleQuestionTopic, "Retrieved question topic should not be null");

        if (!possibleQuestionTopic.equals(testQuestionTopic)) {
            log.info("possibleQuestionTopic: {}", possibleQuestionTopic);
            log.info("testQuestionTopic: {}", testQuestionTopic);
            fail("testFindQuestionTopicById failed: possibleQuestionTopic does not equal to testQuestionTopic");
        }
    }

    @Test
    @Order(2)
    void testFindQuestionTopicByQuestionIdAndTopicEnum() {
        QuestionTopic possibleQuestionTopic = questionTopicRepository.findQuestionTopicByQuestionIdAndTopicEnum(
                mockQuestionId, LeetcodeTopicEnum.ARRAY);

        assertNotNull(possibleQuestionTopic, "Retrieved question topic should not be null");

        if (!possibleQuestionTopic.equals(testQuestionTopic)) {
            log.info("possibleQuestionTopic: {}", possibleQuestionTopic);
            log.info("testQuestionTopic: {}", testQuestionTopic);
            fail(
                    "testFindQuestionTopicByQuestionIdAndTopicEnum failed: possibleQuestionTopic does not equal to testQuestionTopic");
        }
    }

    @Test
    @Order(3)
    void testFindQuestionTopicsByQuestionId() {
        List<QuestionTopic> questionTopics = questionTopicRepository.findQuestionTopicsByQuestionId(mockQuestionId);

        assertNotNull(questionTopics, "Retrieved question topic list should not be null");

        if (!questionTopics.contains(testQuestionTopic)) {
            log.info("questionTopics: {}", questionTopics);
            log.info("testQuestionTopic: {}", testQuestionTopic);
            fail("testFindQuestionTopicsByQuestionId failed: testQuestionTopic is not in questionTopics");
        }
    }

    @Test
    @Order(4)
    void testUpdateQuestion() {
        testQuestionTopic.setTopic(LeetcodeTopicEnum.BACKTRACKING);
        testQuestionTopic.setTopicSlug("backtracking");

        boolean isSuccessful = questionTopicRepository.updateQuestionTopicById(testQuestionTopic);

        if (!isSuccessful) {
            fail("testUpdateQuestion failed: Failed to update question");
        }

        QuestionTopic newQuestionTopic = questionTopicRepository.findQuestionTopicById(testQuestionTopic.getId());

        assertNotNull(newQuestionTopic, "Retrieved question topic should not be null");

        if (!newQuestionTopic.equals(testQuestionTopic)) {
            log.info("newQuestionTopic: {}", newQuestionTopic);
            log.info("testQuestionTopic: {}", testQuestionTopic);
            fail("testFindQuestionTopicById failed: newQuestionTopic does not equal to testQuestionTopic");
        }
    }

    @Test
    @Order(5)
    void testFindQuestionTopicsByQuestionBankId() {
        List<QuestionTopic> questionBankTopics =
                questionTopicRepository.findQuestionTopicsByQuestionBankId(mockQuestionBankId);

        assertNotNull(questionBankTopics, "Retrieved question topic list should not be null");

        if (!questionBankTopics.contains(testQuestionBankTopic)) {
            log.info("questionBankTopics: {}", questionBankTopics);
            log.info("testQuestionBankTopic: {}", testQuestionBankTopic);
            fail("testFindQuestionTopicsByQuestionBankId failed: testQuestionBankTopic is not in questionTopics");
        }
    }
}
