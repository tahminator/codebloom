package com.patina.codebloom.component;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.github.javafaker.Faker;
import com.patina.codebloom.common.components.LeaderboardManager;
import com.patina.codebloom.common.db.models.achievements.Achievement;
import com.patina.codebloom.common.db.models.achievements.AchievementPlaceEnum;
import com.patina.codebloom.common.db.models.leaderboard.Leaderboard;
import com.patina.codebloom.common.db.models.user.UserWithScore;
import com.patina.codebloom.common.db.models.usertag.Tag;
import com.patina.codebloom.common.db.models.usertag.UserTag;
import com.patina.codebloom.common.db.repos.achievements.AchievementRepository;
import com.patina.codebloom.common.db.repos.leaderboard.LeaderboardRepository;
import com.patina.codebloom.common.page.Indexed;
import com.patina.codebloom.common.time.StandardizedLocalDateTime;
import com.patina.codebloom.db.leaderboard.options.LeaderboardFilterGeneratorTest;

public class LeaderboardManagerTest {
    private final LeaderboardManager leaderboardManager;
    private final Faker faker;

    private LeaderboardRepository leaderboardRepository = mock(LeaderboardRepository.class);
    private AchievementRepository achievementRepository = mock(AchievementRepository.class);

    private final int validLeaderboardTags = LeaderboardFilterGeneratorTest.VALID_LEADERBOARD_TAGS.size();

    public LeaderboardManagerTest() {
        this.leaderboardManager = new LeaderboardManager(leaderboardRepository, achievementRepository);
        this.faker = Faker.instance();
    }

    private String randomSnowflake() {
        return String.valueOf(faker.number().randomNumber(18, true));
    }

    private UserWithScore.UserWithScoreBuilder<?, ?> randomPartialUserWithScore() {
        return UserWithScore.builder()
                        .id(UUID.randomUUID().toString())
                        .discordId(randomSnowflake())
                        .discordName(faker.name().username())
                        .leetcodeUsername(faker.name().username())
                        .admin(faker.bool().bool())
                        .verifyKey(faker.crypto().md5());
    };

    private void assertAchievement(final Achievement achievement,
                    final Tag leaderboard,
                    final AchievementPlaceEnum placeEnum,
                    final String userId) {
        assertEquals(leaderboard, achievement.getLeaderboard());
        assertEquals(placeEnum, achievement.getPlace());
        assertEquals(userId, achievement.getUserId());
    }

    @Test
    void testWithNoAvailableLeaderboard() {
        when(leaderboardRepository.getRecentLeaderboardMetadata()).thenReturn(null);

        leaderboardManager.generateAchievementsForAllWinners();

        verify(leaderboardRepository,
                        times(0)).getRankedIndexedLeaderboardUsersById(any(), any());
        verify(leaderboardRepository,
                        times(0)).getGlobalRankedIndexedLeaderboardUsersById(any(), any());
        verify(achievementRepository, times(0)).createAchievement(any());
    }

    @Test
    void testWithAvailableLeaderboardButNoUsers() {
        var latestLeaderboard = Leaderboard.builder()
                        .id(UUID.randomUUID().toString())
                        .name("Test Leaderboard")
                        .createdAt(StandardizedLocalDateTime.now())
                        .build();

        when(leaderboardRepository.getRecentLeaderboardMetadata()).thenReturn(latestLeaderboard);
        when(
                        leaderboardRepository.getRankedIndexedLeaderboardUsersById(eq(latestLeaderboard.getId()), any()))
                                        .thenReturn(List.of());

        leaderboardManager.generateAchievementsForAllWinners();

        verify(leaderboardRepository,
                        times(validLeaderboardTags)).getRankedIndexedLeaderboardUsersById(any(), any());
        verify(leaderboardRepository,
                        times(1)).getGlobalRankedIndexedLeaderboardUsersById(any(), any());
        verify(achievementRepository, times(0)).createAchievement(any());
    }

