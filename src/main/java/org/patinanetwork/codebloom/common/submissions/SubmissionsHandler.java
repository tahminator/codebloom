package org.patinanetwork.codebloom.common.submissions;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.patinanetwork.codebloom.common.db.models.job.Job;
import org.patinanetwork.codebloom.common.db.models.job.JobStatus;
import org.patinanetwork.codebloom.common.db.models.leaderboard.Leaderboard;
import org.patinanetwork.codebloom.common.db.models.potd.POTD;
import org.patinanetwork.codebloom.common.db.models.question.Question;
import org.patinanetwork.codebloom.common.db.models.question.QuestionDifficulty;
import org.patinanetwork.codebloom.common.db.models.question.topic.LeetcodeTopicEnum;
import org.patinanetwork.codebloom.common.db.models.question.topic.QuestionTopic;
import org.patinanetwork.codebloom.common.db.models.user.User;
import org.patinanetwork.codebloom.common.db.models.user.UserWithScore;
import org.patinanetwork.codebloom.common.db.repos.job.JobRepository;
import org.patinanetwork.codebloom.common.db.repos.leaderboard.LeaderboardRepository;
import org.patinanetwork.codebloom.common.db.repos.potd.POTDRepository;
import org.patinanetwork.codebloom.common.db.repos.question.QuestionRepository;
import org.patinanetwork.codebloom.common.db.repos.question.topic.QuestionTopicRepository;
import org.patinanetwork.codebloom.common.db.repos.user.UserRepository;
import org.patinanetwork.codebloom.common.db.repos.user.options.UserFilterOptions;
import org.patinanetwork.codebloom.common.leetcode.LeetcodeClient;
import org.patinanetwork.codebloom.common.leetcode.models.LeetcodeQuestion;
import org.patinanetwork.codebloom.common.leetcode.models.LeetcodeSubmission;
import org.patinanetwork.codebloom.common.leetcode.score.ScoreCalculator;
import org.patinanetwork.codebloom.common.leetcode.throttled.ThrottledLeetcodeClient;
import org.patinanetwork.codebloom.common.reporter.Reporter;
import org.patinanetwork.codebloom.common.reporter.throttled.ThrottledReporter;
import org.patinanetwork.codebloom.common.submissions.object.AcceptedSubmission;
import org.patinanetwork.codebloom.common.utils.pair.Pair;
import org.springframework.stereotype.Component;

/**
 * The submission logic is abstracted because it gets reused in two different parts of the app: the submissions API and
 * the automated recurring task.
 */
@Component
public class SubmissionsHandler {

    private final QuestionRepository questionRepository;
    private final LeetcodeClient leetcodeClient;
    private final LeaderboardRepository leaderboardRepository;
    private final POTDRepository potdRepository;
    private final UserRepository userRepository;
    private final QuestionTopicRepository questionTopicRepository;
    private final JobRepository jobRepository;
    private final Reporter throttledReporter;

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

    public SubmissionsHandler(
            final QuestionRepository questionRepository,
            final ThrottledLeetcodeClient throttledLeetcodeClient,
            final LeaderboardRepository leaderboardRepository,
            final POTDRepository potdRepository,
            final UserRepository userRepository,
            final QuestionTopicRepository questionTopicRepository,
            final ThrottledReporter throttledReporter,
            final JobRepository jobRepository) {
        this.questionRepository = questionRepository;
        this.leetcodeClient = throttledLeetcodeClient;
        this.leaderboardRepository = leaderboardRepository;
        this.potdRepository = potdRepository;
        this.userRepository = userRepository;
        this.questionTopicRepository = questionTopicRepository;
        this.jobRepository = jobRepository;
        this.throttledReporter = throttledReporter;
    }

