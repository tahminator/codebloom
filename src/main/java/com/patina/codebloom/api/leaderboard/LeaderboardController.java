package com.patina.codebloom.api.leaderboard;

import java.util.ArrayList;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.patina.codebloom.common.db.models.leaderboard.Leaderboard;
import com.patina.codebloom.common.db.models.user.UserWithScore;
import com.patina.codebloom.common.db.repos.leaderboard.LeaderboardRepository;
import com.patina.codebloom.common.db.repos.user.UserRepository;
import com.patina.codebloom.common.dto.ApiResponder;
import com.patina.codebloom.common.dto.autogen.UnsafeGenericFailureResponse;
import com.patina.codebloom.common.lag.FakeLag;
import com.patina.codebloom.common.page.Page;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/leaderboard")
@Tag(name = "Leaderboard routes")
public class LeaderboardController {
    private static final int LEADERBOARD_PAGE_SIZE = 20;

    private final LeaderboardRepository leaderboardRepository;
    private final UserRepository userRepository;

    public LeaderboardController(final LeaderboardRepository leaderboardRepository, final UserRepository userRepository) {
        this.leaderboardRepository = leaderboardRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/current/metadata")
    @Operation(summary = "Unprotected route that fetches the currently active leaderboard data, attaching only the top 5 users for each leaderboard.", responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content(schema = @Schema(implementation = UnsafeGenericFailureResponse.class))) })

    public ResponseEntity<ApiResponder<Leaderboard>> getCurrentLeaderboardMetadata(final HttpServletRequest request) {
        FakeLag.sleep(650);

        Leaderboard leaderboardData = leaderboardRepository.getRecentLeaderboardMetadata();

        return ResponseEntity.ok().body(ApiResponder.success("All leaderboards found!", leaderboardData));
    }

    @GetMapping("/current/user/all")
    @Operation(summary = "Fetch the currently active leaderboard data, attaching the users for each leaderboard.", responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content(schema = @Schema(implementation = UnsafeGenericFailureResponse.class))) })
    public ResponseEntity<ApiResponder<Page<ArrayList<UserWithScore>>>> getCurrentLeaderboardUsers(final HttpServletRequest request,
                    @Parameter(description = "Page index", example = "1") @RequestParam(required = false, defaultValue = "1") final int page,
                    @Parameter(description = "Page size (maximum of " + LEADERBOARD_PAGE_SIZE) @RequestParam(required = false, defaultValue = "" + LEADERBOARD_PAGE_SIZE) final int pageSize,
                    @Parameter(description = "Filter for Patina users") @RequestParam(required = false, defaultValue = "false") final boolean patina) {
        FakeLag.sleep(800);

        final int parsedPageSize = Math.min(pageSize, LEADERBOARD_PAGE_SIZE);

        ArrayList<UserWithScore> leaderboardData = leaderboardRepository.getRecentLeaderboardUsers(page, parsedPageSize, patina);

        int totalUsers = leaderboardRepository.getRecentLeaderboardUserCount(patina);
        int totalPages = (int) Math.ceil((double) totalUsers / parsedPageSize);
        boolean hasNextPage = page < totalPages;

        Page<ArrayList<UserWithScore>> createdPage = new Page<>(hasNextPage, leaderboardData, totalPages, LEADERBOARD_PAGE_SIZE);

        return ResponseEntity.ok().body(ApiResponder.success("All leaderboards found!", createdPage));
    }

    @GetMapping("/current/user/{userId}")
    @Operation(summary = "Fetch the specific user data in the currently active leaderboard data.", responses = { @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content(schema = @Schema(implementation = UnsafeGenericFailureResponse.class))) })
    public ResponseEntity<ApiResponder<UserWithScore>> getUserCurrentLeaderboardFull(final HttpServletRequest request,
                    @PathVariable final String userId) {
        FakeLag.sleep(650);

        Leaderboard leaderboardData = leaderboardRepository.getRecentLeaderboardMetadata();

        UserWithScore user = userRepository.getUserWithScoreById(userId, leaderboardData.getId());

        // if (user == null) {
        // return
        // ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponder.failure("This
        // user does not exist on this leaderboard."));
        // }

        return ResponseEntity.ok().body(ApiResponder.success("User found!", user));
    }
}
