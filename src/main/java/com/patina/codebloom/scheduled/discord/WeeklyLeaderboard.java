package com.patina.codebloom.scheduled.discord;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.patina.codebloom.jda.client.JDAClient;

@Component
public class WeeklyLeaderboard {
    // private static final Logger LOGGER =
    // LoggerFactory.getLogger(WeeklyLeaderboard.class);

    private final JDAClient jdaClient;

    WeeklyLeaderboard(final JDAClient jdaClient) {
        this.jdaClient = jdaClient;
    }

    @Scheduled(cron = "0 0 12 ? * SAT")
    public void sendWeeklyLeaderboard() {
        try {
            jdaClient.connect();
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize JDAClient", e);
        }

        jdaClient.sendLeaderboardMessage(jdaClient.getPatinaGuildId(), jdaClient.getPatinaLeetcodeChannelId());
    }

}