    @Test
    void testWithAvailableLeaderboardAndTwoWinnersGlobal() {
        var latestLeaderboard = Leaderboard.builder()
                        .id(UUID.randomUUID().toString())
                        .name("Test Leaderboard")
                        .createdAt(StandardizedLocalDateTime.now())
                        .build();

        var winners = Indexed.ofDefaultList(
                        List.of(
                                        randomPartialUserWithScore()
                                                        .totalScore(150_000)
                                                        .build(),
                                        randomPartialUserWithScore()
                                                        .totalScore(70_000)
                                                        .build()));

        when(leaderboardRepository.getRecentLeaderboardMetadata()).thenReturn(latestLeaderboard);
        when(
                        leaderboardRepository.getGlobalRankedIndexedLeaderboardUsersById(eq(latestLeaderboard.getId()), any()))
                                        .thenReturn(winners);

        leaderboardManager.generateAchievementsForAllWinners();

        ArgumentCaptor<Achievement> captor = ArgumentCaptor.forClass(Achievement.class);
        verify(achievementRepository, times(winners.size())).createAchievement(captor.capture());

        verify(leaderboardRepository,
                        times(validLeaderboardTags)).getRankedIndexedLeaderboardUsersById(any(), any());
        verify(leaderboardRepository,
                        times(1)).getGlobalRankedIndexedLeaderboardUsersById(any(), any());

        var achievements = captor.getAllValues();
        var userOneAchievement = achievements.get(0);
        var userTwoAchievement = achievements.get(1);
        assertAchievement(userOneAchievement, null, AchievementPlaceEnum.ONE, winners.get(0).getItem().getId());
        assertAchievement(userTwoAchievement, null, AchievementPlaceEnum.TWO, winners.get(1).getItem().getId());
    }

    @Test
    void testWithAvailableLeaderboardAndTwoWinnersGlobalWithOneValidTag() {
        var latestLeaderboard = Leaderboard.builder()
                        .id(UUID.randomUUID().toString())
                        .name("Test Leaderboard")
                        .createdAt(StandardizedLocalDateTime.now())
                        .build();

        var winners = Indexed.ofDefaultList(
                        List.of(
                                        randomPartialUserWithScore()
                                                        .totalScore(150_000)
                                                        .build(),
                                        randomPartialUserWithScore()
                                                        .totalScore(70_000)
                                                        .build()));

        winners.forEach(winner -> {
            var user = winner.getItem();
            user.setTags(
                            List.of(
                                            UserTag.builder()
                                                            .id(UUID.randomUUID().toString())
                                                            .tag(Tag.Sbu)
                                                            .userId(user.getId())
                                                            .build()));
        });

        when(leaderboardRepository.getRecentLeaderboardMetadata()).thenReturn(latestLeaderboard);
        when(
                        leaderboardRepository.getGlobalRankedIndexedLeaderboardUsersById(eq(latestLeaderboard.getId()), any()))
                                        .thenReturn(winners);

        when(
                        leaderboardRepository.getRankedIndexedLeaderboardUsersById(eq(latestLeaderboard.getId()), argThat(opt -> opt.isSbu())))
                                        .thenReturn(winners);

        leaderboardManager.generateAchievementsForAllWinners();

        ArgumentCaptor<Achievement> captor = ArgumentCaptor.forClass(Achievement.class);
        verify(achievementRepository, times(winners.size() * 2))
                        .createAchievement(captor.capture());

        verify(leaderboardRepository,
                        times(validLeaderboardTags)).getRankedIndexedLeaderboardUsersById(any(), any());
        verify(leaderboardRepository,
                        times(1)).getGlobalRankedIndexedLeaderboardUsersById(any(), any());

        var achievements = captor.getAllValues();
        var userOneSbuAchievement = achievements.get(0);
        var userTwoSbuAchievement = achievements.get(1);
        assertAchievement(userOneSbuAchievement, Tag.Sbu, AchievementPlaceEnum.ONE, winners.get(0).getItem().getId());
        assertAchievement(userTwoSbuAchievement, Tag.Sbu, AchievementPlaceEnum.TWO, winners.get(1).getItem().getId());
        var userOneGlobalAchievement = achievements.get(2);
        var userTwoGlobalAchievement = achievements.get(3);
        assertAchievement(userOneGlobalAchievement, null, AchievementPlaceEnum.ONE, winners.get(0).getItem().getId());
        assertAchievement(userTwoGlobalAchievement, null, AchievementPlaceEnum.TWO, winners.get(1).getItem().getId());
    }

