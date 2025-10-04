package com.patina.codebloom.api.admin.helper;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;

import org.springframework.stereotype.Service;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Browser.NewContextOptions;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.ScreenshotType;
import com.patina.codebloom.common.db.models.leaderboard.Leaderboard;
import com.patina.codebloom.common.db.models.user.UserWithScore;
import com.patina.codebloom.common.db.repos.leaderboard.LeaderboardRepository;
import com.patina.codebloom.common.db.repos.leaderboard.options.LeaderboardFilterOptions;
import com.patina.codebloom.common.time.StandardizedLocalDateTime;
import com.patina.codebloom.jda.client.JDAClient;
import com.patina.codebloom.jda.client.options.EmbeddedImagesMessageOptions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PatinaDiscordMessageHelper {
    private final LeaderboardRepository leaderboardRepository;
    private final JDAClient jdaClient;

    public PatinaDiscordMessageHelper(final LeaderboardRepository leaderboardRepository, final JDAClient jdaClient) {
        this.leaderboardRepository = leaderboardRepository;
        this.jdaClient = jdaClient;
    }

    /**
     * Loads the Patina page, takes a screenshot, and sends a final Discord message
     * with the winners of the leaderboard.
     */
    public void sendLatestLeaderboardDiscordMessage() {
        log.info("Connecting to JDA client...");
        jdaClient.connect();
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.firefox().launch(new BrowserType.LaunchOptions().setHeadless(true).setTimeout(40000));
            BrowserContext context = browser.newContext(new NewContextOptions()
                            .setUserAgent("Mozilla/5.0 (Linux; U; Android 4.4.1; SAMSUNG SM-J210G Build/KTU84P) AppleWebKit/536.31 (KHTML, like Gecko) Chrome/48.0.2090.359 Mobile Safari/601.9")
                            .setStorageState(null));
            context.clearCookies();
            Page page = browser.newPage();

            List<byte[]> screenshotBytesList = new ArrayList<>();

            log.info("Loading page 1 for screenshot...");
            page.navigate("https://codebloom.patinanetwork.org/leaderboard?patina=true");
            page.waitForTimeout(5_000);
            byte[] screenshot1 = page.screenshot(new Page.ScreenshotOptions().setType(ScreenshotType.PNG).setFullPage(true));
            screenshotBytesList.add(screenshot1);

            log.info("Loading page 2 for screenshot...");
            page.navigate("https://codebloom.patinanetwork.org/leaderboard?patina=true&page=2");
            page.waitForTimeout(5_000);
            byte[] screenshot2 = page.screenshot(new Page.ScreenshotOptions().setType(ScreenshotType.PNG).setFullPage(true));
            screenshotBytesList.add(screenshot2);

            log.info("Merging screenshots...");
            BufferedImage img1 = ImageIO.read(new ByteArrayInputStream(screenshot1));
            BufferedImage img2 = ImageIO.read(new ByteArrayInputStream(screenshot2));

            int combinedHeight = img1.getHeight() + img2.getHeight();
            int width = img1.getWidth();
            BufferedImage combined = new BufferedImage(width, combinedHeight, BufferedImage.TYPE_INT_RGB);

            Graphics2D g = combined.createGraphics();
            g.drawImage(img1, 0, 0, null);
            g.drawImage(img2, 0, img1.getHeight(), null);
            g.dispose();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(combined, "png", baos);
            byte[] mergedScreenshot = baos.toByteArray();


            LeaderboardFilterOptions options = LeaderboardFilterOptions.builder()
                            .page(1)
                            .pageSize(5)
                            .query("")
                            .patina(true)
                            .build();

            List<UserWithScore> users = leaderboardRepository.getRecentLeaderboardUsers(options);

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
                            CONGRATS ON THE WINNERS FROM THIS LEADERBOARD!

                            🥇- <@%s> - %s pts
                            🥈- <@%s> - %s pts
                            🥉- <@%s> - %s pts

                            To view the rest of the members, visit the website or check out the image embedded in this message!

                            The new leaderboard just started, so best of luck to everyone!

                            Happy LeetCoding,
                            Codebloom
                            <https://codebloom.patinanetwork.org>
                            """,
                            users.get(0).getDiscordId(),
                            users.get(0).getTotalScore(),
                            users.get(1).getDiscordId(),
                            users.get(1).getTotalScore(),
                            users.get(2).getDiscordId(),
                            users.get(2).getTotalScore());

            jdaClient.sendEmbedWithImages(
                            EmbeddedImagesMessageOptions.builder()
                                            .guildId(jdaClient.getJdaPatinaProperties().getGuildId())
                                            .channelId(jdaClient.getJdaPatinaProperties().getLeetcodeChannelId())
                                            .description(description)
                                            .title(title)
                                            .footerText("Codebloom - LeetCode Leaderboard for Patina Network")
                                            .footerIcon("https://codebloom.patinanetwork.org/favicon.ico")
                                            .color(new Color(69, 129, 103))
                                            .filesBytes(List.of(mergedScreenshot))
                                            .fileNames(List.of("leaderboard.png"))
                                            .build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
