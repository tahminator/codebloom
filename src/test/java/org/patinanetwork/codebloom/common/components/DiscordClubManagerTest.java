package org.patinanetwork.codebloom.common.components;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.patinanetwork.codebloom.common.db.models.discord.DiscordClub;
import org.patinanetwork.codebloom.common.db.models.discord.DiscordClubMetadata;
import org.patinanetwork.codebloom.common.db.models.leaderboard.Leaderboard;
import org.patinanetwork.codebloom.common.db.models.user.UserWithScore;
import org.patinanetwork.codebloom.common.db.models.usertag.Tag;
import org.patinanetwork.codebloom.common.db.repos.discord.club.DiscordClubRepository;
import org.patinanetwork.codebloom.common.db.repos.leaderboard.LeaderboardRepository;
import org.patinanetwork.codebloom.common.db.repos.leaderboard.options.LeaderboardFilterOptions;
import org.patinanetwork.codebloom.common.url.ServerUrlUtils;
import org.patinanetwork.codebloom.jda.client.JDAClient;
import org.patinanetwork.codebloom.jda.client.options.EmbeddedImagesMessageOptions;
import org.patinanetwork.codebloom.playwright.PlaywrightClient;
import org.slf4j.LoggerFactory;

public class DiscordClubManagerTest {

    private JDAClient jdaClient = mock(JDAClient.class);
    private LeaderboardRepository leaderboardRepository = mock(LeaderboardRepository.class);
    private DiscordClubRepository discordClubRepository = mock(DiscordClubRepository.class);
    private PlaywrightClient playwrightClient = mock(PlaywrightClient.class);
    private ServerUrlUtils serverUrlUtils = mock(ServerUrlUtils.class);

    private DiscordClubManager discordClubManager;

    private ListAppender<ILoggingEvent> logWatcher;

    @BeforeEach
    void setUp() {
        discordClubManager = new DiscordClubManager(
                serverUrlUtils, jdaClient, leaderboardRepository, discordClubRepository, playwrightClient);

        logWatcher = new ListAppender<>();
        logWatcher.start();
        ((Logger) LoggerFactory.getLogger(discordClubManager.getClass())).addAppender(logWatcher);
    }

    @AfterEach
    void teardown() {
        ((Logger) LoggerFactory.getLogger(discordClubManager.getClass())).detachAndStopAllAppenders();
    }

    @Test
    void testSendLeaderboardCompletedDiscordMessageToAllClubsSuccess() {
        DiscordClub mockClub = createMockDiscordClub("Test Club", Tag.Rpi);
        when(discordClubRepository.getAllActiveDiscordClubs()).thenReturn(Arrays.asList(mockClub));

        setupMockLeaderboardData();

        discordClubManager.sendLeaderboardCompletedDiscordMessageToAllClubs();

        verify(discordClubRepository).getAllActiveDiscordClubs();
        verify(jdaClient).connect();
        verify(jdaClient).sendEmbedWithImages(any(EmbeddedImagesMessageOptions.class));
    }

    @Test
    void testSendLeaderboardCompletedDiscordMessageToAllClubsEmptyClubList() {
        when(discordClubRepository.getAllActiveDiscordClubs()).thenReturn(Collections.emptyList());

        discordClubManager.sendLeaderboardCompletedDiscordMessageToAllClubs();

        verify(discordClubRepository).getAllActiveDiscordClubs();
        verify(jdaClient, never()).connect();
        verify(jdaClient, never()).sendEmbedWithImages(any());
    }

    @Test
    void testSendLeaderboardCompletedDiscordMessageToAllClubsMultipleClubs() {
        DiscordClub club1 = createMockDiscordClub("Club 1", Tag.Rpi);
        DiscordClub club2 = createMockDiscordClub("Club 2", Tag.Baruch);
        when(discordClubRepository.getAllActiveDiscordClubs()).thenReturn(Arrays.asList(club1, club2));

        setupMockLeaderboardData();

        discordClubManager.sendLeaderboardCompletedDiscordMessageToAllClubs();

        verify(discordClubRepository).getAllActiveDiscordClubs();
        verify(jdaClient, times(2)).connect();
        verify(jdaClient, times(2)).sendEmbedWithImages(any(EmbeddedImagesMessageOptions.class));
    }

