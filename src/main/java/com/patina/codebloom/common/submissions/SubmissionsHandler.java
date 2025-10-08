package com.patina.codebloom.common.submissions;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Controller;

import com.patina.codebloom.common.db.models.leaderboard.Leaderboard;
import com.patina.codebloom.common.db.models.potd.POTD;
import com.patina.codebloom.common.db.models.question.Question;
import com.patina.codebloom.common.db.models.question.QuestionDifficulty;
import com.patina.codebloom.common.db.models.question.topic.LeetcodeTopicEnum;
import com.patina.codebloom.common.db.models.question.topic.QuestionTopic;
import com.patina.codebloom.common.db.models.user.User;
import com.patina.codebloom.common.db.models.user.UserWithScore;
import com.patina.codebloom.common.db.repos.leaderboard.LeaderboardRepository;
import com.patina.codebloom.common.db.repos.potd.POTDRepository;
import com.patina.codebloom.common.db.repos.question.QuestionRepository;
import com.patina.codebloom.common.db.repos.question.topic.QuestionTopicRepository;
import com.patina.codebloom.common.db.repos.user.UserRepository;
import com.patina.codebloom.common.db.repos.user.options.UserFilterOptions;
import com.patina.codebloom.common.leetcode.LeetcodeClient;
import com.patina.codebloom.common.leetcode.models.LeetcodeDetailedQuestion;
import com.patina.codebloom.common.leetcode.models.LeetcodeQuestion;
import com.patina.codebloom.common.leetcode.models.LeetcodeSubmission;
import com.patina.codebloom.common.leetcode.score.ScoreCalculator;
import com.patina.codebloom.common.leetcode.throttled.ThrottledLeetcodeClient;
import com.patina.codebloom.common.submissions.object.AcceptedSubmission;

/**
 * The submission logic is abstracted because it gets reused in two different
 * parts of the app: the submissions API and the automated recurring task.
 */
@Controller
public class SubmissionsHandler {
    private final QuestionRepository questionRepository;
    private final LeetcodeClient leetcodeClient;
    private final LeaderboardRepository leaderboardRepository;
    private final POTDRepository potdRepository;
    private final UserRepository userRepository;
    private final QuestionTopicRepository questionTopicRepository;

    private boolean isValid(final LocalDateTime createdAt) {
        // TODO - Replace EST locked functionality.
        ZoneId est = ZoneId.of("America/New_York");
        ZonedDateTime now = ZonedDateTime.now(est);
        ZonedDateTime cutoff = createdAt
                        .atZone(ZoneId.systemDefault())
                        .withZoneSameInstant(est)
                        .toLocalDate()
                        .plusDays(1)
                        .atTime(20, 0)
                        .atZone(est);
        return now.isBefore(cutoff);
    }

    public SubmissionsHandler(final QuestionRepository questionRepository, final ThrottledLeetcodeClient throttledLeetcodeClient, final LeaderboardRepository leaderboardRepository,
                    final POTDRepository potdRepository, final UserRepository userRepository, final QuestionTopicRepository questionTopicRepository) {
        this.questionRepository = questionRepository;
        this.leetcodeClient = throttledLeetcodeClient;
        this.leaderboardRepository = leaderboardRepository;
        this.potdRepository = potdRepository;
        this.userRepository = userRepository;
        this.questionTopicRepository = questionTopicRepository;
    }