    @Test
    void testWithAvailableLeaderboardAndTwoWinnersGlobalWithTwoValidTags() {
        var latestLeaderboard = Leaderboard.builder()
                        .id(UUID.randomUUID().toString())
                        .name("Test Leaderboard")
                        .createdAt(StandardizedLocalDateTime.now())
                        .build();

        var winners = Indexed.ofDefaultList(
                        List.of(
                                        randomPartialUserWithScore()
                                                        .totalScore(150_000)
                                                        .build(),
                                        randomPartialUserWithScore()
                                                        .totalScore(70_000)
                                                        .build()));

        winners.forEach(winner -> {
            var user = winner.getItem();
            user.setTags(
                            List.of(
                                            UserTag.builder()
                                                            .id(UUID.randomUUID().toString())
                                                            .tag(Tag.Sbu)
                                                            .userId(user.getId())
                                                            .build(),
                                            UserTag.builder()
                                                            .id(UUID.randomUUID().toString())
                                                            .tag(Tag.Patina)
                                                            .userId(user.getId())
                                                            .build()));
        });

        when(leaderboardRepository.getRecentLeaderboardMetadata()).thenReturn(latestLeaderboard);
        when(
                        leaderboardRepository.getGlobalRankedIndexedLeaderboardUsersById(eq(latestLeaderboard.getId()), any()))
                                        .thenReturn(winners);

        when(
                        leaderboardRepository.getRankedIndexedLeaderboardUsersById(eq(latestLeaderboard.getId()), argThat(opt -> opt.isSbu())))
                                        .thenReturn(winners);

        when(
                        leaderboardRepository.getRankedIndexedLeaderboardUsersById(eq(latestLeaderboard.getId()), argThat(opt -> opt.isPatina())))
                                        .thenReturn(winners);

        leaderboardManager.generateAchievementsForAllWinners();

        ArgumentCaptor<Achievement> captor = ArgumentCaptor.forClass(Achievement.class);
        verify(achievementRepository, times(winners.size() * 3))
                        .createAchievement(captor.capture());

        verify(leaderboardRepository,
                        times(validLeaderboardTags)).getRankedIndexedLeaderboardUsersById(any(), any());
        verify(leaderboardRepository,
                        times(1)).getGlobalRankedIndexedLeaderboardUsersById(any(), any());

        /**
         * keep in mind the order of how we check filters is ordered. check
         * LeaderboardFilterGenerator for order.
         */
        var achievements = captor.getAllValues();
        var userOneSbuAchievement = achievements.get(0);
        var userTwoSbuAchievement = achievements.get(1);
        assertAchievement(userOneSbuAchievement, Tag.Sbu, AchievementPlaceEnum.ONE, winners.get(0).getItem().getId());
        assertAchievement(userTwoSbuAchievement, Tag.Sbu, AchievementPlaceEnum.TWO, winners.get(1).getItem().getId());
        var userOnePatinaAchievement = achievements.get(2);
        var userTwoPatinaAchievement = achievements.get(3);
        assertAchievement(userOnePatinaAchievement, Tag.Patina, AchievementPlaceEnum.ONE, winners.get(0).getItem().getId());
        assertAchievement(userTwoPatinaAchievement, Tag.Patina, AchievementPlaceEnum.TWO, winners.get(1).getItem().getId());
        var userOneGlobalAchievement = achievements.get(4);
        var userTwoGlobalAchievement = achievements.get(5);
        assertAchievement(userOneGlobalAchievement, null, AchievementPlaceEnum.ONE, winners.get(0).getItem().getId());
        assertAchievement(userTwoGlobalAchievement, null, AchievementPlaceEnum.TWO, winners.get(1).getItem().getId());
    }

