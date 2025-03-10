package com.patina.codebloom.website.leetcode.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;

import org.springframework.stereotype.Controller;

import com.patina.codebloom.website.auth.model.User;
import com.patina.codebloom.website.auth.repo.UserRepository;
import com.patina.codebloom.website.leaderboard.model.Leaderboard;
import com.patina.codebloom.website.leaderboard.model.UserWithScore;
import com.patina.codebloom.website.leaderboard.repo.LeaderboardRepository;
import com.patina.codebloom.website.leetcode.client.LeetcodeApiClient;
import com.patina.codebloom.website.leetcode.client.model.LeetcodeDetailedQuestion;
import com.patina.codebloom.website.leetcode.client.model.LeetcodeQuestion;
import com.patina.codebloom.website.leetcode.client.model.LeetcodeSubmission;
import com.patina.codebloom.website.leetcode.client.score.ScoreCalculator;
import com.patina.codebloom.website.leetcode.model.POTD;
import com.patina.codebloom.website.leetcode.model.Question;
import com.patina.codebloom.website.leetcode.model.QuestionDifficulty;
import com.patina.codebloom.website.leetcode.repo.POTDRepository;
import com.patina.codebloom.website.leetcode.repo.QuestionRepository;

/**
 * The submission logic is abstracted because it gets reused
 * in two different parts of the app: the submissions API
 * and the automated recurring task.
 */
@Controller
public class SubmissionService {
    private final QuestionRepository questionRepository;
    private final LeetcodeApiClient leetcodeApiClient;
    private final LeaderboardRepository leaderboardRepository;
    private final POTDRepository potdRepository;
    private final UserRepository userRepository;

    private boolean isSameDay(final LocalDateTime createdAt) {
        LocalDate createdAtDate = createdAt.toLocalDate();
        LocalDate today = LocalDate.now();

        return createdAtDate.equals(today);
    }

    public SubmissionService(final QuestionRepository questionRepository, final LeetcodeApiClient leetcodeApiClient,
                    final LeaderboardRepository leaderboardRepository,
                    final POTDRepository potdRepository, final UserRepository userRepository) {
        this.questionRepository = questionRepository;
        this.leetcodeApiClient = leetcodeApiClient;
        this.leaderboardRepository = leaderboardRepository;
        this.potdRepository = potdRepository;
        this.userRepository = userRepository;
    }

    public ArrayList<AcceptedSubmission> handleSubmissions(final ArrayList<LeetcodeSubmission> leetcodeSubmissions, final User user) {
        ArrayList<AcceptedSubmission> acceptedSubmissions = new ArrayList<>();

        for (LeetcodeSubmission leetcodeSubmission : leetcodeSubmissions) {
            if (!leetcodeSubmission.getStatusDisplay().equals("Accepted")) {
                continue;
            }

            Question question = questionRepository.getQuestionBySlugAndUserId(leetcodeSubmission.getTitleSlug(), user.getId());

            if (question != null) {
                continue;
            }

            float multiplier;

            POTD potd = potdRepository.getCurrentPOTD();

            if (potd == null || !isSameDay(potd.getCreatedAt())) {
                multiplier = 1.0f;
            } else {
                multiplier = potd.getMultiplier();
            }

            LeetcodeQuestion leetcodeQuestion = leetcodeApiClient.findQuestionBySlug(leetcodeSubmission.getTitleSlug());

            LeetcodeDetailedQuestion detailedQuestion = leetcodeApiClient.findSubmissionDetailBySubmissionId(leetcodeSubmission.getId());

            int points = ScoreCalculator.calculateScore(QuestionDifficulty.valueOf(leetcodeQuestion.getDifficulty()),
                            leetcodeQuestion.getAcceptanceRate(), multiplier);

            Question newQuestion = new Question(user.getId(), leetcodeQuestion.getTitleSlug(),
                            QuestionDifficulty.valueOf(leetcodeQuestion.getDifficulty()), leetcodeQuestion.getQuestionId(),
                            "https://leetcode.com/problems/" + leetcodeQuestion.getTitleSlug(), leetcodeQuestion.getQuestionTitle(),
                            leetcodeQuestion.getQuestion(), points,
                            leetcodeQuestion.getAcceptanceRate(), leetcodeSubmission.getTimestamp(), detailedQuestion.getRuntimeDisplay(),
                            detailedQuestion.getMemoryDisplay(), detailedQuestion.getCode(),
                            detailedQuestion.getLang().getName());

            questionRepository.createQuestion(newQuestion);

            acceptedSubmissions.add(new AcceptedSubmission(leetcodeQuestion.getQuestionTitle(), points));

            // TODO - Update the points given on the current
            // leaderboard.
            Leaderboard recentLeaderboard = leaderboardRepository.getRecentLeaderboardShallow();

            // This should never be happening as there should always be
            // an existing
            // leaderboard to fall on. Howerver, race conditions could
            // trigger this problem.
            if (recentLeaderboard == null) {
                throw new RuntimeException("No recent leaderboard found.");
            }

            UserWithScore recentUserMetadata = leaderboardRepository.getUserFromLeaderboard(recentLeaderboard.getId(), user.getId());

            leaderboardRepository.updateUserPointsFromLeaderboard(recentLeaderboard.getId(), user.getId(),
                            recentUserMetadata.getTotalScore() + points);
        }

        return acceptedSubmissions;
    }

    // Uncomment this if you need to run the script. The 5
    // second delay allows the
    // LeetcodeAuthStealer to run it's method first.
    // @Scheduled(initialDelay = 5000, fixedDelay = 1000000)
    public void updateSubmissions() {
        System.out.println("Migration script activated. DO NOT LEAVE THIS ON IN PRODUCTION.");
        List<User> users = userRepository.getAllUsers();
        for (User user : users) {
            System.out.println("Starting migration for user ID " + user.getId());
            ArrayList<LeetcodeSubmission> leetcodeSubmissions = leetcodeApiClient.findSubmissionsByUsername(user.getLeetcodeUsername());

            for (LeetcodeSubmission leetcodeSubmission : leetcodeSubmissions) {
                if (!leetcodeSubmission.getStatusDisplay().equals("Accepted")) {
                    continue;
                }

                Question question = questionRepository.getQuestionBySlugAndUserId(leetcodeSubmission.getTitleSlug(), user.getId());

                if (question == null || question.getCode() != null) {
                    continue;
                }

                LeetcodeDetailedQuestion detailedQuestion = leetcodeApiClient
                                .findSubmissionDetailBySubmissionId(leetcodeSubmission.getId());

                if (detailedQuestion == null) {
                    continue;
                }

                System.out.println("Attempting to update User ID" + user.getId() + " with question of " + question.getQuestionSlug());

                Question newQuestion = new Question(question.getId(), question.getUserId(), question.getQuestionSlug(),
                                question.getQuestionDifficulty(), question.getQuestionNumber(),
                                question.getQuestionLink(), question.getPointsAwarded(), question.getQuestionTitle(),
                                question.getDescription(), question.getAcceptanceRate(), question.getCreatedAt(),
                                question.getSubmittedAt(), detailedQuestion.getRuntimeDisplay(), detailedQuestion.getMemoryDisplay(),
                                detailedQuestion.getCode(), detailedQuestion.getLang().getName());

                questionRepository.updateQuestion(newQuestion);
            }
        }

        System.out.println("Exiting migration script.");
    }
}
