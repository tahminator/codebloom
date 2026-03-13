package org.patinanetwork.codebloom.scheduled.metrics;

import jakarta.annotation.PostConstruct;
import java.time.ZoneId;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.patinanetwork.codebloom.common.components.LeaderboardException;
import org.patinanetwork.codebloom.common.db.models.leaderboard.Leaderboard;
import org.patinanetwork.codebloom.common.db.models.task.BackgroundTask;
import org.patinanetwork.codebloom.common.db.models.task.BackgroundTaskEnum;
import org.patinanetwork.codebloom.common.db.models.user.UserMetrics;
import org.patinanetwork.codebloom.common.db.models.user.UserWithScore;
import org.patinanetwork.codebloom.common.db.repos.leaderboard.LeaderboardRepository;
import org.patinanetwork.codebloom.common.db.repos.leaderboard.options.LeaderboardFilterOptions;
import org.patinanetwork.codebloom.common.db.repos.task.BackgroundTaskRepository;
import org.patinanetwork.codebloom.common.db.repos.user.UserMetricsRepository;
import org.patinanetwork.codebloom.common.env.Env;
import org.patinanetwork.codebloom.common.time.StandardizedOffsetDateTime;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("!ci")
public class AddUserMetricsService {

    private final BackgroundTaskRepository backgroundTaskRepository;
    private final LeaderboardRepository leaderboardRepository;
    private final UserMetricsRepository userMetricsRepository;
    private final Env env;

    public AddUserMetricsService(
            final BackgroundTaskRepository backgroundTaskRepository,
            final LeaderboardRepository leaderboardRepository,
            final UserMetricsRepository userMetricsRepository,
            final Env env) {
        this.backgroundTaskRepository = backgroundTaskRepository;
        this.leaderboardRepository = leaderboardRepository;
        this.userMetricsRepository = userMetricsRepository;
        this.env = env;
    }

    @PostConstruct
    public void init() {
        if (env.isDev()) {
            log.info("Running user metrics service.");
        }
    }

    @Scheduled(cron = "0 */30 * * * *")
    public void congregateUserMetrics() throws LeaderboardException {
        BackgroundTask recentMetricsTask = backgroundTaskRepository.getMostRecentlyCompletedBackgroundTaskByTaskEnum(
                BackgroundTaskEnum.USER_METRICS);

        var midnight = StandardizedOffsetDateTime.now()
                .atZoneSameInstant(ZoneId.systemDefault())
                .toLocalDate()
                .atStartOfDay(ZoneId.systemDefault())
                .toOffsetDateTime();

        if (recentMetricsTask != null
                && recentMetricsTask.getCompletedAt() != null
                && !recentMetricsTask.getCompletedAt().isBefore(midnight)) {
            log.info("Skipping user metrics sync because today's snapshot already exists.");
            return;
        }

        Leaderboard currentLeaderboard = leaderboardRepository
                .getRecentLeaderboardMetadata()
                .orElseThrow(() -> new LeaderboardException("No leaderboard", "No recent leaderboard was found"));

        if (currentLeaderboard == null) {
            log.warn("Skipping user metrics sync because there is no active leaderboard.");
            return;
        }

        int leaderboardUserCount = leaderboardRepository.getLeaderboardUserCountById(
                currentLeaderboard.getId(), LeaderboardFilterOptions.DEFAULT);
        List<UserWithScore> leaderboardUsers = leaderboardUserCount == 0
                ? List.of()
                : leaderboardRepository.getLeaderboardUsersById(
                        currentLeaderboard.getId(),
                        LeaderboardFilterOptions.builder()
                                .pageSize(leaderboardUserCount)
                                .build());

        for (var leaderboardUser : leaderboardUsers) {
            userMetricsRepository.createUserMetrics(UserMetrics.builder()
                    .userId(leaderboardUser.getId())
                    .points(leaderboardUser.getTotalScore())
                    .build());
        }

        backgroundTaskRepository.createBackgroundTask(BackgroundTask.builder()
                .task(BackgroundTaskEnum.USER_METRICS)
                .completedAt(StandardizedOffsetDateTime.now())
                .build());
    }
}