    @Test
    void testSendLeaderboardCompletedDiscordMessageMissingGuildId() {
        DiscordClub mockClub = createMockDiscordClubWithoutMetadata("Test Club", Tag.Rpi);
        when(discordClubRepository.getAllActiveDiscordClubs()).thenReturn(Arrays.asList(mockClub));

        setupMockLeaderboardData();

        discordClubManager.sendLeaderboardCompletedDiscordMessageToAllClubs();

        verify(jdaClient).connect();
        verify(jdaClient, never()).sendEmbedWithImages(any());
    }

    @Test
    void testSendWeeklyLeaderboardUpdateDiscordMessageToAllClubsSuccess() {
        DiscordClub mockClub = createMockDiscordClub("Test Club", Tag.Rpi);
        when(discordClubRepository.getAllActiveDiscordClubs()).thenReturn(Arrays.asList(mockClub));

        setupMockLeaderboardDataWithExpiration();

        discordClubManager.sendWeeklyLeaderboardUpdateDiscordMessageToAllClubs();

        verify(discordClubRepository).getAllActiveDiscordClubs();
        verify(jdaClient).connect();
        verify(jdaClient).sendEmbedWithImages(any(EmbeddedImagesMessageOptions.class));
    }

    @Test
    void testSendWeeklyLeaderboardUpdateDiscordMessageToAllClubsEmptyClubList() {
        when(discordClubRepository.getAllActiveDiscordClubs()).thenReturn(Collections.emptyList());

        discordClubManager.sendWeeklyLeaderboardUpdateDiscordMessageToAllClubs();

        verify(discordClubRepository).getAllActiveDiscordClubs();
        verify(jdaClient, never()).connect();
        verify(jdaClient, never()).sendEmbedWithImages(any());
    }

    @Test
    void testSendWeeklyLeaderboardUpdateDiscordMessageMissingChannelId() {
        DiscordClub mockClub = createMockDiscordClubWithPartialMetadata("Test Club", Tag.Rpi);
        when(discordClubRepository.getAllActiveDiscordClubs()).thenReturn(Arrays.asList(mockClub));

        setupMockLeaderboardDataWithExpiration();

        discordClubManager.sendWeeklyLeaderboardUpdateDiscordMessageToAllClubs();

        verify(jdaClient).connect();
        verify(jdaClient, never()).sendEmbedWithImages(any());
    }

    @Test
    void testSendWeeklyLeaderboardUpdateWithoutExpiration() {
        DiscordClub mockClub = createMockDiscordClub("Test Club", Tag.Rpi);
        when(discordClubRepository.getAllActiveDiscordClubs()).thenReturn(Arrays.asList(mockClub));

        setupMockLeaderboardDataWithoutExpiration();

        discordClubManager.sendWeeklyLeaderboardUpdateDiscordMessageToAllClubs();

        verify(discordClubRepository).getAllActiveDiscordClubs();
        verify(jdaClient).connect();
        verify(jdaClient).sendEmbedWithImages(any(EmbeddedImagesMessageOptions.class));
    }

    @Test
    void testSendLeaderboardCompletedDiscordMessageFiltersZeroPointUsers() {
        DiscordClub mockClub = createMockDiscordClub("Test Club", Tag.Rpi);
        when(discordClubRepository.getAllActiveDiscordClubs()).thenReturn(Arrays.asList(mockClub));

        setupMockLeaderboardDataWithMixedScoreUsers();

        discordClubManager.sendLeaderboardCompletedDiscordMessageToAllClubs();

        verify(discordClubRepository).getAllActiveDiscordClubs();
        verify(jdaClient).connect();
        verify(jdaClient).sendEmbedWithImages(any(EmbeddedImagesMessageOptions.class));
    }

