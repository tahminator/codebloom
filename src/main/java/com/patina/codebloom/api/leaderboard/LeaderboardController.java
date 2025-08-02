package com.patina.codebloom.api.leaderboard;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.patina.codebloom.common.db.models.leaderboard.Leaderboard;
import com.patina.codebloom.common.db.models.user.UserWithScore;
import com.patina.codebloom.common.db.repos.leaderboard.LeaderboardRepository;
import com.patina.codebloom.common.db.repos.leaderboard.options.LeaderboardFilterOptions;
import com.patina.codebloom.common.db.repos.user.UserRepository;
import com.patina.codebloom.common.dto.ApiResponder;
import com.patina.codebloom.common.dto.autogen.UnsafeGenericFailureResponse;
import com.patina.codebloom.common.lag.FakeLag;
import com.patina.codebloom.common.page.Indexed;
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
    private static final int MAX_LEADERBOARD_PAGE_SIZE = 20;

    private final LeaderboardRepository leaderboardRepository;
    private final UserRepository userRepository;

    public LeaderboardController(final LeaderboardRepository leaderboardRepository, final UserRepository userRepository) {
        this.leaderboardRepository = leaderboardRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/{leaderboardId}/metadata")
    @Operation(summary = "Unprotected route that fetches metadata of a leaderboard via leaderboard ID.", responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content(schema = @Schema(implementation = UnsafeGenericFailureResponse.class))) })

    public ResponseEntity<ApiResponder<Leaderboard>> getLeaderboardMetadataByLeaderboardId(
                    final @PathVariable String leaderboardId,
                    final HttpServletRequest request) {
        FakeLag.sleep(650);

        Leaderboard leaderboardData = leaderboardRepository.getLeaderboardMetadataById(leaderboardId);

        if (leaderboardData == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Leaderboard cannot be found or does not exist.");
        }

        return ResponseEntity.ok().body(ApiResponder.success("Leaderboard metadata found!", leaderboardData));
    }

    @GetMapping("/{leaderboardId}/user/all")
    @Operation(summary = "Unprotected route that fetches metadata of a leaderboard via leaderboard ID.", responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content(schema = @Schema(implementation = UnsafeGenericFailureResponse.class))) })

    public ResponseEntity<ApiResponder<Page<List<UserWithScore>>>> getLeaderboardUsersById(
                    @PathVariable final String leaderboardId,
                    @Parameter(description = "Page index", example = "1") @RequestParam(required = false, defaultValue = "1") final int page,
                    @Parameter(description = "Page size (maximum of " + MAX_LEADERBOARD_PAGE_SIZE) @RequestParam(required = false, defaultValue = "" + MAX_LEADERBOARD_PAGE_SIZE) final int pageSize,
                    @Parameter(description = "Discord name", example = "tahmid") @RequestParam(required = false, defaultValue = "") final String query,
                    @Parameter(description = "Filter for Patina users") @RequestParam(required = false, defaultValue = "false") final boolean patina,
                    @Parameter(description = "Filter for Hunter College users") @RequestParam(required = false, defaultValue = "false") final boolean hunter,
                    @Parameter(description = "Filter for NYU users") @RequestParam(required = false, defaultValue = "false") final boolean nyu,
                    final HttpServletRequest request) {
        FakeLag.sleep(800);

        final int parsedPageSize = Math.min(pageSize, MAX_LEADERBOARD_PAGE_SIZE);

        LeaderboardFilterOptions options = LeaderboardFilterOptions.builder()
                        .page(page)
                        .pageSize(parsedPageSize)
                        .query(query)
                        .patina(patina)
                        .hunter(hunter)
                        .nyu(nyu)
                        .build();

        List<UserWithScore> leaderboardData = leaderboardRepository.getLeaderboardUsersById(leaderboardId, options);

        int totalUsers = leaderboardRepository.getLeaderboardUserCountById(leaderboardId, options);
        int totalPages = (int) Math.ceil((double) totalUsers / parsedPageSize);
        boolean hasNextPage = page < totalPages;

        Page<List<UserWithScore>> createdPage = new Page<>(hasNextPage, leaderboardData, totalPages, MAX_LEADERBOARD_PAGE_SIZE);

        return ResponseEntity.ok().body(ApiResponder.success("All leaderboards found!", createdPage));
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
    public ResponseEntity<ApiResponder<Page<List<Indexed<UserWithScore>>>>> getCurrentLeaderboardUsers(final HttpServletRequest request,
                    @Parameter(description = "Page index", example = "1") @RequestParam(required = false, defaultValue = "1") final int page,
                    @Parameter(description = "Page size (maximum of " + MAX_LEADERBOARD_PAGE_SIZE) @RequestParam(required = false, defaultValue = "" + MAX_LEADERBOARD_PAGE_SIZE) final int pageSize,
                    @Parameter(description = "Discord name", example = "tahmid") @RequestParam(required = false, defaultValue = "") final String query,
                    @Parameter(description = "Filter for Patina users") @RequestParam(required = false, defaultValue = "false") final boolean patina,
                    @Parameter(description = "Filter for Hunter College users") @RequestParam(required = false, defaultValue = "false") final boolean hunter,
                    @Parameter(description = "Filter for NYU users") @RequestParam(required = false, defaultValue = "false") final boolean nyu,
                    @Parameter(description = "Enable global leaderboard index") @RequestParam(required = false, defaultValue = "false") final boolean globalIndex) {
        FakeLag.sleep(800);

        final int parsedPageSize = Math.min(pageSize, MAX_LEADERBOARD_PAGE_SIZE);

        LeaderboardFilterOptions options = LeaderboardFilterOptions.builder()
                        .page(page)
                        .pageSize(parsedPageSize)
                        .query(query)
                        .patina(patina)
                        .hunter(hunter)
                        .nyu(nyu)
                        .build();

        int totalUsers = leaderboardRepository.getRecentLeaderboardUserCount(options);
        int totalPages = (int) Math.ceil((double) totalUsers / parsedPageSize);
        boolean hasNextPage = page < totalPages;

        List<Indexed<UserWithScore>> leaderboardData;
        // don't use globalIndex when there are no filters enabled.
        if (globalIndex && (patina || nyu || hunter)) {
            String currentLeaderboardId = leaderboardRepository.getRecentLeaderboardMetadata().getId();
            leaderboardData = leaderboardRepository.getRankedLeaderboardUsersById(
                            leaderboardRepository.getRecentLeaderboardUsers(options),
                            currentLeaderboardId);
        } else {
            int startIndex = (page - 1) * parsedPageSize + 1;
            leaderboardData = Indexed.ofDefaultList(leaderboardRepository.getRecentLeaderboardUsers(options), startIndex);
        }

        Page<List<Indexed<UserWithScore>>> createdPage = new Page<>(hasNextPage, leaderboardData, totalPages, MAX_LEADERBOARD_PAGE_SIZE);

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

    @GetMapping("/all/metadata")
    @Operation(summary = "Returns the metadata for all leaderboards.", responses = { @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content(schema = @Schema(implementation = UnsafeGenericFailureResponse.class))) })
    public ResponseEntity<ApiResponder<Page<List<Leaderboard>>>> getAllLeaderboardMetadata(final HttpServletRequest request,
                    @Parameter(description = "Page index", example = "1") @RequestParam(required = false, defaultValue = "1") final int page,
                    @Parameter(description = "Question Title", example = "Two") @RequestParam(required = false, defaultValue = "") final String query,
                    @Parameter(description = "Page size (maximum of " + MAX_LEADERBOARD_PAGE_SIZE) @RequestParam(required = false, defaultValue = "" + MAX_LEADERBOARD_PAGE_SIZE) final int pageSize) {
        FakeLag.sleep(650);

        final int parsedPageSize = Math.min(pageSize, MAX_LEADERBOARD_PAGE_SIZE);

        LeaderboardFilterOptions options = LeaderboardFilterOptions.builder()
                        .page(page)
                        .pageSize(parsedPageSize)
                        .query(query)
                        .build();

        List<Leaderboard> leaderboardMetaData = leaderboardRepository.getAllLeaderboardsShallow(options);

        int totalLeaderboards = leaderboardRepository.getLeaderboardCount();
        int totalPages = (int) Math.ceil((double) totalLeaderboards / parsedPageSize);
        boolean hasNextPage = page < totalPages;

        Page<List<Leaderboard>> createdPage = new Page<>(hasNextPage, leaderboardMetaData, totalPages, parsedPageSize);

        return ResponseEntity.ok().body(ApiResponder.success("All leaderboard metadatas have been found!", createdPage));
    }
}
