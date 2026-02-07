package org.patinanetwork.codebloom.common.components;

import java.awt.Color;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.patinanetwork.codebloom.common.db.models.discord.DiscordClub;
import org.patinanetwork.codebloom.common.db.models.discord.DiscordClubMetadata;
import org.patinanetwork.codebloom.common.db.models.leaderboard.Leaderboard;
import org.patinanetwork.codebloom.common.db.models.user.UserWithScore;
import org.patinanetwork.codebloom.common.db.repos.discord.club.DiscordClubRepository;
import org.patinanetwork.codebloom.common.db.repos.leaderboard.LeaderboardRepository;
import org.patinanetwork.codebloom.common.db.repos.leaderboard.options.LeaderboardFilterGenerator;
import org.patinanetwork.codebloom.common.db.repos.leaderboard.options.LeaderboardFilterOptions;
import org.patinanetwork.codebloom.common.time.StandardizedLocalDateTime;
import org.patinanetwork.codebloom.common.url.ServerUrlUtils;
import org.patinanetwork.codebloom.common.utils.leaderboard.LeaderboardUtils;
import org.patinanetwork.codebloom.jda.client.JDAClient;
import org.patinanetwork.codebloom.jda.client.options.EmbeddedImagesMessageOptions;
import org.patinanetwork.codebloom.playwright.PlaywrightClient;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DiscordClubManager {

    private static final int FRONTEND_PAGE_SIZE = 20;

    private final JDAClient jdaClient;
    private final LeaderboardRepository leaderboardRepository;
    private final DiscordClubRepository discordClubRepository;
    private final ServerUrlUtils serverUrlUtils;

    public DiscordClubManager(
            final ServerUrlUtils serverUrlUtils,
            final JDAClient jdaClient,
            final LeaderboardRepository leaderboardRepository,
            final DiscordClubRepository discordClubRepository,
            final PlaywrightClient playwrightClient) {
        this.serverUrlUtils = serverUrlUtils;
        this.jdaClient = jdaClient;
        this.leaderboardRepository = leaderboardRepository;
        this.discordClubRepository = discordClubRepository;
    }

    private static final String[] MEDAL_EMOJIS = {"🥇", "🥈", "🥉"};

    private String buildTopUsersSection(final List<UserWithScore> users, final boolean isFinal) {
        if (users.isEmpty()) {
            if (isFinal) {
                return "Keep solving problems and climb your way to the top of the next one!";
            }
            return "There are no scores yet, but the leaderboard is still open. Get in there and start solving!";
        }

        var sb = new StringBuilder();
        for (int i = 0; i < Math.min(users.size(), 3); i++) {
            if (i > 0) {
                sb.append("\n");
            }
            sb.append("%s- <@%s> - %s pts"
                    .formatted(
                            MEDAL_EMOJIS[i],
                            users.get(i).getDiscordId(),
                            users.get(i).getTotalScore()));
        }
        return sb.toString();
    }

    /**
     * Loads the page for each discord club's tag, takes a screenshot, and sends a final Discord message with the
     * winners of the leaderboard.
     *
     * <p>CANNOT BE ASYNC, WILL BREAK THE FLOW.
     *
     * <p>TODO:
     * https://codebloom.notion.site/Refactor-create-new-leaderboard-to-load-new-leaderboard-first-then-take-screenshots-of-the-old-page-2a87c85563aa809a9ca6dc23a31b0ab2?pvs=74
     */
    private void sendLeaderboardCompletedDiscordMessage(final DiscordClub club) {
        log.info("Connecting to JDA client...");
        jdaClient.connect();
        try {
            var latestLeaderboard = leaderboardRepository.getRecentLeaderboardMetadata();

            LeaderboardFilterOptions options = LeaderboardFilterGenerator.builderWithTag(club.getTag())
                    .page(1)
                    .pageSize(5)
                    .build();

            List<UserWithScore> users = LeaderboardUtils.filterUsersWithPoints(
                    leaderboardRepository.getLeaderboardUsersById(latestLeaderboard.getId(), options));

            Leaderboard currentLeaderboard = leaderboardRepository.getRecentLeaderboardMetadata();

            String topUsersSection = buildTopUsersSection(users, true);
            String headerText = users.isEmpty()
                    ? "No one claimed a spot on this leaderboard. "
                    : "CONGRATS TO THE WINNERS FROM THIS LEADERBOARD!";

            String description = String.format(
                    """
                Dear %s users,

                %s

                %s

                To view the rest of the members, visit the website or check out the image embedded in this message!

                The new leaderboard just started, so best of luck to everyone!

                View the full leaderboard for %s users at %s/leaderboard/%s?%s=true

                Happy LeetCoding,
                Codebloom
                <%s>
                """,
                    club.getName(),
                    headerText,
                    topUsersSection,
                    club.getName(),
                    serverUrlUtils.getUrl(),
                    currentLeaderboard.getId(),
                    club.getTag().name().toLowerCase(),
                    serverUrlUtils.getUrl());

            var guildId = club.getDiscordClubMetadata().flatMap(DiscordClubMetadata::getGuildId);
            var channelId = club.getDiscordClubMetadata().flatMap(DiscordClubMetadata::getLeaderboardChannelId);

            if (guildId.isEmpty() || channelId.isEmpty()) {
                log.error("club {} is skipped because of missing metadata", club.getName());
                return;
            }

            jdaClient.sendEmbedWithImages(EmbeddedImagesMessageOptions.builder()
                    .guildId(Long.valueOf(guildId.get()))
                    .channelId(Long.valueOf(channelId.get()))
                    .description(description)
                    .title("🏆🏆🏆 %s - Final Leaderboard Score for %s"
                            .formatted(currentLeaderboard.getName(), club.getName()))
                    .footerText("Codebloom - LeetCode Leaderboard for %s".formatted(club.getName()))
                    .footerIcon("%s/favicon.ico".formatted(serverUrlUtils.getUrl()))
                    .color(new Color(69, 129, 103))
                    .build());
        } catch (Exception e) {
            log.error("Exception thrown in DiscordClubManager", e);
        }
    }

    private void sendWeeklyLeaderboardUpdateDiscordMessage(final DiscordClub club) {
        log.info("Connecting to JDA client...");
        jdaClient.connect();
        try {
            var latestLeaderboard = leaderboardRepository.getRecentLeaderboardMetadata();

            LeaderboardFilterOptions options = LeaderboardFilterGenerator.builderWithTag(club.getTag())
                    .page(1)
                    .pageSize(5)
                    .build();

            List<UserWithScore> users = LeaderboardUtils.filterUsersWithPoints(
                    leaderboardRepository.getLeaderboardUsersById(latestLeaderboard.getId(), options));

            Leaderboard currentLeaderboard = leaderboardRepository.getRecentLeaderboardMetadata();

            LocalDateTime shouldExpireByTime = Optional.ofNullable(currentLeaderboard.getShouldExpireBy())
                    // this orElse will only trigger if leaderboard doesn't have expiration time.
                    .orElse(StandardizedLocalDateTime.now());

            Duration remaining = Duration.between(StandardizedLocalDateTime.now(), shouldExpireByTime);

            long daysLeft = remaining.toDays();
            long hoursLeft = remaining.toHours() % 24;
            long minutesLeft = remaining.toMinutes() % 60;

            String topUsersSection = buildTopUsersSection(users, false);
            String headerText = "Here is a weekly update on the LeetCode leaderboard for our very own members!";

            String description = String.format(
                    """
                Dear %s users,

                %s

                %s

                To view the rest of the members, visit the website or check out the image embedded in this message!

                Just as a reminder, there's %d day(s), %d hour(s), and %d minute(s) left until the leaderboard closes, so keep grinding!

                View the full leaderboard for %s users at %s/leaderboard?%s=true


                See you next week!

                Beep boop,
                Codebloom
                <%s>
                """,
                    club.getName(),
                    headerText,
                    topUsersSection,
                    daysLeft,
                    hoursLeft,
                    minutesLeft,
                    club.getName(),
                    serverUrlUtils.getUrl(),
                    club.getTag().name().toLowerCase(),
                    serverUrlUtils.getUrl());

            var guildId = club.getDiscordClubMetadata().flatMap(DiscordClubMetadata::getGuildId);
            var channelId = club.getDiscordClubMetadata().flatMap(DiscordClubMetadata::getLeaderboardChannelId);

            if (guildId.isEmpty() || channelId.isEmpty()) {
                log.error("club {} is skipped because of missing metadata", club.getName());
                return;
            }

            jdaClient.sendEmbedWithImages(EmbeddedImagesMessageOptions.builder()
                    .guildId(Long.valueOf(guildId.get()))
                    .channelId(Long.valueOf(channelId.get()))
                    .description(description)
                    .title("%s - Weekly Leaderboard Update for %s"
                            .formatted(currentLeaderboard.getName(), club.getName()))
                    .footerText("Codebloom - LeetCode Leaderboard for %s".formatted(club.getName()))
                    .footerIcon("%s/favicon.ico".formatted(serverUrlUtils.getUrl()))
                    .color(new Color(69, 129, 103))
                    .build());
        } catch (Exception e) {
            log.error("Error in DiscordClubManager when sending weekly leaderboard", e);
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

    public void sendWeeklyLeaderboardUpdateDiscordMessageForClub(String guildId) {
        System.out.println("working");
        DiscordClub club = discordClubRepository.getDiscordClubByGuildId(guildId).get();
        var latestLeaderboard = leaderboardRepository.getRecentLeaderboardMetadata();

            LeaderboardFilterOptions options = LeaderboardFilterGenerator.builderWithTag(club.getTag())
                    .page(1)
                    .pageSize(5)
                    .build();

            List<UserWithScore> users = LeaderboardUtils.filterUsersWithPoints(
                    leaderboardRepository.getLeaderboardUsersById(latestLeaderboard.getId(), options));

            Leaderboard currentLeaderboard = leaderboardRepository.getRecentLeaderboardMetadata();

            LocalDateTime shouldExpireByTime = Optional.ofNullable(currentLeaderboard.getShouldExpireBy())
                        .orElse(StandardizedLocalDateTime.now());

            Duration remaining = Duration.between(StandardizedLocalDateTime.now(), shouldExpireByTime);

            long daysLeft = remaining.toDays();
            long hoursLeft = remaining.toHours() % 24;
            long minutesLeft = remaining.toMinutes() % 60;

            String description = String.format(
                    """
                Dear %s users,

                Here is a weekly update on the LeetCode leaderboard for our very own members!

                🥇- <@%s> - %s pts
                🥈- <@%s> - %s pts
                🥉- <@%s> - %s pts

                To view the rest of the members, visit the website or check out the image embedded in this message!

                Just as a reminder, there's %d day(s), %d hour(s), and %d minute(s) left until the leaderboard closes, so keep grinding!

                View the full leaderboard for %s users at https://codebloom.patinanetwork.org/leaderboard?%s=true


                See you next week!

                Beep boop,
                Codebloom
                <%s>
                """,
                    club.getName(),
                    getUser(users, 0).map(UserWithScore::getDiscordId).orElse("N/A"),
                    getUser(users, 0)
                            .map(UserWithScore::getTotalScore)
                            .map(String::valueOf)
                            .orElse("N/A"),
                    getUser(users, 1).map(UserWithScore::getDiscordId).orElse("N/A"),
                    getUser(users, 1)
                            .map(UserWithScore::getTotalScore)
                            .map(String::valueOf)
                            .orElse("N/A"),
                    getUser(users, 2).map(UserWithScore::getDiscordId).orElse("N/A"),
                    getUser(users, 2)
                            .map(UserWithScore::getTotalScore)
                            .map(String::valueOf)
                            .orElse("N/A"),
                    daysLeft,
                    hoursLeft,
                    minutesLeft,
                    club.getName(),
                    club.getTag().name().toLowerCase(),
                    serverUrlUtils.getUrl());

            var channelId = club.getDiscordClubMetadata().flatMap(DiscordClubMetadata::getLeaderboardChannelId);

            if (guildId.isEmpty() || channelId.isEmpty()) {
                log.error("club {} is skipped because of missing metadata", club.getName());
                return;
            }

            jdaClient.sendEmbedWithImages(EmbeddedImagesMessageOptions.builder()
                    .guildId(Long.valueOf(guildId))
                    .channelId(Long.valueOf(channelId.get()))
                    .description(description)
                    .title("%s - Weekly Leaderboard Update for %s"
                            .formatted(currentLeaderboard.getName(), club.getName()))
                    .footerText("Codebloom - LeetCode Leaderboard for %s".formatted(club.getName()))
                    .footerIcon("%s/favicon.ico".formatted(serverUrlUtils.getUrl()))
                    .color(new Color(69, 129, 103))
                    .build());
    }
}