    @Test
    void testSendLeaderboardCompletedDiscordMessageAllUsersHaveZeroPoints() {
        DiscordClub mockClub = createMockDiscordClub("Test Club", Tag.Rpi);
        when(discordClubRepository.getAllActiveDiscordClubs()).thenReturn(Arrays.asList(mockClub));

        setupMockLeaderboardDataWithAllZeroScoreUsers();

        discordClubManager.sendLeaderboardCompletedDiscordMessageToAllClubs();

        ArgumentCaptor<EmbeddedImagesMessageOptions> captor =
                ArgumentCaptor.forClass(EmbeddedImagesMessageOptions.class);
        verify(jdaClient).sendEmbedWithImages(captor.capture());

        String description = captor.getValue().getDescription();
        assertTrue(description.contains("No one claimed a spot on this leaderboard"));
        assertFalse(description.contains("CONGRATS TO THE WINNERS"));
        assertFalse(description.contains("🥇"));
        assertFalse(description.contains("🥈"));
        assertFalse(description.contains("🥉"));
    }

    @Test
    void testSendWeeklyLeaderboardUpdateDiscordMessageFiltersZeroPointUsers() {
        DiscordClub mockClub = createMockDiscordClub("Test Club", Tag.Rpi);
        when(discordClubRepository.getAllActiveDiscordClubs()).thenReturn(Arrays.asList(mockClub));

        setupMockLeaderboardDataWithExpirationAndMixedScoreUsers();

        discordClubManager.sendWeeklyLeaderboardUpdateDiscordMessageToAllClubs();

        verify(discordClubRepository).getAllActiveDiscordClubs();
        verify(jdaClient).connect();
        verify(jdaClient).sendEmbedWithImages(any(EmbeddedImagesMessageOptions.class));
    }

    @Test
    void testSendWeeklyLeaderboardUpdateDiscordMessageAllUsersHaveZeroPoints() {
        DiscordClub mockClub = createMockDiscordClub("Test Club", Tag.Rpi);
        when(discordClubRepository.getAllActiveDiscordClubs()).thenReturn(Arrays.asList(mockClub));

        setupMockLeaderboardDataWithExpirationAndAllZeroScoreUsers();

        discordClubManager.sendWeeklyLeaderboardUpdateDiscordMessageToAllClubs();

        ArgumentCaptor<EmbeddedImagesMessageOptions> captor =
                ArgumentCaptor.forClass(EmbeddedImagesMessageOptions.class);
        verify(jdaClient).sendEmbedWithImages(captor.capture());

        String description = captor.getValue().getDescription();
        assertTrue(description.contains("no scores yet"));
        assertFalse(description.contains("🥇"));
        assertFalse(description.contains("🥈"));
        assertFalse(description.contains("🥉"));
    }

    @Test
    void testSendLeaderboardCompletedDiscordMessageOnlyOneUserWithPoints() {
        DiscordClub mockClub = createMockDiscordClub("Test Club", Tag.Rpi);
        when(discordClubRepository.getAllActiveDiscordClubs()).thenReturn(Arrays.asList(mockClub));

        setupMockLeaderboardDataWithOneUser();

        discordClubManager.sendLeaderboardCompletedDiscordMessageToAllClubs();

        ArgumentCaptor<EmbeddedImagesMessageOptions> captor =
                ArgumentCaptor.forClass(EmbeddedImagesMessageOptions.class);
        verify(jdaClient).sendEmbedWithImages(captor.capture());

        String description = captor.getValue().getDescription();
        assertTrue(description.contains("🥇"));
        assertFalse(description.contains("🥈"));
        assertFalse(description.contains("🥉"));
    }