    @Test
    void testWithAvailableLeaderboardAndTwoWinnersGlobalWithOneValidOneInvalidTag() {
        var latestLeaderboard = Leaderboard.builder()
                        .id(UUID.randomUUID().toString())
                        .name("Test Leaderboard")
                        .createdAt(StandardizedLocalDateTime.now())
                        .build();

        var winners = Indexed.ofDefaultList(
                        List.of(
                                        randomPartialUserWithScore()
                                                        .totalScore(150_000)
                                                        .build(),
                                        randomPartialUserWithScore()
                                                        .totalScore(70_000)
                                                        .build()));

        winners.forEach(winner -> {
            var user = winner.getItem();
            user.setTags(
                            List.of(
                                            UserTag.builder()
                                                            .id(UUID.randomUUID().toString())
                                                            .tag(Tag.Sbu)
                                                            .userId(user.getId())
                                                            .build(),
                                            UserTag.builder()
                                                            .id(UUID.randomUUID().toString())
                                                            .tag(Tag.Gwc)
                                                            .userId(user.getId())
                                                            .build()));
        });

        when(leaderboardRepository.getRecentLeaderboardMetadata()).thenReturn(latestLeaderboard);
        when(
                        leaderboardRepository.getGlobalRankedIndexedLeaderboardUsersById(eq(latestLeaderboard.getId()), any()))
                                        .thenReturn(winners);

        when(
                        leaderboardRepository.getRankedIndexedLeaderboardUsersById(eq(latestLeaderboard.getId()), argThat(opt -> opt.isSbu())))
                                        .thenReturn(winners);

        when(
                        leaderboardRepository.getRankedIndexedLeaderboardUsersById(eq(latestLeaderboard.getId()), argThat(opt -> opt.isGwc())))
                                        .thenReturn(winners);

        leaderboardManager.generateAchievementsForAllWinners();

        verify(achievementRepository, times(0))
                        .createAchievement(argThat(achievement -> achievement.getLeaderboard() == Tag.Gwc));

        ArgumentCaptor<Achievement> captor = ArgumentCaptor.forClass(Achievement.class);
        verify(achievementRepository, times(winners.size() * 2))
                        .createAchievement(captor.capture());

        verify(leaderboardRepository,
                        times(validLeaderboardTags)).getRankedIndexedLeaderboardUsersById(any(), any());
        verify(leaderboardRepository,
                        times(1)).getGlobalRankedIndexedLeaderboardUsersById(any(), any());

        /**
         * keep in mind the order of how we check filters is ordered. check
         * LeaderboardFilterGenerator for order.
         */
        var achievements = captor.getAllValues();
        var userOneSbuAchievement = achievements.get(0);
        var userTwoSbuAchievement = achievements.get(1);
        assertAchievement(userOneSbuAchievement, Tag.Sbu, AchievementPlaceEnum.ONE, winners.get(0).getItem().getId());
        assertAchievement(userTwoSbuAchievement, Tag.Sbu, AchievementPlaceEnum.TWO, winners.get(1).getItem().getId());
        var userOneGlobalAchievement = achievements.get(2);
        var userTwoGlobalAchievement = achievements.get(3);
        assertAchievement(userOneGlobalAchievement, null, AchievementPlaceEnum.ONE, winners.get(0).getItem().getId());
        assertAchievement(userTwoGlobalAchievement, null, AchievementPlaceEnum.TWO, winners.get(1).getItem().getId());
    }

    @Test
    void testWithAvailableLeaderboardAndThreeWinnersGlobalButFourUsers() {
        var latestLeaderboard = Leaderboard.builder()
                        .id(UUID.randomUUID().toString())
                        .name("Test Leaderboard")
                        .createdAt(StandardizedLocalDateTime.now())
                        .build();

        var winners = Indexed.ofDefaultList(
                        List.of(
                                        randomPartialUserWithScore()
                                                        .totalScore(150_000)
                                                        .build(),
                                        randomPartialUserWithScore()
                                                        .totalScore(70_000)
                                                        .build(),
                                        randomPartialUserWithScore()
                                                        .totalScore(30_000)
                                                        .build(),
                                        randomPartialUserWithScore()
                                                        .totalScore(29_999)
                                                        .build()));

        when(leaderboardRepository.getRecentLeaderboardMetadata()).thenReturn(latestLeaderboard);
        when(
                        leaderboardRepository.getGlobalRankedIndexedLeaderboardUsersById(eq(latestLeaderboard.getId()), any()))
                                        .thenReturn(winners);

        leaderboardManager.generateAchievementsForAllWinners();

        ArgumentCaptor<Achievement> captor = ArgumentCaptor.forClass(Achievement.class);
        verify(achievementRepository, times(leaderboardManager.MAX_POSSIBLE_WINNERS))
                        .createAchievement(captor.capture());

        verify(leaderboardRepository,
                        times(validLeaderboardTags)).getRankedIndexedLeaderboardUsersById(any(), any());
        verify(leaderboardRepository,
                        times(1)).getGlobalRankedIndexedLeaderboardUsersById(any(), any());

        var achievements = captor.getAllValues();
        var userOneAchievement = achievements.get(0);
        var userTwoAchievement = achievements.get(1);
        assertAchievement(userOneAchievement, null, AchievementPlaceEnum.ONE, winners.get(0).getItem().getId());
        assertAchievement(userTwoAchievement, null, AchievementPlaceEnum.TWO, winners.get(1).getItem().getId());
    }

    @Test
    void testGetLeaderboardMetadata() {
        String testId = UUID.randomUUID().toString();
        var leaderboard = Leaderboard.builder()
                        .id(testId)
                        .name("Testing Leaderboard")
                        .createdAt(StandardizedLocalDateTime.now())
                        .build();

        Leaderboard leaderboardData = leaderboardManager.getLeaderboardMetadata(testId);
        assertEquals(leaderboard, leaderboardData);
    }
}
