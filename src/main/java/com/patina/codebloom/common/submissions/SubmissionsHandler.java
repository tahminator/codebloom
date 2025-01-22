package com.patina.codebloom.common.submissions;

import java.util.ArrayList;
import java.util.OptionalInt;

import org.springframework.stereotype.Controller;

import com.patina.codebloom.common.db.models.leaderboard.Leaderboard;
import com.patina.codebloom.common.db.models.question.Question;
import com.patina.codebloom.common.db.models.question.QuestionDifficulty;
import com.patina.codebloom.common.db.models.user.User;
import com.patina.codebloom.common.db.models.user.UserWithScore;
import com.patina.codebloom.common.db.repos.leaderboard.LeaderboardRepository;
import com.patina.codebloom.common.db.repos.question.QuestionRepository;
import com.patina.codebloom.common.leetcode.LeetcodeApiHandler;
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

    public SubmissionsHandler(QuestionRepository questionRepository, LeetcodeApiHandler leetcodeApiHandler,
            LeaderboardRepository leaderboardRepository) {
        this.questionRepository = questionRepository;
        this.leetcodeApiHandler = leetcodeApiHandler;
        this.leaderboardRepository = leaderboardRepository;
    }

    public ArrayList<AcceptedSubmission> handleSubmissions(ArrayList<LeetcodeSubmission> leetcodeSubmissions,
            User user) {
        ArrayList<AcceptedSubmission> acceptedSubmissions = new ArrayList<>();

        for (LeetcodeSubmission leetcodeSubmission : leetcodeSubmissions) {
            if (leetcodeSubmission.getStatusDisplay().equals("Accepted")) {
                continue;
            }

            Question question = questionRepository.getQuestionBySlug(leetcodeSubmission.getTitleSlug());

            if (question != null) {
                continue;
            }

            LeetcodeQuestion leetcodeQuestion = leetcodeApiHandler
                    .findQuestionBySlug(leetcodeSubmission.getTitleSlug());

            int points = ScoreCalculator.calculateScore(QuestionDifficulty.valueOf(leetcodeQuestion.getDifficulty()),
                    leetcodeQuestion.getAcceptanceRate());

            Question newQuestion = new Question(
                    user.getId(),
                    leetcodeQuestion.getTitleSlug(),
                    QuestionDifficulty.valueOf(leetcodeQuestion.getDifficulty()),
                    leetcodeQuestion.getQuestionId(),
                    "https://leetcode.com/problems" + leetcodeQuestion.getTitleSlug(),
                    leetcodeQuestion.getQuestionTitle(),
                    leetcodeQuestion.getQuestion(),
                    OptionalInt.of(points),
                    leetcodeQuestion.getAcceptanceRate());

            questionRepository.createQuestion(newQuestion);

            acceptedSubmissions.add(new AcceptedSubmission(leetcodeQuestion.getQuestionTitle(), points));

            // TODO - Update the points given on the current leaderboard.
            Leaderboard recentLeaderboard = leaderboardRepository.getRecentLeaderboardShallow();

            // This should never be happening as there should always be an existing
            // leaderboard to fall on. Howerver, race conditions could trigger this problem.
            if (recentLeaderboard == null) {
                throw new RuntimeException("No recent leaderboard found.");
            }

            UserWithScore recentUserMetadata = leaderboardRepository.getUserFromLeaderboard(recentLeaderboard.getId(),
                    user.getId());

            leaderboardRepository.updateUserPointsFromLeaderboard(recentLeaderboard.getId(), user.getId(),
                    recentUserMetadata.getTotalScore() + points);
        }

        return acceptedSubmissions;
    }
}
