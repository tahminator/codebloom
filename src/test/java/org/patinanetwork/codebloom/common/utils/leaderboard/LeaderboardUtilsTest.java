package org.patinanetwork.codebloom.common.utils.leaderboard;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.patinanetwork.codebloom.common.db.models.user.UserWithScore;
import org.patinanetwork.codebloom.common.page.Indexed;

class LeaderboardUtilsTest {

    private UserWithScore userWithScore(int score) {
        return UserWithScore.builder()
                .id("id")
                .discordId("123")
                .discordName("name")
                .admin(false)
                .verifyKey("key")
                .totalScore(score)
                .build();
    }

    @Test
    void filterUsersWithPointsEmpty() {
        var result = LeaderboardUtils.filterUsersWithPoints(List.of());
        assertTrue(result.isEmpty());
    }

    @Test
    void filterUsersWithPointsAllPositive() {
        var users = List.of(userWithScore(100), userWithScore(50));
        var result = LeaderboardUtils.filterUsersWithPoints(users);
        assertEquals(2, result.size());
    }

    @Test
    void filterUsersWithPointsAllZero() {
        var users = List.of(userWithScore(0), userWithScore(0));
        var result = LeaderboardUtils.filterUsersWithPoints(users);
        assertTrue(result.isEmpty());
    }

    @Test
    void filterUsersWithPointsMixed() {
        var users = List.of(userWithScore(100), userWithScore(0), userWithScore(50));
        var result = LeaderboardUtils.filterUsersWithPoints(users);
        assertEquals(2, result.size());
        assertEquals(100, result.get(0).getTotalScore());
        assertEquals(50, result.get(1).getTotalScore());
    }

    @Test
    void filterIndexedUsersWithPointsEmpty() {
        var result = LeaderboardUtils.filterUsersWithPoints(List.of());
        assertTrue(result.isEmpty());
    }

    @Test
    void filterIndexedUsersWithPointsAllPositive() {
        var users = Indexed.ofDefaultList(List.of(userWithScore(100), userWithScore(50)));
        var result = LeaderboardUtils.filterUsersWithPoints(
                users.stream().map(Indexed::getItem).toList());
        assertEquals(2, result.size());
    }

    @Test
    void filterIndexedUsersWithPointsAllZero() {
        var users = Indexed.ofDefaultList(List.of(userWithScore(0), userWithScore(0)));
        var result = LeaderboardUtils.filterUsersWithPoints(
                users.stream().map(Indexed::getItem).toList());
        assertTrue(result.isEmpty());
    }

    @Test
    void filterIndexedUsersWithPointsMixed() {
        var users = Indexed.ofDefaultList(List.of(userWithScore(100), userWithScore(0), userWithScore(50)));
        var result = LeaderboardUtils.filterUsersWithPoints(
                users.stream().map(Indexed::getItem).toList());
        assertEquals(2, result.size());
        assertEquals(100, result.get(0).getTotalScore());
        assertEquals(50, result.get(1).getTotalScore());
    }

    @Test
    void filterUsersWithPointsSingleZero() {
        var users = List.of(userWithScore(0));
        var result = LeaderboardUtils.filterUsersWithPoints(users);
        assertTrue(result.isEmpty());
    }

    @Test
    void filterIndexedUsersWithPointsSingleZero() {
        var users = Indexed.ofDefaultList(List.of(userWithScore(0)));
        var result = LeaderboardUtils.filterUsersWithPoints(
                users.stream().map(Indexed::getItem).toList());
        assertTrue(result.isEmpty());
    }
}