    public ArrayList<AcceptedSubmission> handleSubmissions(final List<LeetcodeSubmission> leetcodeSubmissions, final User user) {
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

            if (potd == null
                            || !isValid(potd.getCreatedAt())
                            || !Objects.equals(potd.getSlug(), leetcodeSubmission.getTitleSlug())) {
                multiplier = 1.0f;
            } else {
                multiplier = potd.getMultiplier();
            }

            LeetcodeQuestion leetcodeQuestion = leetcodeClient.findQuestionBySlug(leetcodeSubmission.getTitleSlug());

            LeetcodeDetailedQuestion detailedQuestion = leetcodeClient.findSubmissionDetailBySubmissionId(leetcodeSubmission.getId());

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

            Question newQuestion = Question.builder()
                            .userId(user.getId())
                            .questionSlug(leetcodeQuestion.getTitleSlug())
                            .questionDifficulty(QuestionDifficulty.valueOf(leetcodeQuestion.getDifficulty()))
                            .questionNumber(leetcodeQuestion.getQuestionId())
                            .questionLink("https://leetcode.com/problems/" + leetcodeQuestion.getTitleSlug())
                            .questionTitle(leetcodeQuestion.getQuestionTitle())
                            .description(leetcodeQuestion.getQuestion())
                            .pointsAwarded(points)
                            .acceptanceRate(leetcodeQuestion.getAcceptanceRate())
                            .submittedAt(leetcodeSubmission.getTimestamp())
                            .runtime(detailedQuestion.getRuntimeDisplay())
                            .memory(detailedQuestion.getMemoryDisplay())
                            .code(detailedQuestion.getCode())
                            .language(detailedQuestion.getLang().getName())
                            .submissionId(String.valueOf(leetcodeSubmission.getId()))
                            .build();

            questionRepository.createQuestion(newQuestion);

            leetcodeQuestion.getTopics().stream().forEach(topic -> questionTopicRepository.createQuestionTopic(
                            QuestionTopic.builder()
                                            .questionId(newQuestion.getId())
                                            .topicSlug(topic.getSlug())
                                            .topic(LeetcodeTopicEnum.fromValue(topic.getSlug()))
                                            .build()));

            acceptedSubmissions.add(new AcceptedSubmission(leetcodeQuestion.getQuestionTitle(), points));

            UserWithScore recentUserMetadata = userRepository.getUserWithScoreByIdAndLeaderboardId(user.getId(), recentLeaderboard.getId(), UserFilterOptions.builder().build());

            leaderboardRepository.updateUserPointsFromLeaderboard(recentLeaderboard.getId(), user.getId(), recentUserMetadata.getTotalScore() + points);
        }

        return acceptedSubmissions;
    }

    // Uncomment this if you need to run the script. The 5 second delay allows the
    // LeetcodeAuthStealer to run it's method first.
    // @Scheduled(initialDelay = 5000, fixedDelay = 1000000)
    public void updateSubmissions() {
        System.out.println("Migration script activated. DO NOT LEAVE THIS ON IN PRODUCTION.");
        List<User> users = userRepository.getAllUsers();
        for (User user : users) {
            System.out.println("Starting migration for user ID " + user.getId());
            List<LeetcodeSubmission> leetcodeSubmissions = leetcodeClient.findSubmissionsByUsername(user.getLeetcodeUsername());

            for (LeetcodeSubmission leetcodeSubmission : leetcodeSubmissions) {
                if (!leetcodeSubmission.getStatusDisplay().equals("Accepted")) {
                    continue;
                }

                Question question = questionRepository.getQuestionBySlugAndUserId(leetcodeSubmission.getTitleSlug(), user.getId());

                if (question == null || question.getCode() != null) {
                    continue;
                }

                LeetcodeDetailedQuestion detailedQuestion = leetcodeClient.findSubmissionDetailBySubmissionId(leetcodeSubmission.getId());

                if (detailedQuestion == null) {
                    continue;
                }

                System.out.println("Attempting to update User ID" + user.getId() + " with question of " + question.getQuestionSlug());

                Question newQuestion = Question.builder()
                                .id(question.getId())
                                .userId(question.getUserId())
                                .questionSlug(question.getQuestionSlug())
                                .questionDifficulty(question.getQuestionDifficulty())
                                .questionNumber(question.getQuestionNumber())
                                .questionLink(question.getQuestionLink())
                                .pointsAwarded(question.getPointsAwarded())
                                .questionTitle(question.getQuestionTitle())
                                .description(question.getDescription())
                                .acceptanceRate(question.getAcceptanceRate())
                                .createdAt(question.getCreatedAt())
                                .submittedAt(question.getSubmittedAt())
                                .runtime(detailedQuestion.getRuntimeDisplay())
                                .memory(detailedQuestion.getMemoryDisplay())
                                .code(detailedQuestion.getCode())
                                .language(detailedQuestion.getLang().getName())
                                .submissionId(String.valueOf(leetcodeSubmission.getId()))
                                .build();

                questionRepository.updateQuestion(newQuestion);
            }
        }

        System.out.println("Exiting migration script.");
    }
}
