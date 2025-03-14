package com.patina.codebloom.api.leaderboard;

import java.util.ArrayList;

import org.apache.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.patina.codebloom.common.db.models.leaderboard.Leaderboard;
import com.patina.codebloom.common.db.models.leaderboard.LeaderboardWithUsers;
import com.patina.codebloom.common.db.models.user.UserWithScore;
import com.patina.codebloom.common.db.repos.leaderboard.LeaderboardRepository;
import com.patina.codebloom.common.dto.ApiResponder;
import com.patina.codebloom.common.dto.autogen.UnsafeGenericFailureResponse;
import com.patina.codebloom.common.lag.FakeLag;
import com.patina.codebloom.common.page.Page;
import com.patina.codebloom.common.security.Protector;

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
    private static final int LEADERBOARD_PAGE_SIZE = 5;

    private final LeaderboardRepository leaderboardRepository;
    private final Protector protector;

    public LeaderboardController(final LeaderboardRepository leaderboardRepository, final Protector protector) {
        this.leaderboardRepository = leaderboardRepository;
        this.protector = protector;
    }

    @GetMapping("/all")
    @Operation(summary = "Fetch all leaderboards, attaching the users for each leaderboard.", responses = { @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content(schema = @Schema(implementation = UnsafeGenericFailureResponse.class))) })
    public ResponseEntity<ApiResponder<ArrayList<LeaderboardWithUsers>>> getAllLeaderboardsFull(final HttpServletRequest request) {
        FakeLag.sleep(1200);
        protector.validateSession(request);

        ArrayList<LeaderboardWithUsers> leaderboards = leaderboardRepository.getAllLeaderboardsFull();

        return ResponseEntity.ok().body(ApiResponder.success("All leaderboards found!", leaderboards));
    }

    @GetMapping("/all/shallow")
    @Operation(summary = "Fetch all leaderboards, excluding users.", responses = { @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content(schema = @Schema(implementation = UnsafeGenericFailureResponse.class))) })
    public ResponseEntity<ApiResponder<ArrayList<Leaderboard>>> getAllLeaderboardsShallow(final HttpServletRequest request) {
        protector.validateSession(request);

        ArrayList<Leaderboard> leaderboards = leaderboardRepository.getAllLeaderboardsShallow();
        return ResponseEntity.ok().body(ApiResponder.success("All leaderboards found!", leaderboards));
    }

    @GetMapping("/current")
    @Operation(summary = "Fetch the currently active leaderboard data, attaching the users for each leaderboard.", responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content(schema = @Schema(implementation = UnsafeGenericFailureResponse.class))) })
    public ResponseEntity<ApiResponder<Page<LeaderboardWithUsers>>> getCurrentLeaderboardFull(final HttpServletRequest request,
                    @Parameter(description = "Page index", example = "1") @RequestParam(required = false, defaultValue = "1") final int page) {
        FakeLag.sleep(800);

        protector.validateSession(request);

        LeaderboardWithUsers leaderboardData = leaderboardRepository.getRecentLeaderboardFull(page, LEADERBOARD_PAGE_SIZE);

        int totalUsers = leaderboardRepository.getRecentLeaderboardUserCount();
        int totalPages = (int) Math.ceil((double) totalUsers / LEADERBOARD_PAGE_SIZE);
        boolean hasNextPage = page < totalPages;

        Page<LeaderboardWithUsers> createdPage = new Page<>(hasNextPage, leaderboardData, totalPages);

        return ResponseEntity.ok().body(ApiResponder.success("All leaderboards found!", createdPage));
    }

    @GetMapping("/current/shallow")
    @Operation(summary = "Unprotected route that fetches the currently active leaderboard data, attaching only the top 5 users for each leaderboard.", responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content(schema = @Schema(implementation = UnsafeGenericFailureResponse.class))) })

    public ResponseEntity<ApiResponder<LeaderboardWithUsers>> getCurrentLeaderboardShallow(final HttpServletRequest request) {
        FakeLag.sleep(650);

        LeaderboardWithUsers leaderboardData = leaderboardRepository.getRecentLeaderboardShallow();

        return ResponseEntity.ok().body(ApiResponder.success("All leaderboards found!", leaderboardData));
    }

    @GetMapping("/current/user/{userId}")
    @Operation(summary = "Fetch the specific user data in the currently active leaderboard data.", responses = { @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content(schema = @Schema(implementation = UnsafeGenericFailureResponse.class))) })
    public ResponseEntity<ApiResponder<UserWithScore>> getUserCurrentLeaderboardFull(final HttpServletRequest request,
                    @PathVariable final String userId) {
        FakeLag.sleep(650);

        protector.validateSession(request);

        LeaderboardWithUsers leaderboardData = leaderboardRepository.getRecentLeaderboardShallow();

        UserWithScore user = leaderboardRepository.getUserFromLeaderboard(leaderboardData.getId(), userId);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.SC_NOT_FOUND).body(ApiResponder.failure("This user does not exist on this leaderboard."));
        }

        return ResponseEntity.ok().body(ApiResponder.success("User found!", user));
    }

    @GetMapping("/{leaderboardId}")
    @Operation(description = "", responses = { @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "404", description = "Leaderboard not found", content = @Content(schema = @Schema(implementation = UnsafeGenericFailureResponse.class))),
            @ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content(schema = @Schema(implementation = UnsafeGenericFailureResponse.class))) })
    public ResponseEntity<ApiResponder<LeaderboardWithUsers>> getLeaderboardByIdFull(final HttpServletRequest request,
                    @PathVariable final String leaderboardId) {
        FakeLag.sleep(800);

        protector.validateSession(request);

        LeaderboardWithUsers leaderboardData = leaderboardRepository.getLeaderboardByIdFull(leaderboardId);

        if (leaderboardData == null) {
            return ResponseEntity.status(HttpStatus.SC_NOT_FOUND).body(ApiResponder.failure("This leaderboard does not exist."));
        }

        return ResponseEntity.ok().body(ApiResponder.success("Leaderboard found!", leaderboardData));
    }

    @GetMapping("/{leaderboardId}/user/{userId}")
    @Operation(summary = "Fetch the user data in the current leaderboard.", responses = { @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "404", description = "Leaderboard not found", content = @Content(schema = @Schema(implementation = UnsafeGenericFailureResponse.class))),
            @ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content(schema = @Schema(implementation = UnsafeGenericFailureResponse.class))) })
    public ResponseEntity<ApiResponder<UserWithScore>> getUserByLeaderboardById(final HttpServletRequest request,
                    @PathVariable final String leaderboardId, @PathVariable final String userId) {
        FakeLag.sleep(650);

        protector.validateSession(request);

        UserWithScore user = leaderboardRepository.getUserFromLeaderboard(leaderboardId, userId);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.SC_NOT_FOUND).body(ApiResponder.failure("This user does not exist on this leaderboard."));
        }

        return ResponseEntity.ok().body(ApiResponder.success("User found!", user));
    }

}
