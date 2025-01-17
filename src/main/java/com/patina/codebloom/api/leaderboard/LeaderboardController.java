package com.patina.codebloom.api.leaderboard;

import java.util.ArrayList;

import org.apache.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.patina.codebloom.common.db.models.leaderboard.Leaderboard;
import com.patina.codebloom.common.db.models.leaderboard.LeaderboardWithUsers;
import com.patina.codebloom.common.db.models.user.UserWithScore;
import com.patina.codebloom.common.db.repos.leaderboard.LeaderboardRepository;
import com.patina.codebloom.common.dto.ApiResponder;
import com.patina.codebloom.common.dto.autogen.__DO_NOT_USE_UNLESS_YOU_KNOW_WHAT_YOU_ARE_DOING_GENERIC_FAILURE_RESPONSE;
import com.patina.codebloom.common.security.Protector;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/leaderboard")
@Tag(name = "Leaderboard routes")
public class LeaderboardController {
        private final LeaderboardRepository leaderboardRepository;
        private final Protector protector;

        public LeaderboardController(LeaderboardRepository leaderboardRepository, Protector protector) {
                this.leaderboardRepository = leaderboardRepository;
                this.protector = protector;
        }

        @GetMapping("/all")
        @Operation(summary = "Fetch all leaderboards, attaching the users for each leaderboard.", responses = {
                        @ApiResponse(responseCode = "200"),
                        @ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content(schema = @Schema(implementation = __DO_NOT_USE_UNLESS_YOU_KNOW_WHAT_YOU_ARE_DOING_GENERIC_FAILURE_RESPONSE.class)))
        })
        public ResponseEntity<ApiResponder<ArrayList<LeaderboardWithUsers>>> getAllLeaderboardsFull(
                        HttpServletRequest request) {
                protector.validateSession(request);

                ArrayList<LeaderboardWithUsers> leaderboards = leaderboardRepository.getAllLeaderboardsFull();

                return ResponseEntity.ok().body(ApiResponder.success("All leaderboards found!", leaderboards));
        }

        @GetMapping("/all/shallow")
        @Operation(summary = "Fetch all leaderboards, excluding users.", responses = {
                        @ApiResponse(responseCode = "200"),
                        @ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content(schema = @Schema(implementation = __DO_NOT_USE_UNLESS_YOU_KNOW_WHAT_YOU_ARE_DOING_GENERIC_FAILURE_RESPONSE.class)))
        })
        public ResponseEntity<ApiResponder<ArrayList<Leaderboard>>> getAllLeaderboardsShallow(
                        HttpServletRequest request) {
                protector.validateSession(request);

                ArrayList<Leaderboard> leaderboards = leaderboardRepository.getAllLeaderboardsShallow();
                return ResponseEntity.ok().body(ApiResponder.success("All leaderboards found!", leaderboards));
        }

        @GetMapping("/current")
        @Operation(summary = "Fetch the currently active leaderboard data, attaching the users for each leaderboard.", responses = {
                        @ApiResponse(responseCode = "200"),
                        @ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content(schema = @Schema(implementation = __DO_NOT_USE_UNLESS_YOU_KNOW_WHAT_YOU_ARE_DOING_GENERIC_FAILURE_RESPONSE.class)))
        })
        public ResponseEntity<ApiResponder<LeaderboardWithUsers>> getCurrentLeaderboardFull(
                        HttpServletRequest request) {
                protector.validateSession(request);

                LeaderboardWithUsers leaderboardData = leaderboardRepository.getRecentLeaderboardFull();

                return ResponseEntity.ok().body(ApiResponder.success("All leaderboards found!", leaderboardData));
        }

        @GetMapping("/current/shallow")
        @Operation(summary = "Unprotected route that fetches the currently active leaderboard data, attaching only the top 5 users for each leaderboard.", responses = {
                        @ApiResponse(responseCode = "200"),
                        @ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content(schema = @Schema(implementation = __DO_NOT_USE_UNLESS_YOU_KNOW_WHAT_YOU_ARE_DOING_GENERIC_FAILURE_RESPONSE.class)))
        })

        public ResponseEntity<ApiResponder<LeaderboardWithUsers>> getCurrentLeaderboardShallow(
                        HttpServletRequest request) {
                LeaderboardWithUsers leaderboardData = leaderboardRepository.getRecentLeaderboardShallow();

                return ResponseEntity.ok().body(ApiResponder.success("All leaderboards found!", leaderboardData));
        }

        @GetMapping("/current/user/{userId}")
        @Operation(summary = "Fetch the specific user data in the currently active leaderboard data.", responses = {
                        @ApiResponse(responseCode = "200"),
                        @ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content(schema = @Schema(implementation = __DO_NOT_USE_UNLESS_YOU_KNOW_WHAT_YOU_ARE_DOING_GENERIC_FAILURE_RESPONSE.class)))
        })
        public ResponseEntity<ApiResponder<UserWithScore>> getUserCurrentLeaderboardFull(
                        HttpServletRequest request, @PathVariable String userId) {
                protector.validateSession(request);

                LeaderboardWithUsers leaderboardData = leaderboardRepository.getRecentLeaderboardShallow();

                UserWithScore user = leaderboardRepository.getUserFromLeaderboard(leaderboardData.getId(), userId);

                if (user == null) {
                        return ResponseEntity.status(HttpStatus.SC_NOT_FOUND)
                                        .body(ApiResponder.failure("This user does not exist on this leaderboard."));
                }

                return ResponseEntity.ok().body(ApiResponder.success("User found!", user));
        }

        @GetMapping("/{leaderboardId}")
        @Operation(description = "", responses = {
                        @ApiResponse(responseCode = "200"),
                        @ApiResponse(responseCode = "404", description = "Leaderboard not found", content = @Content(schema = @Schema(implementation = __DO_NOT_USE_UNLESS_YOU_KNOW_WHAT_YOU_ARE_DOING_GENERIC_FAILURE_RESPONSE.class))),
                        @ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content(schema = @Schema(implementation = __DO_NOT_USE_UNLESS_YOU_KNOW_WHAT_YOU_ARE_DOING_GENERIC_FAILURE_RESPONSE.class)))
        })
        public ResponseEntity<ApiResponder<LeaderboardWithUsers>> getLeaderboardByIdFull(HttpServletRequest request,
                        @PathVariable String leaderboardId) {
                protector.validateSession(request);

                LeaderboardWithUsers leaderboardData = leaderboardRepository.getLeaderboardByIdFull(leaderboardId);

                if (leaderboardData == null) {
                        return ResponseEntity.status(HttpStatus.SC_NOT_FOUND)
                                        .body(ApiResponder.failure("This leaderboard does not exist."));
                }

                return ResponseEntity.ok().body(ApiResponder.success("Leaderboard found!", leaderboardData));
        }

        @GetMapping("/{leaderboardId}/user/{userId}")
        @Operation(summary = "Fetch the user data in the current leaderboard.", responses = {
                        @ApiResponse(responseCode = "200"),
                        @ApiResponse(responseCode = "404", description = "Leaderboard not found", content = @Content(schema = @Schema(implementation = __DO_NOT_USE_UNLESS_YOU_KNOW_WHAT_YOU_ARE_DOING_GENERIC_FAILURE_RESPONSE.class))),
                        @ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content(schema = @Schema(implementation = __DO_NOT_USE_UNLESS_YOU_KNOW_WHAT_YOU_ARE_DOING_GENERIC_FAILURE_RESPONSE.class)))
        })
        public ResponseEntity<ApiResponder<UserWithScore>> getUserByLeaderboardById(HttpServletRequest request,
                        @PathVariable String leaderboardId, @PathVariable String userId) {
                protector.validateSession(request);

                UserWithScore user = leaderboardRepository.getUserFromLeaderboard(leaderboardId, userId);

                if (user == null) {
                        return ResponseEntity.status(HttpStatus.SC_NOT_FOUND)
                                        .body(ApiResponder.failure("This user does not exist on this leaderboard."));
                }

                return ResponseEntity.ok().body(ApiResponder.success("User found!", user));
        }

}
