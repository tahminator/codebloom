package com.patina.codebloom.scheduled.leetcode;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.patina.codebloom.common.db.models.question.Question;
import com.patina.codebloom.common.db.repos.question.QuestionRepository;
import com.patina.codebloom.common.leetcode.models.LeetcodeDetailedQuestion;
import com.patina.codebloom.common.leetcode.DefaultLeetcodeApiHandler;

@Component
public class RefetchIncompleteQuestions {
    private static final Logger LOGGER = LoggerFactory.getLogger(RefetchIncompleteQuestions.class);

    private final QuestionRepository questionRepository;
    private final DefaultLeetcodeApiHandler defaultLeetcodeApiHandler;

    public RefetchIncompleteQuestions(final QuestionRepository questionRepository, final DefaultLeetcodeApiHandler defaultLeetcodeApiHandler) {
        this.questionRepository = questionRepository;
        this.defaultLeetcodeApiHandler = defaultLeetcodeApiHandler;

    }

    @Scheduled(initialDelay = 0, fixedDelay = 12, timeUnit = TimeUnit.HOURS)
    public void reFetchIncompleteQuestions() {
        ArrayList<Question> questions = questionRepository.getAllIncompleteQuestions();

        if (questions.isEmpty()) {
            LOGGER.info("All questions have complete information.");
            return;
        }

        for (Question q : questions) {
            try {
                int submissionId = Integer.parseInt(q.getSubmissionId());
                LeetcodeDetailedQuestion matchingQuestionWithDetails = defaultLeetcodeApiHandler.findSubmissionDetailBySubmissionId(submissionId);

                Question updated = Question.builder()
                                .id(q.getId())
                                .userId(q.getUserId())
                                .questionSlug(q.getQuestionSlug())
                                .questionTitle(q.getQuestionTitle())
                                .questionDifficulty(q.getQuestionDifficulty())
                                .questionNumber(q.getQuestionNumber())
                                .questionLink(q.getQuestionLink())
                                .description(q.getDescription())
                                .pointsAwarded(q.getPointsAwarded())
                                .acceptanceRate(q.getAcceptanceRate())
                                .createdAt(q.getCreatedAt())
                                .submittedAt(q.getSubmittedAt())
                                .runtime(Integer.toString(matchingQuestionWithDetails.getRuntime()))
                                .memory(Integer.toString(matchingQuestionWithDetails.getMemory()))
                                .code(matchingQuestionWithDetails.getCode())
                                .language(matchingQuestionWithDetails.getLang().getName())
                                .submissionId(q.getSubmissionId())
                                .build();

                questionRepository.updateQuestion(updated);
                LOGGER.info("Successfully updated question of id: " + submissionId);
            } catch (Exception e) {
                LOGGER.error("Failed to refetch question: " + q.getQuestionSlug(), e);
            }
        }
    }
}