    @Test
    void testSendLeaderboardCompletedDiscordMessageOnlyTwoUsersWithPoints() {
        DiscordClub mockClub = createMockDiscordClub("Test Club", Tag.Rpi);
        when(discordClubRepository.getAllActiveDiscordClubs()).thenReturn(Arrays.asList(mockClub));

        setupMockLeaderboardDataWithTwoUsers();

        discordClubManager.sendLeaderboardCompletedDiscordMessageToAllClubs();

        ArgumentCaptor<EmbeddedImagesMessageOptions> captor =
                ArgumentCaptor.forClass(EmbeddedImagesMessageOptions.class);
        verify(jdaClient).sendEmbedWithImages(captor.capture());

        String description = captor.getValue().getDescription();
        assertTrue(description.contains("🥇"));
        assertTrue(description.contains("🥈"));
        assertFalse(description.contains("🥉"));
    }

    @Test
    void testSendWeeklyLeaderboardUpdateDiscordMessageOnlyOneUserWithPoints() {
        DiscordClub mockClub = createMockDiscordClub("Test Club", Tag.Rpi);
        when(discordClubRepository.getAllActiveDiscordClubs()).thenReturn(Arrays.asList(mockClub));

        setupMockLeaderboardDataWithExpirationAndOneUser();

        discordClubManager.sendWeeklyLeaderboardUpdateDiscordMessageToAllClubs();

        ArgumentCaptor<EmbeddedImagesMessageOptions> captor =
                ArgumentCaptor.forClass(EmbeddedImagesMessageOptions.class);
        verify(jdaClient).sendEmbedWithImages(captor.capture());

        String description = captor.getValue().getDescription();
        assertTrue(description.contains("🥇"));
        assertFalse(description.contains("🥈"));
        assertFalse(description.contains("🥉"));
    }

    @Test
    void testSendWeeklyLeaderboardUpdateDiscordMessageOnlyTwoUsersWithPoints() {
        DiscordClub mockClub = createMockDiscordClub("Test Club", Tag.Rpi);
        when(discordClubRepository.getAllActiveDiscordClubs()).thenReturn(Arrays.asList(mockClub));

        setupMockLeaderboardDataWithExpirationAndTwoUsers();

        discordClubManager.sendWeeklyLeaderboardUpdateDiscordMessageToAllClubs();

        ArgumentCaptor<EmbeddedImagesMessageOptions> captor =
                ArgumentCaptor.forClass(EmbeddedImagesMessageOptions.class);
        verify(jdaClient).sendEmbedWithImages(captor.capture());

        String description = captor.getValue().getDescription();
        assertTrue(description.contains("🥇"));
        assertTrue(description.contains("🥈"));
        assertFalse(description.contains("🥉"));
    }

    @Test
    void testSendTestEmbedMessageToClubSuccess() {
        String mockClubId = "9fc269d2-0622-11f1-9900-9b88519a41c8";
        DiscordClub mockClub = createMockDiscordClub("Test Club", Tag.Rpi);

        doNothing().when(jdaClient).sendEmbedWithImages(any(), anyInt(), any());

        boolean result = discordClubManager.sendTestEmbedMessageToClub(mockClubId);
        assertTrue(result);
        verify(jdaClient).connect();

        ArgumentCaptor<EmbeddedImagesMessageOptions> captor =
                ArgumentCaptor.forClass(EmbeddedImagesMessageOptions.class);
        verify(jdaClient).sendEmbedWithImages(captor.capture(), eq(10), eq(TimeUnit.SECONDS));

        String description = captor.getValue().getDescription();
        assertTrue(description.contains("test message"));
    }

    @Test
    void testSendTestEmbedMessageToClubInvalidId() {
        String mockClubId = "9fc269d2-0622-11f1-9900-9b88519a41c8";
        when(discordClubRepository.getDiscordClubById(mockClubId)).thenReturn(Optional.empty());

        boolean result = discordClubManager.sendTestEmbedMessageToClub(mockClubId);

        assertFalse(result, "Expected false if no club exists for the given id");
        verify(jdaClient).connect();
    }

