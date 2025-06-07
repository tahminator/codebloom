package com.patina.codebloom.scheduled.discord;

import java.time.LocalDateTime;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.patina.codebloom.common.db.models.weekly.WeeklyMessage;
import com.patina.codebloom.common.db.repos.weekly.WeeklyMessageRepository;
import com.patina.codebloom.jda.client.JDAClient;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class WeeklyLeaderboard {

    private final JDAClient jdaClient;
    private final WeeklyMessageRepository weeklyMessageRepository;

    WeeklyLeaderboard(final JDAClient jdaClient, final WeeklyMessageRepository weeklyMessageRepository) {
        this.jdaClient = jdaClient;
        this.weeklyMessageRepository = weeklyMessageRepository;
    }

    @Scheduled(initialDelay = 0, fixedDelay = 1000 * 60 * 60)
    public void sendWeeklyLeaderboard() {
        WeeklyMessage weeklyMessage = weeklyMessageRepository.getLatestWeeklyMessage();

        if (weeklyMessage != null && !weeklyMessage
                        .getCreatedAt()
                        .isBefore(
                                        LocalDateTime
                                                        .now()
                                                        .minusDays(7L))) {
            log.info("WeeklyLeaderboard skipped.");
            return;
        }

        log.info("WeeklyLeaderboard triggered.");
        try {
            log.info("Connecting to JDA client...");
            jdaClient.connect();
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize JDAClient", e);
        }
        log.info("JDDA Client should be connected now. Sending leaderboard...");

        jdaClient.sendLeaderboardMessage(jdaClient.getPatinaGuildId(), jdaClient.getPatinaLeetcodeChannelId());

        weeklyMessageRepository.createLatestWeeklyMessage();
    }

}
