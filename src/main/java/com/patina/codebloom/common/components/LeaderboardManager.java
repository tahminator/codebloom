package com.patina.codebloom.common.components;

import java.util.List;

import org.springframework.stereotype.Component;

import com.patina.codebloom.common.db.models.achievements.Achievement;
import com.patina.codebloom.common.db.models.leaderboard.Leaderboard;
import com.patina.codebloom.common.db.models.user.UserWithScore;
import com.patina.codebloom.common.db.models.usertag.Tag;
import com.patina.codebloom.common.db.repos.achievements.AchievementRepository;
import com.patina.codebloom.common.db.repos.leaderboard.LeaderboardRepository;
import com.patina.codebloom.common.db.repos.leaderboard.options.LeaderboardFilterGenerator;
import com.patina.codebloom.common.db.repos.leaderboard.options.LeaderboardFilterOptions;
import com.patina.codebloom.common.page.Indexed;
import com.patina.codebloom.common.utils.pair.Pair;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class LeaderboardManager {
    private final LeaderboardRepository leaderboardRepository;
    private final AchievementRepository achievementRepository;

    public LeaderboardManager(final LeaderboardRepository leaderboardRepository, final AchievementRepository achievementRepository) {
        this.leaderboardRepository = leaderboardRepository;
        this.achievementRepository = achievementRepository;
    }

    private int maxWinners(final int size) {
        return Math.min(Math.max(size, 0), 3);
    }

    private String calculatePlaceString(final int place) {
        return switch (place) {
            case 1 -> "1st";
            case 2 -> "2nd";
            case 3 -> "3rd";
            default -> throw new IllegalArgumentException("Can only process 1st, 2nd, and 3rd place");
        };
    }

    public void generateAchievementsForAllWinners() {
        log.info("generating achievements for all winners...");
        Leaderboard currentLeaderboard = leaderboardRepository.getRecentLeaderboardMetadata();
        List<Pair<LeaderboardFilterOptions, Tag>> filterOptsAndTags = LeaderboardFilterGenerator.generateAllSchoolTagToggles();

        for (var pair : filterOptsAndTags) {
            log.info("on leaderboard for {}", pair.getRight().getResolvedName());
            List<Indexed<UserWithScore>> users = leaderboardRepository.getRankedIndexedLeaderboardUsersById(currentLeaderboard.getId(), pair.getLeft());
            List<Indexed<UserWithScore>> winners = users.subList(0, maxWinners(users.size()));

            for (int i = 0; i < winners.size(); i++) {
                int place = i + 1;
                log.info("on leaderboard for {} for winner #{}", pair.getRight().getResolvedName(), place);
                String placeString = calculatePlaceString(place);
                UserWithScore user = winners.get(i).getItem();
                Achievement achievement = Achievement.builder()
                                .userId(user.getId())
                                .iconUrl(String.format("/badge/%s/%s_place.jpg", pair.getRight().name(), placeString))
                                .title(String.format("%s - %s - %s Place", currentLeaderboard.getName(), pair.getRight().getResolvedName(), placeString))
                                .build();
                achievementRepository.createAchievement(achievement);
            }
        }

        // handle global leaderboard
        List<Indexed<UserWithScore>> users = leaderboardRepository.getGlobalRankedIndexedLeaderboardUsersById(currentLeaderboard.getId(), LeaderboardFilterOptions.DEFAULT);
        List<Indexed<UserWithScore>> winners = users.subList(0, maxWinners(users.size()));

        for (int i = 0; i < winners.size(); i++) {
            int place = i + 1;
            log.info("on leaderboard for {} for global winner #{}", place);
            String placeString = calculatePlaceString(place);
            UserWithScore user = winners.get(i).getItem();
            Achievement achievement = Achievement.builder()
                            .userId(user.getId())
                            .iconUrl(String.format("/badge/%s_place.jpg", placeString))
                            .title(String.format("%s - %s Place", currentLeaderboard.getName(), placeString))
                            .build();
            achievementRepository.createAchievement(achievement);
        }
    }
}
