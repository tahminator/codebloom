package com.patina.codebloom.scheduled.leetcode;

import com.patina.codebloom.common.db.models.question.QuestionDifficulty;
import com.patina.codebloom.common.db.models.question.bank.QuestionBank;
import com.patina.codebloom.common.db.models.task.BackgroundTask;
import com.patina.codebloom.common.db.models.task.BackgroundTaskEnum;
import com.patina.codebloom.common.db.repos.question.questionbank.QuestionBankRepository;
import com.patina.codebloom.common.db.repos.task.BackgroundTaskRepository;
import com.patina.codebloom.common.leetcode.LeetcodeClient;
import com.patina.codebloom.common.leetcode.models.LeetcodeQuestion;
import com.patina.codebloom.common.time.StandardizedOffsetDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("!ci")
public class FetchAllLeetcodeQuestions {

    private final BackgroundTaskRepository backgroundTaskRepository;
    private final LeetcodeClient leetcodeClient;
    private final QuestionBankRepository questionBankRepository;

    public FetchAllLeetcodeQuestions(
        final BackgroundTaskRepository backgroundTaskRepository,
        final LeetcodeClient leetcodeClient,
        final QuestionBankRepository questionBankRepository
    ) {
        this.backgroundTaskRepository = backgroundTaskRepository;
        this.leetcodeClient = leetcodeClient;
        this.questionBankRepository = questionBankRepository;
    }

    @Scheduled(initialDelay = 1, fixedDelay = 3, timeUnit = TimeUnit.HOURS)
    public void updateQuestionBank() {
        BackgroundTask recentLeetcodeTask =
            backgroundTaskRepository.getMostRecentlyCompletedBackgroundTaskByTaskEnum(
                BackgroundTaskEnum.LEETCODE_QUESTION_BANK
            );
        if (recentLeetcodeTask != null) {
            if (
                StandardizedOffsetDateTime.now().isBefore(
                    recentLeetcodeTask.getCompletedAt().plusHours(16)
                )
            ) {
                log.error("Not time yet to resync question bank");
                return;
            }
        }

        List<LeetcodeQuestion> leetcodeQuestions =
            leetcodeClient.getAllProblems();

        List<QuestionBank> bankLeetcodeQuestion = new ArrayList<>();
        for (LeetcodeQuestion question : leetcodeQuestions) {
            QuestionDifficulty difficulty;

            switch (question.getDifficulty().toUpperCase()) {
                case "EASY":
                    difficulty = QuestionDifficulty.Easy;
                    break;
                case "MEDIUM":
                    difficulty = QuestionDifficulty.Medium;
                    break;
                case "HARD":
                    difficulty = QuestionDifficulty.Hard;
                    break;
                default:
                    throw new IllegalArgumentException(
                        "Unknown difficulty: " + question.getDifficulty()
                    );
            }

            QuestionBank bankQuestion = QuestionBank.builder()
                .questionSlug(question.getTitleSlug())
                .questionDifficulty(difficulty)
                .questionTitle(question.getQuestionTitle())
                .questionNumber(question.getQuestionId())
                .questionLink(question.getLink())
                .acceptanceRate(question.getAcceptanceRate())
                .createdAt(StandardizedOffsetDateTime.now())
                .build();

            bankLeetcodeQuestion.add(bankQuestion);
        }

        List<QuestionBank> bankQuestion =
            questionBankRepository.getAllQuestions();

        Set<String> slugsFromLeetcode = bankLeetcodeQuestion
            .stream()
            .map(QuestionBank::getQuestionSlug)
            .collect(Collectors.toSet());

        Set<String> slugsFromDb = bankQuestion
            .stream()
            .map(QuestionBank::getQuestionSlug)
            .collect(Collectors.toSet());

        List<QuestionBank> missingInDb = bankLeetcodeQuestion
            .stream()
            .filter(q -> !slugsFromDb.contains(q.getQuestionSlug()))
            .collect(Collectors.toList());

        for (QuestionBank question : missingInDb) {
            questionBankRepository.createQuestion(question);
        }

        List<QuestionBank> deletedFromLeetcode = bankQuestion
            .stream()
            .filter(q -> !slugsFromLeetcode.contains(q.getQuestionSlug()))
            .collect(Collectors.toList());

        for (QuestionBank question : deletedFromLeetcode) {
            questionBankRepository.deleteQuestionById(question.getId());
        }

        backgroundTaskRepository.createBackgroundTask(
            BackgroundTask.builder()
                .task(BackgroundTaskEnum.LEETCODE_QUESTION_BANK)
                .completedAt(OffsetDateTime.now())
                .build()
        );
    }
}
