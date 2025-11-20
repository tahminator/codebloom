package com.patina.codebloom.api.admin.helper;

import java.awt.Color;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Browser.NewContextOptions;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.ScreenshotType;
import com.patina.codebloom.common.db.models.discord.DiscordClub;
import com.patina.codebloom.common.db.models.discord.DiscordClubMetadata;
import com.patina.codebloom.common.db.models.leaderboard.Leaderboard;
import com.patina.codebloom.common.db.models.user.UserWithScore;
import com.patina.codebloom.common.db.repos.discord.club.DiscordClubRepository;
import com.patina.codebloom.common.db.repos.leaderboard.LeaderboardRepository;
import com.patina.codebloom.common.db.repos.leaderboard.options.LeaderboardFilterOptions;
import com.patina.codebloom.common.time.StandardizedLocalDateTime;
import com.patina.codebloom.common.url.ServerUrlUtils;
import com.patina.codebloom.common.utils.pair.Pair;
import com.patina.codebloom.jda.client.JDAClient;
import com.patina.codebloom.jda.client.options.EmbeddedImagesMessageOptions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class LeaderboardDiscordMessageHelper {
    private static final int FRONTEND_PAGE_SIZE = 20;

    private final LeaderboardRepository leaderboardRepository;
    private final DiscordClubRepository discordClubRepository;
    private final JDAClient jdaClient;
    private final ServerUrlUtils serverUrlUtils;

    public LeaderboardDiscordMessageHelper(final LeaderboardRepository leaderboardRepository, final JDAClient jdaClient, final DiscordClubRepository discordClubRepository,
                    final ServerUrlUtils serverUrlUtils) {
        this.leaderboardRepository = leaderboardRepository;
        this.discordClubRepository = discordClubRepository;
        this.jdaClient = jdaClient;
        this.serverUrlUtils = serverUrlUtils;
    }

    public void sendLatestLeaderboardDiscordMessageToAllClubs() {
        var discordClubs = discordClubRepository.getAllActiveDiscordClubs();
        IO.println(discordClubs.size());
        discordClubs.forEach(this::sendLatestLeaderboardDiscordMessage);
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
    private void sendLatestLeaderboardDiscordMessage(final DiscordClub club) {
        log.info("Connecting to JDA client...");
        jdaClient.connect();
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.firefox().launch(new BrowserType.LaunchOptions().setHeadless(true).setTimeout(40000));
            BrowserContext context = browser.newContext(new NewContextOptions()
                            .setUserAgent("Mozilla/5.0 (Linux; U; Android 4.4.1; SAMSUNG SM-J210G Build/KTU84P) AppleWebKit/536.31 (KHTML, like Gecko) Chrome/48.0.2090.359 Mobile Safari/601.9")
                            .setStorageState(null));
            context.clearCookies();
            Page page = browser.newPage();

            List<Pair<String, byte[]>> screenshotBytesList = new ArrayList<>();

            var latestLeaderboard = leaderboardRepository.getRecentLeaderboardMetadata();

            var totalUsers = leaderboardRepository.getLeaderboardUserCountById(latestLeaderboard.getId(), LeaderboardFilterOptions.builderWithTag(club.getTag()).build());
            int totalPages = (int) Math.ceil((double) totalUsers / FRONTEND_PAGE_SIZE);

            for (int p = 1; p <= totalPages; p++) {
                log.info("Loading page {} for screenshot...", p);
                page.navigate("%s/leaderboard?%s=true&page=%s".formatted(
                                serverUrlUtils.getUrl(),
                                club.getTag().name().toLowerCase(),
                                p));
                page.waitForTimeout(5_000);
                byte[] screenshot = page.screenshot(new Page.ScreenshotOptions().setType(ScreenshotType.PNG).setFullPage(true));
                screenshotBytesList.add(Pair.of("leaderboard_page_%s.png".formatted(p), screenshot));
            }

            LeaderboardFilterOptions options = LeaderboardFilterOptions.builderWithTag(club.getTag())
                            .page(1)
                            .pageSize(5)
                            .query("")
                            .build();

            List<UserWithScore> users = leaderboardRepository.getLeaderboardUsersById(latestLeaderboard.getId(), options);

            browser.close();

            Leaderboard currentLeaderboard = leaderboardRepository.getRecentLeaderboardMetadata();
            LocalDateTime shouldExpireByTime = Optional.ofNullable(currentLeaderboard.getShouldExpireBy())
                            // this orElse will only trigger if leaderboard doesn't have expiration time.
                            .orElse(StandardizedLocalDateTime.now());

            Duration remaining = Duration.between(StandardizedLocalDateTime.now(), shouldExpireByTime);

            long daysLeft = remaining.toDays();
            long hoursLeft = remaining.toHours() % 24;
            long minutesLeft = remaining.toMinutes() % 60;

            String title = String.format("🏆🏆🏆 - %s is now complete!", currentLeaderboard.getName());

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
                            users.get(0).getDiscordId(),
                            users.get(0).getTotalScore(),
                            users.get(1).getDiscordId(),
                            users.get(1).getTotalScore(),
                            users.get(2).getDiscordId(),
                            users.get(2).getTotalScore(),
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
                                            .title(title)
                                            .title("%s Leaderboard - %s".formatted(club.getName(), currentLeaderboard.getName()))
                                            .footerText("Codebloom - LeetCode Leaderboard for %s".formatted(club.getName()))
                                            .footerIcon("%s/favicon.ico".formatted(serverUrlUtils.getUrl()))
                                            .color(new Color(69, 129, 103))
                                            .filesBytes(screenshotBytesList.stream().map(Pair::getRight).toList())
                                            .fileNames(screenshotBytesList.stream().map(Pair::getLeft).toList())
                                            .build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
