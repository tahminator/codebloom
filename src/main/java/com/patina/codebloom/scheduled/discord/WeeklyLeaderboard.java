package com.patina.codebloom.scheduled.discord;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.patina.codebloom.jda.client.JDAClient;

@Component
public class WeeklyLeaderboard {
    private static final Logger LOGGER = LoggerFactory.getLogger(WeeklyLeaderboard.class);

    private final JDAClient jdaClient;

    WeeklyLeaderboard(final JDAClient jdaClient) {
        this.jdaClient = jdaClient;
    }

    @Scheduled(cron = "0 38 12 ? * SAT")
    public void sendWeeklyLeaderboard() {
        LOGGER.info("WeeklyLeaderboard triggered.");
        try {
            LOGGER.info("Connecting to JDA client...");
            jdaClient.connect();
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize JDAClient", e);
        }
        LOGGER.info("JDDA Client should be connected now. Sending leaderboard...");

        jdaClient.sendLeaderboardMessage(jdaClient.getPatinaGuildId(), jdaClient.getPatinaLeetcodeChannelId());
    }

}
