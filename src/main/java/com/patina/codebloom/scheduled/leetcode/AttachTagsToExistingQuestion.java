package com.patina.codebloom.scheduled.leetcode;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.patina.codebloom.common.db.models.question.Question;
import com.patina.codebloom.common.db.models.question.topic.LeetcodeTopicEnum;
import com.patina.codebloom.common.db.models.question.topic.QuestionTopic;
import com.patina.codebloom.common.db.repos.question.QuestionRepository;
import com.patina.codebloom.common.db.repos.question.topic.QuestionTopicRepository;
import com.patina.codebloom.leetcode.client.LeetcodeClient;
import com.patina.codebloom.leetcode.client.throttled.ThrottledLeetcodeClient;
import com.patina.codebloom.leetcode.client.models.LeetcodeQuestion;

import lombok.extern.slf4j.Slf4j;

@Component
@Profile("!ci")
@Slf4j
public class AttachTagsToExistingQuestion {
    private final QuestionRepository questionRepository;
    private final QuestionTopicRepository questionTopicRepository;
    private final LeetcodeClient leetcodeClient;

    public AttachTagsToExistingQuestion(final QuestionRepository questionRepository,
                    final QuestionTopicRepository questionTopicRepository,
                    final ThrottledLeetcodeClient throttledLeetcodeClient) {
        this.questionRepository = questionRepository;
        this.questionTopicRepository = questionTopicRepository;
        this.leetcodeClient = throttledLeetcodeClient;
    }

    @Scheduled(initialDelay = 0, fixedDelay = 120, timeUnit = TimeUnit.MINUTES)
    void attachTagsToExistingQuestions() {
        log.info("Attempting to attach tags to existing questions that are missing any...");
        List<Question> questions = questionRepository.getAllQuestionsWithNoTopics();

        if (questions.size() == 0) {
            log.info("No questions with topics. This task is complete.");
            return;
        }

        for (var question : questions) {
            log.info("Updating question with id of {}", question.getId());
            LeetcodeQuestion leetcodeQuestion;
            try {
                leetcodeQuestion = leetcodeClient.findQuestionBySlug(question.getQuestionSlug());
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }

            for (var leetcodeTopicTag : leetcodeQuestion.getTopics()) {
                String leetcodeTopicTagSlug = leetcodeTopicTag.getSlug();

                var newQuestionTopic = QuestionTopic.builder()
                                .questionId(question.getId())
                                .topicSlug(leetcodeTopicTagSlug)
                                .topic(LeetcodeTopicEnum.fromValue(leetcodeTopicTagSlug))
                                .build();

                questionTopicRepository.createQuestionTopic(newQuestionTopic);
            }
        }

        log.info("This task is complete.");
    }
}
