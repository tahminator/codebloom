package org.patinanetwork.codebloom.common.components;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.patinanetwork.codebloom.common.db.models.achievements.Achievement;
import org.patinanetwork.codebloom.common.db.models.achievements.AchievementPlaceEnum;
import org.patinanetwork.codebloom.common.db.models.leaderboard.Leaderboard;
import org.patinanetwork.codebloom.common.db.models.user.UserWithScore;
import org.patinanetwork.codebloom.common.db.models.usertag.Tag;
import org.patinanetwork.codebloom.common.db.repos.achievements.AchievementRepository;
import org.patinanetwork.codebloom.common.db.repos.leaderboard.LeaderboardRepository;
import org.patinanetwork.codebloom.common.db.repos.leaderboard.options.LeaderboardFilterGenerator;
import org.patinanetwork.codebloom.common.db.repos.leaderboard.options.LeaderboardFilterOptions;
import org.patinanetwork.codebloom.common.dto.user.UserWithScoreDto;
import org.patinanetwork.codebloom.common.page.Indexed;
import org.patinanetwork.codebloom.common.page.Page;
import org.patinanetwork.codebloom.common.utils.leaderboard.LeaderboardUtils;
import org.patinanetwork.codebloom.common.utils.pair.Pair;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LeaderboardManager {

    private final LeaderboardRepository leaderboardRepository;
    private final AchievementRepository achievementRepository;

    public LeaderboardManager(
            final LeaderboardRepository leaderboardRepository, final AchievementRepository achievementRepository) {
        this.leaderboardRepository = leaderboardRepository;
        this.achievementRepository = achievementRepository;
    }

    public static final int MIN_POSSIBLE_WINNERS = 0;
    public static final int MAX_POSSIBLE_WINNERS = 3;

    private int maxWinners(final int size) {
        return Math.min(Math.max(size, MIN_POSSIBLE_WINNERS), MAX_POSSIBLE_WINNERS);
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

        if (currentLeaderboard == null) {
            return;
        }

        List<Pair<LeaderboardFilterOptions, Tag>> filterOptsAndTags =
                LeaderboardFilterGenerator.generateAllSupportedTagToggles();

        for (var pair : filterOptsAndTags) {
            log.info("on leaderboard for {}", pair.getRight().getResolvedName());
            List<Indexed<UserWithScore>> users = leaderboardRepository.getRankedIndexedLeaderboardUsersById(
                    currentLeaderboard.getId(), pair.getLeft());
            List<UserWithScore> usersWithPoints = LeaderboardUtils.filterUsersWithPoints(
                    users.stream().map(Indexed::getItem).toList());
            List<UserWithScore> winners = usersWithPoints.subList(0, maxWinners(usersWithPoints.size()));

            for (int i = 0; i < winners.size(); i++) {
                int place = i + 1;
                log.info("on leaderboard for {} for winner #{}", pair.getRight().getResolvedName(), place);
                String placeString = calculatePlaceString(place);
                UserWithScore user = winners.get(i);
                Achievement achievement = Achievement.builder()
                        .userId(user.getId())
                        .place(AchievementPlaceEnum.fromInteger(place))
                        .leaderboard(pair.getRight())
                        .title(String.format(
                                "%s - %s - %s Place",
                                currentLeaderboard.getName(), pair.getRight().getResolvedName(), placeString))
                        .build();
                achievementRepository.createAchievement(achievement);
            }
        }

        // handle global leaderboard
        List<Indexed<UserWithScore>> users = leaderboardRepository.getGlobalRankedIndexedLeaderboardUsersById(
                currentLeaderboard.getId(), LeaderboardFilterOptions.DEFAULT);
        List<UserWithScore> usersWithPoints = LeaderboardUtils.filterUsersWithPoints(
                users.stream().map(Indexed::getItem).toList());
        List<UserWithScore> winners = usersWithPoints.subList(0, maxWinners(usersWithPoints.size()));

        for (int i = 0; i < winners.size(); i++) {
            int place = i + 1;
            log.info("on leaderboard for {} for global winner #{}", currentLeaderboard.getName(), place);
            String placeString = calculatePlaceString(place);
            UserWithScore user = winners.get(i);
            Achievement achievement = Achievement.builder()
                    .userId(user.getId())
                    .place(AchievementPlaceEnum.fromInteger(place))
                    .leaderboard(null)
                    .title(String.format("%s - %s Place", currentLeaderboard.getName(), placeString))
                    .build();
            achievementRepository.createAchievement(achievement);
        }
    }

    public Leaderboard getLeaderboardMetadata(final String id) {
        return leaderboardRepository.getLeaderboardMetadataById(id);
    }

    public int getLeaderboardUserCountById(final String leaderboardId, final LeaderboardFilterOptions options) {
        return leaderboardRepository.getLeaderboardUserCountById(leaderboardId, options);
    }

    public Page<Indexed<UserWithScoreDto>> getLeaderboardUsers(
            final String currentLeaderboardId, final LeaderboardFilterOptions options, final boolean globalIndex) {
        final int page = options.getPage();
        final int parsedPageSize = options.getPageSize();

        int totalUsers = leaderboardRepository.getLeaderboardUserCountById(currentLeaderboardId, options);
        int totalPages = (int) Math.ceil((double) totalUsers / parsedPageSize);
        boolean hasNextPage = page < totalPages;

        List<Indexed<UserWithScore>> leaderboardData;
        // don't use globalIndex when there are no filters enabled.
        if (globalIndex
                && (options.isPatina()
                        || options.isNyu()
                        || options.isHunter()
                        || options.isBaruch()
                        || options.isRpi()
                        || options.isGwc()
                        || options.isSbu()
                        || options.isCcny()
                        || options.isColumbia()
                        || options.isCornell()
                        || options.isBmcc()
                        || options.isMhcplusplus())) {
            leaderboardData =
                    leaderboardRepository.getGlobalRankedIndexedLeaderboardUsersById(currentLeaderboardId, options);
        } else {
            leaderboardData = leaderboardRepository.getRankedIndexedLeaderboardUsersById(currentLeaderboardId, options);
        }

        List<Indexed<UserWithScoreDto>> indexedUserWithScoreDtos = leaderboardData.stream()
                .map(indexed -> indexed.map(UserWithScoreDto::fromUserWithScore))
                .toList();

        Page<Indexed<UserWithScoreDto>> createdPage =
                new Page<>(hasNextPage, indexedUserWithScoreDtos, totalPages, parsedPageSize);
        return createdPage;
    }
}
