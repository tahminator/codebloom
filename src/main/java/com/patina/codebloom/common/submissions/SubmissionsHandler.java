package com.patina.codebloom.common.submissions;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;

import com.patina.codebloom.common.db.models.leaderboard.Leaderboard;
import com.patina.codebloom.common.db.models.potd.POTD;
import com.patina.codebloom.common.db.models.question.Question;
import com.patina.codebloom.common.db.models.question.QuestionDifficulty;
import com.patina.codebloom.common.db.models.user.User;
import com.patina.codebloom.common.db.models.user.UserWithScore;
import com.patina.codebloom.common.db.repos.leaderboard.LeaderboardRepository;
import com.patina.codebloom.common.db.repos.potd.POTDRepository;
import com.patina.codebloom.common.db.repos.question.QuestionRepository;
import com.patina.codebloom.common.db.repos.user.UserRepository;
import com.patina.codebloom.common.leetcode.LeetcodeApiHandler;
import com.patina.codebloom.common.leetcode.models.LeetcodeDetailedQuestion;
import com.patina.codebloom.common.leetcode.models.LeetcodeQuestion;
import com.patina.codebloom.common.leetcode.models.LeetcodeSubmission;
import com.patina.codebloom.common.leetcode.score.ScoreCalculator;
import com.patina.codebloom.common.submissions.object.AcceptedSubmission;

/**
 * The submission logic is abstracted because it gets reused in two different
 * parts of the app: the submissions API and the automated recurring task.
 */
@Controller
public class SubmissionsHandler {
    private final QuestionRepository questionRepository;
    private final LeetcodeApiHandler leetcodeApiHandler;
    private final LeaderboardRepository leaderboardRepository;
    private final POTDRepository potdRepository;
    private final UserRepository userRepository;

    private boolean isSameDay(final LocalDateTime createdAt) {
        LocalDate createdAtDate = createdAt.toLocalDate();
        LocalDate today = LocalDate.now();

        return createdAtDate.equals(today);
    }

    public SubmissionsHandler(final QuestionRepository questionRepository, final LeetcodeApiHandler leetcodeApiHandler, final LeaderboardRepository leaderboardRepository,
                    final POTDRepository potdRepository, final UserRepository userRepository) {
        this.questionRepository = questionRepository;
        this.leetcodeApiHandler = leetcodeApiHandler;
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
            if (questionRepository.questionExistsBySubmissionId(String.valueOf(leetcodeSubmission.getId()))) {
                continue;
            }

            float multiplier;

            POTD potd = potdRepository.getCurrentPOTD();

            if (potd == null || !isSameDay(potd.getCreatedAt())) {
                multiplier = 1.0f;
            } else {
                multiplier = potd.getMultiplier();
            }

            LeetcodeQuestion leetcodeQuestion = leetcodeApiHandler.findQuestionBySlug(leetcodeSubmission.getTitleSlug());

            LeetcodeDetailedQuestion detailedQuestion = leetcodeApiHandler.findSubmissionDetailBySubmissionId(leetcodeSubmission.getId());

            // If the submission is before the leaderboard started, points awarded = 0
            Leaderboard recentLeaderboard = leaderboardRepository.getRecentLeaderboardMetadata();

            // This should never be happening as there should always be an existing
            // leaderboard to fall on. Howerver, race conditions could trigger this problem.
            if (recentLeaderboard == null) {
                throw new RuntimeException("No recent leaderboard found.");
            }

            boolean isTooLate = recentLeaderboard.getCreatedAt().isAfter(leetcodeSubmission.getTimestamp());

            int points;
            if (question != null || isTooLate) {
                points = 0;
            } else {
                points = ScoreCalculator.calculateScore(QuestionDifficulty.valueOf(leetcodeQuestion.getDifficulty()), leetcodeQuestion.getAcceptanceRate(), multiplier);
            }

            Question newQuestion = new Question(user.getId(), leetcodeQuestion.getTitleSlug(), QuestionDifficulty.valueOf(leetcodeQuestion.getDifficulty()), leetcodeQuestion.getQuestionId(),
                            "https://leetcode.com/problems/" + leetcodeQuestion.getTitleSlug(), leetcodeQuestion.getQuestionTitle(), leetcodeQuestion.getQuestion(), points,
                            leetcodeQuestion.getAcceptanceRate(), leetcodeSubmission.getTimestamp(), detailedQuestion.getRuntimeDisplay(), detailedQuestion.getMemoryDisplay(),
                            detailedQuestion.getCode(),
                            detailedQuestion.getLang().getName(), String.valueOf(leetcodeSubmission.getId()));

            questionRepository.createQuestion(newQuestion);

            acceptedSubmissions.add(new AcceptedSubmission(leetcodeQuestion.getQuestionTitle(), points));

            UserWithScore recentUserMetadata = userRepository.getUserWithScoreById(user.getId(), recentLeaderboard.getId());

            leaderboardRepository.updateUserPointsFromLeaderboard(recentLeaderboard.getId(), user.getId(), recentUserMetadata.getTotalScore() + points);
        }

        return acceptedSubmissions;
    }

    // Uncomment this if you need to run the script. The 5 second delay allows the
    // LeetcodeAuthStealer to run it's method first.
    // @Scheduled(initialDelay = 5000, fixedDelay = 1000000)
    public void updateSubmissions() {
        System.out.println("Migration script activated. DO NOT LEAVE THIS ON IN PRODUCTION.");
        List<User> users = userRepository.getAllUsers(0, 0, null);
        for (User user : users) {
            System.out.println("Starting migration for user ID " + user.getId());
            ArrayList<LeetcodeSubmission> leetcodeSubmissions = leetcodeApiHandler.findSubmissionsByUsername(user.getLeetcodeUsername());

            for (LeetcodeSubmission leetcodeSubmission : leetcodeSubmissions) {
                if (!leetcodeSubmission.getStatusDisplay().equals("Accepted")) {
                    continue;
                }

                Question question = questionRepository.getQuestionBySlugAndUserId(leetcodeSubmission.getTitleSlug(), user.getId());

                if (question == null || question.getCode() != null) {
                    continue;
                }

                LeetcodeDetailedQuestion detailedQuestion = leetcodeApiHandler.findSubmissionDetailBySubmissionId(leetcodeSubmission.getId());

                if (detailedQuestion == null) {
                    continue;
                }

                System.out.println("Attempting to update User ID" + user.getId() + " with question of " + question.getQuestionSlug());

                Question newQuestion = new Question(question.getId(), question.getUserId(), question.getQuestionSlug(), question.getQuestionDifficulty(), question.getQuestionNumber(),
                                question.getQuestionLink(), question.getPointsAwarded(), question.getQuestionTitle(), question.getDescription(), question.getAcceptanceRate(), question.getCreatedAt(),
                                question.getSubmittedAt(), detailedQuestion.getRuntimeDisplay(), detailedQuestion.getMemoryDisplay(), detailedQuestion.getCode(), detailedQuestion.getLang().getName(),
                                String.valueOf(leetcodeSubmission.getId()));

                questionRepository.updateQuestion(newQuestion);
            }
        }

        System.out.println("Exiting migration script.");
    }
}