    private DiscordClub createMockDiscordClub(final String name, final Tag tag) {
        DiscordClubMetadata metadata = mock(DiscordClubMetadata.class);
        when(metadata.getGuildId()).thenReturn(Optional.of("123456789"));
        when(metadata.getLeaderboardChannelId()).thenReturn(Optional.of("987654321"));

        DiscordClub club = mock(DiscordClub.class);
        when(club.getName()).thenReturn(name);
        when(club.getTag()).thenReturn(tag);
        when(club.getDiscordClubMetadata()).thenReturn(Optional.of(metadata));

        return club;
    }

    private DiscordClub createMockDiscordClubWithoutMetadata(final String name, final Tag tag) {
        DiscordClub club = mock(DiscordClub.class);
        when(club.getName()).thenReturn(name);
        when(club.getTag()).thenReturn(tag);
        when(club.getDiscordClubMetadata()).thenReturn(Optional.empty());

        return club;
    }

    private DiscordClub createMockDiscordClubWithPartialMetadata(final String name, final Tag tag) {
        DiscordClubMetadata metadata = mock(DiscordClubMetadata.class);
        when(metadata.getGuildId()).thenReturn(Optional.of("123456789"));
        when(metadata.getLeaderboardChannelId()).thenReturn(Optional.empty());

        DiscordClub club = mock(DiscordClub.class);
        when(club.getName()).thenReturn(name);
        when(club.getTag()).thenReturn(tag);
        when(club.getDiscordClubMetadata()).thenReturn(Optional.of(metadata));

        return club;
    }

    private void setupMockLeaderboardData() {
        Leaderboard mockLeaderboard = mock(Leaderboard.class);
        when(mockLeaderboard.getId()).thenReturn("leaderboard-id");
        when(mockLeaderboard.getName()).thenReturn("Test Leaderboard");

        when(leaderboardRepository.getRecentLeaderboardMetadata()).thenReturn(mockLeaderboard);
        when(leaderboardRepository.getLeaderboardUserCountById(eq("leaderboard-id"), any()))
                .thenReturn(25);

        List<UserWithScore> mockUsers = createMockUsers();
        when(leaderboardRepository.getLeaderboardUsersById(eq("leaderboard-id"), any(LeaderboardFilterOptions.class)))
                .thenReturn(mockUsers);

        when(playwrightClient.getCodebloomLeaderboardScreenshot(anyInt(), any(Tag.class)))
                .thenReturn("mock-screenshot".getBytes());

        when(serverUrlUtils.getUrl()).thenReturn("http://localhost:3000");
    }

    private void setupMockLeaderboardDataWithExpiration() {
        setupMockLeaderboardData();

        Leaderboard mockLeaderboard = mock(Leaderboard.class);
        when(mockLeaderboard.getId()).thenReturn("leaderboard-id");
        when(mockLeaderboard.getName()).thenReturn("Test Leaderboard");
        when(mockLeaderboard.getShouldExpireBy()).thenReturn(LocalDateTime.now().plusDays(7));

        when(leaderboardRepository.getRecentLeaderboardMetadata()).thenReturn(mockLeaderboard);
    }

    private void setupMockLeaderboardDataWithoutExpiration() {
        setupMockLeaderboardData();

        Leaderboard mockLeaderboard = mock(Leaderboard.class);
        when(mockLeaderboard.getId()).thenReturn("leaderboard-id");
        when(mockLeaderboard.getName()).thenReturn("Test Leaderboard");
        when(mockLeaderboard.getShouldExpireBy()).thenReturn(null);

        when(leaderboardRepository.getRecentLeaderboardMetadata()).thenReturn(mockLeaderboard);
    }

    private List<UserWithScore> createMockUsers() {
        UserWithScore user1 = mock(UserWithScore.class);
        when(user1.getDiscordId()).thenReturn("discord1");
        when(user1.getTotalScore()).thenReturn(100);

        UserWithScore user2 = mock(UserWithScore.class);
        when(user2.getDiscordId()).thenReturn("discord2");
        when(user2.getTotalScore()).thenReturn(80);

        UserWithScore user3 = mock(UserWithScore.class);
        when(user3.getDiscordId()).thenReturn("discord3");
        when(user3.getTotalScore()).thenReturn(60);

        return Arrays.asList(user1, user2, user3);
    }

