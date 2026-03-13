package org.patinanetwork.codebloom.scheduled.metrics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.patinanetwork.codebloom.common.components.LeaderboardException;
import org.patinanetwork.codebloom.common.db.models.leaderboard.Leaderboard;
import org.patinanetwork.codebloom.common.db.models.task.BackgroundTask;
import org.patinanetwork.codebloom.common.db.models.task.BackgroundTaskEnum;
import org.patinanetwork.codebloom.common.db.models.user.UserMetrics;
import org.patinanetwork.codebloom.common.db.models.user.UserWithScore;
import org.patinanetwork.codebloom.common.db.repos.leaderboard.LeaderboardRepository;
import org.patinanetwork.codebloom.common.db.repos.task.BackgroundTaskRepository;
import org.patinanetwork.codebloom.common.db.repos.user.UserMetricsRepository;
import org.patinanetwork.codebloom.common.env.Env;

public class AddUserMetricsServiceTest {

    private BackgroundTaskRepository backgroundTaskRepository;
    private LeaderboardRepository leaderboardRepository;
    private UserMetricsRepository userMetricsRepository;
    private Env env;

    private AddUserMetricsService service;

    @BeforeEach
    void setup() {
        backgroundTaskRepository = mock(BackgroundTaskRepository.class);
        leaderboardRepository = mock(LeaderboardRepository.class);
        userMetricsRepository = mock(UserMetricsRepository.class);
        env = mock(Env.class);

        service =
                new AddUserMetricsService(backgroundTaskRepository, leaderboardRepository, userMetricsRepository, env);
    }

    @Test
    void congregateUserMetricsCreatesSnapshotsForCurrentLeaderboard() throws LeaderboardException {
        BackgroundTask lastSync = BackgroundTask.builder()
                .task(BackgroundTaskEnum.USER_METRICS)
                .completedAt(OffsetDateTime.now().minusDays(2))
                .build();
        when(backgroundTaskRepository.getMostRecentlyCompletedBackgroundTaskByTaskEnum(BackgroundTaskEnum.USER_METRICS))
                .thenReturn(lastSync);

        when(leaderboardRepository.getRecentLeaderboardMetadata())
                .thenReturn(Optional.of(Leaderboard.builder()
                        .id("leaderboard-1")
                        .name("Current")
                        .build()));
        when(leaderboardRepository.getLeaderboardUserCountById(
                        org.mockito.ArgumentMatchers.eq("leaderboard-1"), org.mockito.ArgumentMatchers.any()))
                .thenReturn(2);

        UserWithScore firstUser =
                UserWithScore.builder().id("user-1").totalScore(150).build();
        UserWithScore secondUser =
                UserWithScore.builder().id("user-2").totalScore(90).build();
        when(leaderboardRepository.getLeaderboardUsersById(
                        org.mockito.ArgumentMatchers.eq("leaderboard-1"), org.mockito.ArgumentMatchers.any()))
                .thenReturn(List.of(firstUser, secondUser));

        service.congregateUserMetrics();

        ArgumentCaptor<UserMetrics> metricsCaptor = ArgumentCaptor.forClass(UserMetrics.class);
        verify(userMetricsRepository, times(2)).createUserMetrics(metricsCaptor.capture());
        assertEquals(
                List.of("user-1", "user-2"),
                metricsCaptor.getAllValues().stream()
                        .map(UserMetrics::getUserId)
                        .toList());
        assertEquals(
                List.of(150, 90),
                metricsCaptor.getAllValues().stream()
                        .map(UserMetrics::getPoints)
                        .toList());

        ArgumentCaptor<BackgroundTask> taskCaptor = ArgumentCaptor.forClass(BackgroundTask.class);
        verify(backgroundTaskRepository, times(1)).createBackgroundTask(taskCaptor.capture());
        assertEquals(BackgroundTaskEnum.USER_METRICS, taskCaptor.getValue().getTask());
    }

    @Test
    void congregateUserMetricsSkipsWhenTodayEstSnapshotAlreadyExists() throws LeaderboardException {
        OffsetDateTime completedAfterTodayEstMidnight =
                ZonedDateTime.now(ZoneId.of("America/New_York")).minusMinutes(1).toOffsetDateTime();
        BackgroundTask lastSync = BackgroundTask.builder()
                .task(BackgroundTaskEnum.USER_METRICS)
                .completedAt(completedAfterTodayEstMidnight)
                .build();
        when(backgroundTaskRepository.getMostRecentlyCompletedBackgroundTaskByTaskEnum(BackgroundTaskEnum.USER_METRICS))
                .thenReturn(lastSync);

        service.congregateUserMetrics();

        verify(leaderboardRepository, never()).getRecentLeaderboardMetadata();
        verify(userMetricsRepository, never()).createUserMetrics(org.mockito.ArgumentMatchers.any());
        verify(backgroundTaskRepository, never()).createBackgroundTask(org.mockito.ArgumentMatchers.any());
    }
}
