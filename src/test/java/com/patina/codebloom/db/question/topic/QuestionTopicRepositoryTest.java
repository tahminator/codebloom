package com.patina.codebloom.db.question.topic;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.patina.codebloom.common.db.models.question.topic.LeetcodeTopicEnum;
import com.patina.codebloom.common.db.models.question.topic.QuestionTopic;
import com.patina.codebloom.common.db.repos.question.topic.QuestionTopicRepository;
import com.patina.codebloom.db.BaseRepositoryTest;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
@Slf4j
public class QuestionTopicRepositoryTest extends BaseRepositoryTest {
    private QuestionTopicRepository questionTopicRepository;
    private final String mockQuestionId = "c9857a8a-9d0b-4d2e-b73c-3af2425bdca6";

    private QuestionTopic testQuestionTopic;

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

        questionTopicRepository.createQuestionTopic(testQuestionTopic);
    }

    @AfterAll
    void cleanUp() {
        boolean isSuccessful = questionTopicRepository.deleteQuestionTopicById(testQuestionTopic.getId());
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
        QuestionTopic possibleQuestionTopic = questionTopicRepository.findQuestionTopicByQuestionIdAndTopicEnum(mockQuestionId, LeetcodeTopicEnum.ARRAY);

        assertNotNull(possibleQuestionTopic, "Retrieved question topic should not be null");

        if (!possibleQuestionTopic.equals(testQuestionTopic)) {
            log.info("possibleQuestionTopic: {}", possibleQuestionTopic);
            log.info("testQuestionTopic: {}", testQuestionTopic);
            fail("testFindQuestionTopicByQuestionIdAndTopicEnum failed: possibleQuestionTopic does not equal to testQuestionTopic");
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
}
