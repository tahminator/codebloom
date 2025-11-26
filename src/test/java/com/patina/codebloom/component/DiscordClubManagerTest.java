package com.patina.codebloom.component;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.patina.codebloom.common.components.DiscordClubManager;
import com.patina.codebloom.common.db.models.discord.DiscordClub;
import com.patina.codebloom.common.db.models.discord.DiscordClubMetadata;
import com.patina.codebloom.common.db.models.leaderboard.Leaderboard;
import com.patina.codebloom.common.db.models.user.UserWithScore;
import com.patina.codebloom.common.db.models.usertag.Tag;
import com.patina.codebloom.common.db.repos.discord.club.DiscordClubRepository;
import com.patina.codebloom.common.db.repos.leaderboard.LeaderboardRepository;
import com.patina.codebloom.common.db.repos.leaderboard.options.LeaderboardFilterOptions;
import com.patina.codebloom.common.url.ServerUrlUtils;
import com.patina.codebloom.jda.client.JDAClient;
import com.patina.codebloom.jda.client.options.EmbeddedImagesMessageOptions;
import com.patina.codebloom.playwright.PlaywrightClient;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

public class DiscordClubManagerTest {

    private JDAClient jdaClient = mock(JDAClient.class);

    private LeaderboardRepository leaderboardRepository = mock(
        LeaderboardRepository.class
    );

    private DiscordClubRepository discordClubRepository = mock(
        DiscordClubRepository.class
    );

    private PlaywrightClient playwrightClient = mock(PlaywrightClient.class);

    private ServerUrlUtils serverUrlUtils = mock(ServerUrlUtils.class);

    private DiscordClubManager discordClubManager;

    @BeforeEach
    void setUp() {
        discordClubManager = new DiscordClubManager(
            serverUrlUtils,
            jdaClient,
            leaderboardRepository,
            discordClubRepository,
            playwrightClient
        );
    }

    @Test
    void testSendLeaderboardCompletedDiscordMessageToAllClubsSuccess() {
        DiscordClub mockClub = createMockDiscordClub("Test Club", Tag.Rpi);
        when(discordClubRepository.getAllActiveDiscordClubs()).thenReturn(
            Arrays.asList(mockClub)
        );

        setupMockLeaderboardData();

        discordClubManager.sendLeaderboardCompletedDiscordMessageToAllClubs();

        verify(discordClubRepository).getAllActiveDiscordClubs();
        verify(jdaClient).connect();
        verify(jdaClient).sendEmbedWithImages(
            any(EmbeddedImagesMessageOptions.class)
        );
    }

    @Test
    void testSendLeaderboardCompletedDiscordMessageToAllClubsEmptyClubList() {
        when(discordClubRepository.getAllActiveDiscordClubs()).thenReturn(
            Collections.emptyList()
        );

        discordClubManager.sendLeaderboardCompletedDiscordMessageToAllClubs();

        verify(discordClubRepository).getAllActiveDiscordClubs();
        verify(jdaClient, never()).connect();
        verify(jdaClient, never()).sendEmbedWithImages(any());
    }

    @Test
    void testSendLeaderboardCompletedDiscordMessageToAllClubsMultipleClubs() {
        DiscordClub club1 = createMockDiscordClub("Club 1", Tag.Rpi);
        DiscordClub club2 = createMockDiscordClub("Club 2", Tag.Baruch);
        when(discordClubRepository.getAllActiveDiscordClubs()).thenReturn(
            Arrays.asList(club1, club2)
        );

        setupMockLeaderboardData();

        discordClubManager.sendLeaderboardCompletedDiscordMessageToAllClubs();

        verify(discordClubRepository).getAllActiveDiscordClubs();
        verify(jdaClient, times(2)).connect();
        verify(jdaClient, times(2)).sendEmbedWithImages(
            any(EmbeddedImagesMessageOptions.class)
        );
    }

    @Test
    void testSendLeaderboardCompletedDiscordMessageMissingGuildId() {
        DiscordClub mockClub = createMockDiscordClubWithoutMetadata(
            "Test Club",
            Tag.Rpi
        );
        when(discordClubRepository.getAllActiveDiscordClubs()).thenReturn(
            Arrays.asList(mockClub)
        );

        setupMockLeaderboardData();

        discordClubManager.sendLeaderboardCompletedDiscordMessageToAllClubs();

        verify(jdaClient).connect();
        verify(jdaClient, never()).sendEmbedWithImages(any());
    }

    @Test
    void testSendWeeklyLeaderboardUpdateDiscordMessageToAllClubsSuccess() {
        DiscordClub mockClub = createMockDiscordClub("Test Club", Tag.Rpi);
        when(discordClubRepository.getAllActiveDiscordClubs()).thenReturn(
            Arrays.asList(mockClub)
        );

        setupMockLeaderboardDataWithExpiration();

        discordClubManager.sendWeeklyLeaderboardUpdateDiscordMessageToAllClubs();

        verify(discordClubRepository).getAllActiveDiscordClubs();
        verify(jdaClient).connect();
        verify(jdaClient).sendEmbedWithImages(
            any(EmbeddedImagesMessageOptions.class)
        );
    }