    public static <T> Predicate<T> distinctByKey(final Function<? super T, ?> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    public ArrayList<AcceptedSubmission> handleSubmissions(
            final List<LeetcodeSubmission> leetcodeSubmissions, final User user) {
        ArrayList<AcceptedSubmission> acceptedSubmissions = new ArrayList<>();
        POTD potd = potdRepository.getCurrentPOTD();

        var questionMap = leetcodeSubmissions.parallelStream()
                .filter(distinctByKey(LeetcodeSubmission::getTitleSlug))
                .map(s -> Pair.of(s.getTitleSlug(), leetcodeClient.findQuestionBySlug(s.getTitleSlug())))
                .collect(Collectors.toMap(p -> p.getLeft(), p -> p.getRight()));

        for (LeetcodeSubmission leetcodeSubmission : leetcodeSubmissions) {
            if (!leetcodeSubmission.getStatusDisplay().equals("Accepted")) {
                continue;
            }

            if (questionRepository.questionExistsBySubmissionId(String.valueOf(leetcodeSubmission.getId()))) {
                continue;
            }

            Question question =
                    questionRepository.getQuestionBySlugAndUserId(leetcodeSubmission.getTitleSlug(), user.getId());

            float multiplier;
            if (potd == null
                    || !isValid(potd.getCreatedAt())
                    || !Objects.equals(potd.getSlug(), leetcodeSubmission.getTitleSlug())) {
                multiplier = 1.0f;
            } else {
                multiplier = potd.getMultiplier();
            }

            LeetcodeQuestion leetcodeQuestion = questionMap.get(leetcodeSubmission.getTitleSlug());

            // If the submission is before the leaderboard started, points awarded = 0
            Leaderboard recentLeaderboard = leaderboardRepository.getRecentLeaderboardMetadata();

            // This should never be happening as there should always be an existing
            // leaderboard to fall on. Howerver, race conditions could trigger this problem.
            if (recentLeaderboard == null) {
                throw new RuntimeException("No recent leaderboard found.");
            }

            boolean isTooLate = recentLeaderboard.getCreatedAt().isAfter(leetcodeSubmission.getTimestamp());

            // int basePoints = switch (leetcodeQuestion.getDifficulty().toUpperCase()) {
            // case "EASY" -> 100;
            // case "MEDIUM" -> 300;
            // case "HARD" -> 600;
            // default -> 0;
            // };

            int points;
            if (question != null || isTooLate) {
                points = 0;
            } else {
                points = ScoreCalculator.calculateScore(
                        QuestionDifficulty.valueOf(leetcodeQuestion.getDifficulty()),
                        leetcodeQuestion.getAcceptanceRate(),
                        multiplier);
            }

            // throttledReporter.log(Report.builder()
            // .data(String.format("""
            // Score Distribution Report

            // Leetcode Username: %s
            // Difficulty: %s (%d pts)
            // Acceptance Rate: %.2f
            // Question Multiplier: %.2f
            // Total: %d
            // """,
            // user.getLeetcodeUsername(),
            // leetcodeQuestion.getDifficulty(),
            // basePoints,
            // leetcodeQuestion.getAcceptanceRate(),
            // multiplier,
            // points
            // ))
            // .build());

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
                    .submissionId(String.valueOf(leetcodeSubmission.getId()))
                    .build();

            var createdQuestion = questionRepository.createQuestion(newQuestion);

            Job newJob = Job.builder()
                    .questionId(newQuestion.getId())
                    .status(JobStatus.INCOMPLETE)
                    .build();

            jobRepository.createJob(newJob);

            leetcodeQuestion.getTopics().stream()
                    .forEach(topic -> questionTopicRepository.createQuestionTopic(QuestionTopic.builder()
                            .questionId(newQuestion.getId())
                            .topicSlug(topic.getSlug())
                            .topic(LeetcodeTopicEnum.fromValue(topic.getSlug()))
                            .build()));

            acceptedSubmissions.add(
                    new AcceptedSubmission(leetcodeQuestion.getQuestionTitle(), createdQuestion.getId(), points));

            UserWithScore recentUserMetadata = userRepository.getUserWithScoreByIdAndLeaderboardId(
                    user.getId(), recentLeaderboard.getId(), UserFilterOptions.DEFAULT);

            leaderboardRepository.updateUserPointsFromLeaderboard(
                    recentLeaderboard.getId(), user.getId(), recentUserMetadata.getTotalScore() + points);
        }

        return acceptedSubmissions;
    }
}