    private List<UserWithScore> createMockUsersWithMixedScores() {
        UserWithScore user1 = mock(UserWithScore.class);
        when(user1.getDiscordId()).thenReturn("discord1");
        when(user1.getTotalScore()).thenReturn(100);

        UserWithScore user2 = mock(UserWithScore.class);
        when(user2.getDiscordId()).thenReturn("discord2");
        when(user2.getTotalScore()).thenReturn(0);

        UserWithScore user3 = mock(UserWithScore.class);
        when(user3.getDiscordId()).thenReturn("discord3");
        when(user3.getTotalScore()).thenReturn(50);

        UserWithScore user4 = mock(UserWithScore.class);
        when(user4.getDiscordId()).thenReturn("discord4");
        when(user4.getTotalScore()).thenReturn(0);

        return Arrays.asList(user1, user2, user3, user4);
    }

    private List<UserWithScore> createMockUsersAllZeroScores() {
        UserWithScore user1 = mock(UserWithScore.class);
        when(user1.getDiscordId()).thenReturn("discord1");
        when(user1.getTotalScore()).thenReturn(0);

        UserWithScore user2 = mock(UserWithScore.class);
        when(user2.getDiscordId()).thenReturn("discord2");
        when(user2.getTotalScore()).thenReturn(0);

        UserWithScore user3 = mock(UserWithScore.class);
        when(user3.getDiscordId()).thenReturn("discord3");
        when(user3.getTotalScore()).thenReturn(0);

        return Arrays.asList(user1, user2, user3);
    }

    private void setupMockLeaderboardDataWithMixedScoreUsers() {
        Leaderboard mockLeaderboard = mock(Leaderboard.class);
        when(mockLeaderboard.getId()).thenReturn("leaderboard-id");
        when(mockLeaderboard.getName()).thenReturn("Test Leaderboard");

        when(leaderboardRepository.getRecentLeaderboardMetadata()).thenReturn(mockLeaderboard);

        List<UserWithScore> mockUsers = createMockUsersWithMixedScores();
        when(leaderboardRepository.getLeaderboardUsersById(eq("leaderboard-id"), any(LeaderboardFilterOptions.class)))
                .thenReturn(mockUsers);

        when(serverUrlUtils.getUrl()).thenReturn("http://localhost:3000");
    }

    private void setupMockLeaderboardDataWithAllZeroScoreUsers() {
        Leaderboard mockLeaderboard = mock(Leaderboard.class);
        when(mockLeaderboard.getId()).thenReturn("leaderboard-id");
        when(mockLeaderboard.getName()).thenReturn("Test Leaderboard");

        when(leaderboardRepository.getRecentLeaderboardMetadata()).thenReturn(mockLeaderboard);

        List<UserWithScore> mockUsers = createMockUsersAllZeroScores();
        when(leaderboardRepository.getLeaderboardUsersById(eq("leaderboard-id"), any(LeaderboardFilterOptions.class)))
                .thenReturn(mockUsers);

        when(serverUrlUtils.getUrl()).thenReturn("http://localhost:3000");
    }

    private void setupMockLeaderboardDataWithExpirationAndMixedScoreUsers() {
        setupMockLeaderboardDataWithMixedScoreUsers();

        Leaderboard mockLeaderboard = mock(Leaderboard.class);
        when(mockLeaderboard.getId()).thenReturn("leaderboard-id");
        when(mockLeaderboard.getName()).thenReturn("Test Leaderboard");
        when(mockLeaderboard.getShouldExpireBy()).thenReturn(LocalDateTime.now().plusDays(7));

        when(leaderboardRepository.getRecentLeaderboardMetadata()).thenReturn(mockLeaderboard);
    }

