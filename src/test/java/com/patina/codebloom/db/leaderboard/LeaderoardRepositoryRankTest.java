package com.patina.codebloom.db.leaderboard;

// CHECKSTYLE:OFF
import static org.junit.jupiter.api.Assertions.*;
// CHECKSTYLE:ON

import java.util.List;
import java.util.Objects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Description;

import com.patina.codebloom.common.db.models.leaderboard.Leaderboard;
import com.patina.codebloom.common.db.models.user.UserWithScore;
import com.patina.codebloom.common.db.models.usertag.Tag;
import com.patina.codebloom.common.db.repos.leaderboard.LeaderboardRepository;
import com.patina.codebloom.common.db.repos.leaderboard.options.LeaderboardFilterOptions;
import com.patina.codebloom.common.page.Indexed;
import com.patina.codebloom.db.BaseRepositoryTest;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Slf4j
public class LeaderoardRepositoryRankTest extends BaseRepositoryTest {
    private final LeaderboardRepository leaderboardRepository;

    private final Leaderboard currentLeaderboard;

    @Autowired
    public LeaderoardRepositoryRankTest(final LeaderboardRepository leaderboardRepository) {
        this.leaderboardRepository = leaderboardRepository;
        this.currentLeaderboard = this.leaderboardRepository.getRecentLeaderboardMetadata();
    }

    @Test
    @DisplayName("Assert that ranked behaviors are correct between users list and single user - Filtered")
    void assertRankedConsistencyBetweenUserListAndSingleUserFiltered() {
        var opts = LeaderboardFilterOptions.builder()
                        .patina(true)
                        .build();

        List<Indexed<UserWithScore>> users = leaderboardRepository.getRankedIndexedLeaderboardUsersById(currentLeaderboard.getId(), opts);

        users.forEach(i -> {
            var ni = leaderboardRepository.getFilteredRankedUserById(currentLeaderboard.getId(), i.getItem().getId(), opts);
            var user = ni.getItem();
            assertEquals(ni.getIndex(),
                            users.stream()
                                            .filter(u -> {
                                                var possibleUser = u.getItem();
                                                return Objects.equals(user.getId(), possibleUser.getId());
                                            })
                                            .findFirst()
                                            .map(Indexed::getIndex)
                                            .orElse(-1));
        });
    }

    @Test
    @DisplayName("Assert that ranked behaviors are correct between users list and single user - Global")
    void assertRankedConsistencyBetweenUserListAndSingleUserGlobal() {
        var opts = LeaderboardFilterOptions.DEFAULT;
        List<Indexed<UserWithScore>> users = leaderboardRepository.getGlobalRankedIndexedLeaderboardUsersById(currentLeaderboard.getId(), opts);

        users.forEach(i -> {
            var ni = leaderboardRepository.getGlobalRankedUserById(currentLeaderboard.getId(), i.getItem().getId());
            var user = ni.getItem();
            assertEquals(ni.getIndex(),
                            users.stream()
                                            .filter(u -> {
                                                var possibleUser = u.getItem();
                                                return Objects.equals(user.getId(), possibleUser.getId());
                                            })
                                            .findFirst()
                                            .map(Indexed::getIndex)
                                            .orElse(-1));
        });
    }

    @Test
    @DisplayName("Assert that getFilteredRankedUserById fails if user is not in the search criteria")
    void assertGetFilteredRankedUserByIdFailsIfNotInSearchCriteria() {
        String gwcUserId = "a1a1a1a1-a2d2-e3e3-f4f4-a5a5a5a5a5a5";
        var opts = LeaderboardFilterOptions.builder()
                        .patina(true)
                        .build();

        Indexed<UserWithScore> i = leaderboardRepository.getFilteredRankedUserById(currentLeaderboard.getId(), gwcUserId, opts);
        assertNull(i);
    }
}