    @Test
    void testSendWeeklyLeaderboardUpdateDiscordMessageToAllClubsEmptyClubList() {
        when(discordClubRepository.getAllActiveDiscordClubs()).thenReturn(
            Collections.emptyList()
        );

        discordClubManager.sendWeeklyLeaderboardUpdateDiscordMessageToAllClubs();

        verify(discordClubRepository).getAllActiveDiscordClubs();
        verify(jdaClient, never()).connect();
        verify(jdaClient, never()).sendEmbedWithImages(any());
    }

    @Test
    void testSendWeeklyLeaderboardUpdateDiscordMessageMissingChannelId() {
        DiscordClub mockClub = createMockDiscordClubWithPartialMetadata(
            "Test Club",
            Tag.Rpi
        );
        when(discordClubRepository.getAllActiveDiscordClubs()).thenReturn(
            Arrays.asList(mockClub)
        );

        setupMockLeaderboardDataWithExpiration();

        discordClubManager.sendWeeklyLeaderboardUpdateDiscordMessageToAllClubs();

        verify(jdaClient).connect();
        verify(jdaClient, never()).sendEmbedWithImages(any());
    }

    @Test
    void testSendWeeklyLeaderboardUpdateWithoutExpiration() {
        DiscordClub mockClub = createMockDiscordClub("Test Club", Tag.Rpi);
        when(discordClubRepository.getAllActiveDiscordClubs()).thenReturn(
            Arrays.asList(mockClub)
        );

        setupMockLeaderboardDataWithoutExpiration();

        discordClubManager.sendWeeklyLeaderboardUpdateDiscordMessageToAllClubs();

        verify(discordClubRepository).getAllActiveDiscordClubs();
        verify(jdaClient).connect();
        verify(jdaClient).sendEmbedWithImages(
            any(EmbeddedImagesMessageOptions.class)
        );
    }

    private DiscordClub createMockDiscordClub(
        final String name,
        final Tag tag
    ) {
        DiscordClubMetadata metadata = mock(DiscordClubMetadata.class);
        when(metadata.getGuildId()).thenReturn(Optional.of("123456789"));
        when(metadata.getLeaderboardChannelId()).thenReturn(
            Optional.of("987654321")
        );

        DiscordClub club = mock(DiscordClub.class);
        when(club.getName()).thenReturn(name);
        when(club.getTag()).thenReturn(tag);
        when(club.getDiscordClubMetadata()).thenReturn(Optional.of(metadata));

        return club;
    }

    private DiscordClub createMockDiscordClubWithoutMetadata(
        final String name,
        final Tag tag
    ) {
        DiscordClub club = mock(DiscordClub.class);
        when(club.getName()).thenReturn(name);
        when(club.getTag()).thenReturn(tag);
        when(club.getDiscordClubMetadata()).thenReturn(Optional.empty());

        return club;
    }

    private DiscordClub createMockDiscordClubWithPartialMetadata(
        final String name,
        final Tag tag
    ) {
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

        when(leaderboardRepository.getRecentLeaderboardMetadata()).thenReturn(
            mockLeaderboard
        );
        when(
            leaderboardRepository.getLeaderboardUserCountById(
                eq("leaderboard-id"),
                any()
            )
        ).thenReturn(25);

        List<UserWithScore> mockUsers = createMockUsers();
        when(
            leaderboardRepository.getLeaderboardUsersById(
                eq("leaderboard-id"),
                any(LeaderboardFilterOptions.class)
            )
        ).thenReturn(mockUsers);

        when(
            playwrightClient.getCodebloomLeaderboardScreenshot(
                anyInt(),
                any(Tag.class)
            )
        ).thenReturn("mock-screenshot".getBytes());

        when(serverUrlUtils.getUrl()).thenReturn("http://localhost:3000");
    }

    private void setupMockLeaderboardDataWithExpiration() {
        setupMockLeaderboardData();

        Leaderboard mockLeaderboard = mock(Leaderboard.class);
        when(mockLeaderboard.getId()).thenReturn("leaderboard-id");
        when(mockLeaderboard.getName()).thenReturn("Test Leaderboard");
        when(mockLeaderboard.getShouldExpireBy()).thenReturn(
            LocalDateTime.now().plusDays(7)
        );

        when(leaderboardRepository.getRecentLeaderboardMetadata()).thenReturn(
            mockLeaderboard
        );
    }

    private void setupMockLeaderboardDataWithoutExpiration() {
        setupMockLeaderboardData();

        Leaderboard mockLeaderboard = mock(Leaderboard.class);
        when(mockLeaderboard.getId()).thenReturn("leaderboard-id");
        when(mockLeaderboard.getName()).thenReturn("Test Leaderboard");
        when(mockLeaderboard.getShouldExpireBy()).thenReturn(null);

        when(leaderboardRepository.getRecentLeaderboardMetadata()).thenReturn(
            mockLeaderboard
        );
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
}
