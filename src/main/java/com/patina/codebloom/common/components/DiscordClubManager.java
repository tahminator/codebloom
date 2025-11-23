package com.patina.codebloom.common.components;

import java.awt.Color;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.patina.codebloom.common.db.models.discord.DiscordClub;
import com.patina.codebloom.common.db.models.discord.DiscordClubMetadata;
import com.patina.codebloom.common.db.models.leaderboard.Leaderboard;
import com.patina.codebloom.common.db.models.user.UserWithScore;
import com.patina.codebloom.common.db.repos.discord.club.DiscordClubRepository;
import com.patina.codebloom.common.db.repos.leaderboard.LeaderboardRepository;
import com.patina.codebloom.common.db.repos.leaderboard.options.LeaderboardFilterGenerator;
import com.patina.codebloom.common.db.repos.leaderboard.options.LeaderboardFilterOptions;
import com.patina.codebloom.common.time.StandardizedLocalDateTime;
import com.patina.codebloom.common.url.ServerUrlUtils;
import com.patina.codebloom.common.utils.pair.Pair;
import com.patina.codebloom.jda.client.JDAClient;
import com.patina.codebloom.jda.client.options.EmbeddedImagesMessageOptions;
import com.patina.codebloom.playwright.PlaywrightClient;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class DiscordClubManager {
    private static final int FRONTEND_PAGE_SIZE = 20;

    private final JDAClient jdaClient;
    private final LeaderboardRepository leaderboardRepository;
    private final DiscordClubRepository discordClubRepository;
    private final PlaywrightClient playwrightClient;
    private final ServerUrlUtils serverUrlUtils;

    public DiscordClubManager(final ServerUrlUtils serverUrlUtils, final JDAClient jdaClient, final LeaderboardRepository leaderboardRepository, final DiscordClubRepository discordClubRepository,
                    final PlaywrightClient playwrightClient) {
        this.serverUrlUtils = serverUrlUtils;
        this.jdaClient = jdaClient;
        this.leaderboardRepository = leaderboardRepository;
        this.discordClubRepository = discordClubRepository;
        this.playwrightClient = playwrightClient;
    }

    private Optional<UserWithScore> getUser(final List<UserWithScore> users, final int index) {
        if (index < 0 || index >= users.size()) {
            return Optional.empty();
        }

        return Optional.ofNullable(users.get(index));
    }

    private List<Pair<String, byte[]>> getScreenshotsForRecentLeaderboard(final Leaderboard leaderboard, final DiscordClub club) {
        List<Pair<String, byte[]>> screenshots = new ArrayList<>();

        var totalUsers = leaderboardRepository.getLeaderboardUserCountById(leaderboard.getId(), LeaderboardFilterGenerator.builderWithTag(club.getTag()).build());
        int totalPages = (int) Math.ceil((double) totalUsers / FRONTEND_PAGE_SIZE);

        for (int p = 1; p <= totalPages; p++) {
            byte[] screenshot = playwrightClient.getCodebloomLeaderboardScreenshot(p, club.getTag());
            screenshots.add(Pair.of("leaderboard_page_%s.png".formatted(p), screenshot));
        }

        return screenshots;
    }

    /**
     * Loads the page for each discord club's tag, takes a screenshot, and sends a
     * final Discord message with the winners of the leaderboard.
     *
     * CANNOT BE ASYNC, WILL BREAK THE FLOW.
     *
     * TODO:
     * https://codebloom.notion.site/Refactor-create-new-leaderboard-to-load-new-leaderboard-first-then-take-screenshots-of-the-old-page-2a87c85563aa809a9ca6dc23a31b0ab2?pvs=74
     */
    private void sendLeaderboardCompletedDiscordMessage(final DiscordClub club) {
        log.info("Connecting to JDA client...");
        jdaClient.connect();
        try {
            var latestLeaderboard = leaderboardRepository.getRecentLeaderboardMetadata();
            var screenshots = getScreenshotsForRecentLeaderboard(latestLeaderboard, club);

            LeaderboardFilterOptions options = LeaderboardFilterGenerator.builderWithTag(club.getTag())
                            .page(1)
                            .pageSize(5)
                            .build();

            List<UserWithScore> users = leaderboardRepository.getLeaderboardUsersById(latestLeaderboard.getId(), options);

            Leaderboard currentLeaderboard = leaderboardRepository.getRecentLeaderboardMetadata();

            String description = String.format("""
                            Dear %s users,

                            CONGRATS ON THE WINNERS FROM THIS LEADERBOARD!

                            🥇- <@%s> - %s pts
                            🥈- <@%s> - %s pts
                            🥉- <@%s> - %s pts

                            To view the rest of the members, visit the website or check out the image embedded in this message!

                            The new leaderboard just started, so best of luck to everyone!

                            Happy LeetCoding,
                            Codebloom
                            <%s>
                            """,
                            club.getName(),
                            getUser(users, 0).map(UserWithScore::getDiscordId).orElse("N/A"),
                            getUser(users, 0).map(UserWithScore::getTotalScore).map(String::valueOf).orElse("N/A"),
                            getUser(users, 1).map(UserWithScore::getDiscordId).orElse("N/A"),
                            getUser(users, 1).map(UserWithScore::getTotalScore).map(String::valueOf).orElse("N/A"),
                            getUser(users, 2).map(UserWithScore::getDiscordId).orElse("N/A"),
                            getUser(users, 2).map(UserWithScore::getTotalScore).map(String::valueOf).orElse("N/A"),
                            serverUrlUtils.getUrl());

            var guildId = club.getDiscordClubMetadata().flatMap(DiscordClubMetadata::getGuildId);
            var channelId = club.getDiscordClubMetadata().flatMap(DiscordClubMetadata::getLeaderboardChannelId);

            if (guildId.isEmpty() || channelId.isEmpty()) {
                log.error("club {} is skipped because of missing metadata", club.getName());
                return;
            }

            jdaClient.sendEmbedWithImages(
                            EmbeddedImagesMessageOptions.builder()
                                            .guildId(Long.valueOf(guildId.get()))
                                            .channelId(Long.valueOf(channelId.get()))
                                            .description(description)

                                            .title("🏆🏆🏆 %s - Final Leaderboard Score for %s".formatted(currentLeaderboard.getName(), club.getName()))
                                            .footerText("Codebloom - LeetCode Leaderboard for %s".formatted(club.getName()))
                                            .footerIcon("%s/favicon.ico".formatted(serverUrlUtils.getUrl()))
                                            .color(new Color(69, 129, 103))
                                            .filesBytes(screenshots.stream().map(Pair::getRight).toList())
                                            .fileNames(screenshots.stream().map(Pair::getLeft).toList())
                                            .build());
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    private void sendWeeklyLeaderboardUpdateDiscordMessage(final DiscordClub club) {
        log.info("Connecting to JDA client...");
        jdaClient.connect();
        try {
            var latestLeaderboard = leaderboardRepository.getRecentLeaderboardMetadata();
            var screenshots = getScreenshotsForRecentLeaderboard(latestLeaderboard, club);

            LeaderboardFilterOptions options = LeaderboardFilterGenerator.builderWithTag(club.getTag())
                            .page(1)
                            .pageSize(5)
                            .build();

            List<UserWithScore> users = leaderboardRepository.getLeaderboardUsersById(latestLeaderboard.getId(), options);

            Leaderboard currentLeaderboard = leaderboardRepository.getRecentLeaderboardMetadata();

            LocalDateTime shouldExpireByTime = Optional.ofNullable(currentLeaderboard.getShouldExpireBy())
                            // this orElse will only trigger if leaderboard doesn't have expiration time.
                            .orElse(StandardizedLocalDateTime.now());

            Duration remaining = Duration.between(StandardizedLocalDateTime.now(), shouldExpireByTime);

            long daysLeft = remaining.toDays();
            long hoursLeft = remaining.toHours() % 24;
            long minutesLeft = remaining.toMinutes() % 60;

            String description = String.format("""
                            Dear %s users,

                            Here is a weekly update on the LeetCode leaderboard for our very own members!

                            🥇- <@%s> - %s pts
                            🥈- <@%s> - %s pts
                            🥉- <@%s> - %s pts

                            To view the rest of the members, visit the website or check out the image embedded in this message!

                            Just as a reminder, there's %d day(s), %d hour(s), and %d minute(s) left until the leaderboard closes, so keep grinding!


                            See you next week!

                            Beep boop,
                            Codebloom
                            <%s>
                            """,
                            club.getName(),
                            getUser(users, 0).map(UserWithScore::getDiscordId).orElse("N/A"),
                            getUser(users, 0).map(UserWithScore::getTotalScore).map(String::valueOf).orElse("N/A"),
                            getUser(users, 1).map(UserWithScore::getDiscordId).orElse("N/A"),
                            getUser(users, 1).map(UserWithScore::getTotalScore).map(String::valueOf).orElse("N/A"),
                            getUser(users, 2).map(UserWithScore::getDiscordId).orElse("N/A"),
                            getUser(users, 2).map(UserWithScore::getTotalScore).map(String::valueOf).orElse("N/A"),
                            daysLeft,
                            hoursLeft,
                            minutesLeft,
                            serverUrlUtils.getUrl());

            var guildId = club.getDiscordClubMetadata().flatMap(DiscordClubMetadata::getGuildId);
            var channelId = club.getDiscordClubMetadata().flatMap(DiscordClubMetadata::getLeaderboardChannelId);

            if (guildId.isEmpty() || channelId.isEmpty()) {
                log.error("club {} is skipped because of missing metadata", club.getName());
                return;
            }

            jdaClient.sendEmbedWithImages(
                            EmbeddedImagesMessageOptions.builder()
                                            .guildId(Long.valueOf(guildId.get()))
                                            .channelId(Long.valueOf(channelId.get()))
                                            .description(description)

                                            .title("%s - Weekly Leaderboard Update for %s".formatted(currentLeaderboard.getName(), club.getName()))
                                            .footerText("Codebloom - LeetCode Leaderboard for %s".formatted(club.getName()))
                                            .footerIcon("%s/favicon.ico".formatted(serverUrlUtils.getUrl()))
                                            .color(new Color(69, 129, 103))
                                            .filesBytes(screenshots.stream().map(Pair::getRight).toList())
                                            .fileNames(screenshots.stream().map(Pair::getLeft).toList())
                                            .build());
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void sendLeaderboardCompletedDiscordMessageToAllClubs() {
        var discordClubs = discordClubRepository.getAllActiveDiscordClubs();
        discordClubs.forEach(this::sendLeaderboardCompletedDiscordMessage);
    }

    public void sendWeeklyLeaderboardUpdateDiscordMessageToAllClubs() {
        var discordClubs = discordClubRepository.getAllActiveDiscordClubs();
        discordClubs.forEach(this::sendWeeklyLeaderboardUpdateDiscordMessage);
    }

}
