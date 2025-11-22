package com.patina.codebloom.scheduled.discord;

import java.time.LocalDateTime;

import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.patina.codebloom.common.components.DiscordClubManager;
import com.patina.codebloom.common.db.models.weekly.WeeklyMessage;
import com.patina.codebloom.common.db.repos.weekly.WeeklyMessageRepository;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@Profile("!ci | thread")
public class WeeklyLeaderboard {

    private final DiscordClubManager discordClubManager;
    private final WeeklyMessageRepository weeklyMessageRepository;

    WeeklyLeaderboard(final DiscordClubManager discordClubManager, final WeeklyMessageRepository weeklyMessageRepository) {
        this.discordClubManager = discordClubManager;
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
        discordClubManager.sendWeeklyLeaderboardUpdateDiscordMessageToAllClubs();
    }

}