    private void setupMockLeaderboardDataWithExpirationAndAllZeroScoreUsers() {
        setupMockLeaderboardDataWithAllZeroScoreUsers();

        Leaderboard mockLeaderboard = mock(Leaderboard.class);
        when(mockLeaderboard.getId()).thenReturn("leaderboard-id");
        when(mockLeaderboard.getName()).thenReturn("Test Leaderboard");
        when(mockLeaderboard.getShouldExpireBy()).thenReturn(LocalDateTime.now().plusDays(7));

        when(leaderboardRepository.getRecentLeaderboardMetadata()).thenReturn(mockLeaderboard);
    }

    private List<UserWithScore> createMockUsersOneUser() {
        UserWithScore user1 = mock(UserWithScore.class);
        when(user1.getDiscordId()).thenReturn("discord1");
        when(user1.getTotalScore()).thenReturn(100);

        return Arrays.asList(user1);
    }

    private List<UserWithScore> createMockUsersTwoUsers() {
        UserWithScore user1 = mock(UserWithScore.class);
        when(user1.getDiscordId()).thenReturn("discord1");
        when(user1.getTotalScore()).thenReturn(100);

        UserWithScore user2 = mock(UserWithScore.class);
        when(user2.getDiscordId()).thenReturn("discord2");
        when(user2.getTotalScore()).thenReturn(80);

        return Arrays.asList(user1, user2);
    }

    private void setupMockLeaderboardDataWithOneUser() {
        Leaderboard mockLeaderboard = mock(Leaderboard.class);
        when(mockLeaderboard.getId()).thenReturn("leaderboard-id");
        when(mockLeaderboard.getName()).thenReturn("Test Leaderboard");

        when(leaderboardRepository.getRecentLeaderboardMetadata()).thenReturn(mockLeaderboard);

        List<UserWithScore> mockUsers = createMockUsersOneUser();
        when(leaderboardRepository.getLeaderboardUsersById(eq("leaderboard-id"), any(LeaderboardFilterOptions.class)))
                .thenReturn(mockUsers);

        when(serverUrlUtils.getUrl()).thenReturn("http://localhost:3000");
    }

    private void setupMockLeaderboardDataWithTwoUsers() {
        Leaderboard mockLeaderboard = mock(Leaderboard.class);
        when(mockLeaderboard.getId()).thenReturn("leaderboard-id");
        when(mockLeaderboard.getName()).thenReturn("Test Leaderboard");

        when(leaderboardRepository.getRecentLeaderboardMetadata()).thenReturn(mockLeaderboard);

        List<UserWithScore> mockUsers = createMockUsersTwoUsers();
        when(leaderboardRepository.getLeaderboardUsersById(eq("leaderboard-id"), any(LeaderboardFilterOptions.class)))
                .thenReturn(mockUsers);

        when(serverUrlUtils.getUrl()).thenReturn("http://localhost:3000");
    }

    private void setupMockLeaderboardDataWithExpirationAndOneUser() {
        setupMockLeaderboardDataWithOneUser();

        Leaderboard mockLeaderboard = mock(Leaderboard.class);
        when(mockLeaderboard.getId()).thenReturn("leaderboard-id");
        when(mockLeaderboard.getName()).thenReturn("Test Leaderboard");
        when(mockLeaderboard.getShouldExpireBy()).thenReturn(LocalDateTime.now().plusDays(7));

        when(leaderboardRepository.getRecentLeaderboardMetadata()).thenReturn(mockLeaderboard);
    }

    private void setupMockLeaderboardDataWithExpirationAndTwoUsers() {
        setupMockLeaderboardDataWithTwoUsers();

        Leaderboard mockLeaderboard = mock(Leaderboard.class);
        when(mockLeaderboard.getId()).thenReturn("leaderboard-id");
        when(mockLeaderboard.getName()).thenReturn("Test Leaderboard");
        when(mockLeaderboard.getShouldExpireBy()).thenReturn(LocalDateTime.now().plusDays(7));

        when(leaderboardRepository.getRecentLeaderboardMetadata()).thenReturn(mockLeaderboard);
    }
}
