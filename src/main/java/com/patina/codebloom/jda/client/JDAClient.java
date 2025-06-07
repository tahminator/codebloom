package com.patina.codebloom.jda.client;

import java.awt.Color;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
import com.patina.codebloom.jda.JDAInitializer;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.utils.FileUpload;

/**
 * Use this client to interface with any required Discord bot logic.
 */
@Component
public class JDAClient {
    @Autowired
    private JDAInitializer jdaInitializer;
    @Autowired
    private LeaderboardRepository leaderboardRepository;
    private JDA jda;
    private static final Logger LOGGER = LoggerFactory.getLogger(JDAClient.class);

    JDAClient() {
    }

    private void isJdaReadyOrThrow() {
        if (jda == null) {
            throw new RuntimeException("You must call connect() first.");
        }

        try {
            jda.awaitReady();
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException("Something went wrong when awaiting JDA", e);
        }
    }

    public void connect() throws Exception {
        jda = jdaInitializer.jda();
    }

    public List<Guild> getGuilds() {
        isJdaReadyOrThrow();
        return jda.getGuilds();
    }

    /**
     * This is the recommended way to access Patina server's Discord ID.
     */
    public String getPatinaGuildId() {
        isJdaReadyOrThrow();
        return jdaInitializer.getJdaProperties().getId();
    }

    public long getPatinaLeetcodeChannelId() {
        isJdaReadyOrThrow();
        return jdaInitializer.getJdaProperties().getChannelIdAsLong();
    }

    public Guild getGuildById(final String guildId) {
        isJdaReadyOrThrow();
        return jda.getGuilds().stream().filter(g -> g.getId().equals(guildId)).findFirst().orElse(null);
    }

    public List<Member> getMemberListByGuildId(final String guildId) {
        isJdaReadyOrThrow();
        List<Guild> guilds = jda.getGuilds();

        Optional<Guild> optionalGuild = guilds.stream().filter(g -> g.getId().equals(guildId)).findFirst();

        if (optionalGuild.isEmpty()) {
            return List.of();
        }

        return optionalGuild.get().getMembers();
    }

    /**
     * Sends a screenshot of the Patina leaderboard (as well as tagging the top 3
     * Patina users) to a given guild and channel of your choosing.
     */
    public void sendLeaderboardMessage(final String guildId, final long channelId) {
        isJdaReadyOrThrow();
        Guild guild = getGuildById(guildId);
        if (guild == null) {
            LOGGER.error("Guild does not exist to send leaderboard message.");
            return;
        }
        TextChannel channel = guild.getTextChannelById(channelId);
        if (channel == null) {
            LOGGER.error("Channel does not exist on the given guild.");
            return;
        }

        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.firefox().launch(new BrowserType.LaunchOptions().setHeadless(true).setTimeout(40000));
            BrowserContext context = browser.newContext(new NewContextOptions()
                            .setUserAgent("Mozilla/5.0 (Linux; U; Android 4.4.1; SAMSUNG SM-J210G Build/KTU84P) AppleWebKit/536.31 (KHTML, like Gecko) Chrome/48.0.2090.359 Mobile Safari/601.9")
                            .setStorageState(null));
            context.clearCookies();
            Page page = browser.newPage();
            page.navigate("https://codebloom.patinanetwork.org/leaderboard?patina=true");
            LOGGER.info("Loading page for screenshot...");

            page.waitForTimeout(5_000);

            byte[] screenshotBytes = page.screenshot(
                            new Page.ScreenshotOptions().setType(ScreenshotType.PNG).setFullPage(true));

            List<UserWithScore> users = leaderboardRepository.getRecentLeaderboardUsers(1, 5, "", true);

            browser.close();

            Leaderboard currentLeaderboard = leaderboardRepository.getRecentLeaderboardMetadata();
            LocalDateTime shouldExpireByTime = currentLeaderboard.getShouldExpireBy();

            Duration remaining = Duration.between(LocalDateTime.now(), shouldExpireByTime);

            long daysLeft = remaining.toDays();
            long hoursLeft = remaining.toHours() % 24;
            long minutesLeft = remaining.toMinutes() % 60;

            String description = String.format("""
                            Hey everyone! Here is a weekly update on the LeetCode leaderboard for our very own Patina members!

                            🥇- <@%s> - %s pts
                            🥈- <@%s> - %s pts
                            🥉- <@%s> - %s pts

                            To view the rest of the members, visit the website or check out the image embedded in this message!

                            Just as a reminder, there's %d day(s), %d hour(s), and %d minute(s) left until the leaderboard closes, so keep grinding!


                            See you next week!

                            Beep boop,
                            Codebloom
                            <https://codebloom.patinanetwork.org>
                            """,
                            users.get(0).getDiscordId(),
                            users.get(0).getTotalScore(),
                            users.get(1).getDiscordId(),
                            users.get(1).getTotalScore(),
                            users.get(2).getDiscordId(),
                            users.get(2).getTotalScore(),
                            daysLeft,
                            hoursLeft,
                            minutesLeft);

            MessageEmbed embed = new EmbedBuilder()
                            .setTitle("🏆 Patina Leaderboard - " + currentLeaderboard.getName())
                            .setDescription(description)
                            .setFooter("Codebloom - LeetCode Leaderboard for Patina Network", "https://codebloom.patinanetwork.org/favicon.ico")
                            .setImage("attachment://leaderboard.png")
                            .setColor(new Color(69, 129, 103)).build();

            LOGGER.info("Message has been built, ready to send...");

            channel.sendFiles(FileUpload.fromData(screenshotBytes, "leaderboard.png"))
                            .setEmbeds(embed)
                            .queue();

            LOGGER.info("Message has been queued");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
