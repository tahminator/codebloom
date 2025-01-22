package com.patina.codebloom.api.test.leaderboard;

import java.util.ArrayList;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.patina.codebloom.common.db.models.leaderboard.LeaderboardWithUsers;
import com.patina.codebloom.common.db.models.user.UserWithScore;
import com.patina.codebloom.common.db.repos.leaderboard.LeaderboardRepository;
import com.patina.codebloom.common.dto.ApiResponder;
import com.patina.codebloom.common.security.Protector;
import com.patina.codebloom.common.test.TestLeaderboardList;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/test/leaderboard")
@Tag(name = "Test data routes")
public class TestLeaderboardController {
    private final Protector protector;
    private final LeaderboardRepository leaderboardRepository;

    public TestLeaderboardController(Protector protector, LeaderboardRepository leaderboardRepository) {
        this.protector = protector;
        this.leaderboardRepository = leaderboardRepository;
    }

    @GetMapping("/shallow")
    @Operation(summary = "Returns the top 5 leaderboard positions in order.")
    public ResponseEntity<?> getShallowLeaderboard() {
        LeaderboardWithUsers realLeaderboard = leaderboardRepository.getRecentLeaderboardShallow();
        LeaderboardWithUsers leaderboard = TestLeaderboardList.getTestDataShallowList();

        ArrayList<UserWithScore> combinedUsers = new ArrayList<>();
        combinedUsers.addAll(realLeaderboard.getUsers());
        combinedUsers.addAll(leaderboard.getUsers());

        combinedUsers.sort((user1, user2) -> Integer.compare(user2.getTotalScore(), user1.getTotalScore()));

        ArrayList<UserWithScore> topUsers = new ArrayList<>(
                combinedUsers.subList(0, Math.min(5, combinedUsers.size())));

        LeaderboardWithUsers newLeaderboard = new LeaderboardWithUsers(leaderboard.getId(), leaderboard.getName(),
                leaderboard.getCreatedAt(), leaderboard.getDeletedAt(), topUsers);

        return ResponseEntity.ok().body(ApiResponder.success("Here is some of the leaderboard!", newLeaderboard));
    }

    @GetMapping("/all")
    public ResponseEntity<?> getFullLeaderboard(HttpServletRequest request) {
        protector.validateSession(request);

        LeaderboardWithUsers realLeaderboard = leaderboardRepository.getRecentLeaderboardFull();
        LeaderboardWithUsers leaderboard = TestLeaderboardList.getTestDataLongList();

        ArrayList<UserWithScore> combinedUsers = new ArrayList<>();
        combinedUsers.addAll(realLeaderboard.getUsers());
        combinedUsers.addAll(leaderboard.getUsers());

        combinedUsers.sort((user1, user2) -> Integer.compare(user2.getTotalScore(), user1.getTotalScore()));

        LeaderboardWithUsers newLeaderboard = new LeaderboardWithUsers(leaderboard.getId(), leaderboard.getName(),
                leaderboard.getCreatedAt(), leaderboard.getDeletedAt(), combinedUsers);

        return ResponseEntity.ok().body(ApiResponder.success("Giving you the full leaderboard!", newLeaderboard));
    }
}
