package com.patina.codebloom.api.test.leaderboard;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/test/leaderboard")
@Tag(name = "Test data routes")
public class TestLeaderboardController {
    // private final Protector protector;
    // private final LeaderboardRepository leaderboardRepository;
    //
    // public TestLeaderboardController(final Protector protector, final
    // LeaderboardRepository leaderboardRepository) {
    // this.protector = protector;
    // this.leaderboardRepository = leaderboardRepository;
    // }

    // @GetMapping("/shallow")
    // @Operation(summary = "Returns the top 5 leaderboard positions in order.")
    // public ResponseEntity<?> getShallowLeaderboard() {
    // FakeLag.sleep(600);
    //
    // LeaderboardWithUsers realLeaderboard =
    // leaderboardRepository.getRecentLeaderboardShallow();
    // LeaderboardWithUsers leaderboard =
    // TestLeaderboardList.getTestDataShallowList();
    //
    // ArrayList<UserWithScore> combinedUsers = new ArrayList<>();
    // combinedUsers.addAll(realLeaderboard.getUsers());
    // combinedUsers.addAll(leaderboard.getUsers());
    //
    // combinedUsers.sort((user1, user2) -> Integer.compare(user2.getTotalScore(),
    // user1.getTotalScore()));
    //
    // ArrayList<UserWithScore> topUsers = new ArrayList<>(combinedUsers.subList(0,
    // Math.min(5, combinedUsers.size())));
    //
    // LeaderboardWithUsers newLeaderboard = new
    // LeaderboardWithUsers(leaderboard.getId(), leaderboard.getName(),
    // leaderboard.getCreatedAt(), leaderboard.getDeletedAt(), topUsers);
    //
    // return ResponseEntity.ok().body(ApiResponder.success("Here is some of the
    // leaderboard!", newLeaderboard));
    // }

    // @GetMapping("/all")
    // public ResponseEntity<?> getFullLeaderboard(final HttpServletRequest request)
    // {
    // FakeLag.sleep(900);

    // protector.validateSession(request);

    // LeaderboardWithUsers realLeaderboard =
    // leaderboardRepository.getRecentLeaderboardFull();
    // LeaderboardWithUsers leaderboard = TestLeaderboardList.getTestDataLongList();

    // ArrayList<UserWithScore> combinedUsers = new ArrayList<>();
    // combinedUsers.addAll(realLeaderboard.getUsers());
    // combinedUsers.addAll(leaderboard.getUsers());

    // combinedUsers.sort((user1, user2) -> Integer.compare(user2.getTotalScore(),
    // user1.getTotalScore()));

    // LeaderboardWithUsers newLeaderboard = new
    // LeaderboardWithUsers(leaderboard.getId(), leaderboard.getName(),
    // leaderboard.getCreatedAt(), leaderboard.getDeletedAt(), combinedUsers);

    // return ResponseEntity.ok().body(ApiResponder.success("Giving you the full
    // leaderboard!", newLeaderboard));
    // }
}
