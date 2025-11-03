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

import com.patina.codebloom.common.components.LeaderboardManager;
import com.patina.codebloom.common.db.models.leaderboard.Leaderboard;
import com.patina.codebloom.common.db.models.user.UserWithScore;
import com.patina.codebloom.common.db.repos.leaderboard.LeaderboardRepository;
import com.patina.codebloom.common.db.repos.leaderboard.options.LeaderboardFilterOptions;
import com.patina.codebloom.common.db.repos.user.UserRepository;
import com.patina.codebloom.common.db.repos.user.options.UserFilterOptions;
import com.patina.codebloom.common.dto.ApiResponder;
import com.patina.codebloom.common.dto.autogen.UnsafeGenericFailureResponse;
import com.patina.codebloom.common.dto.leaderboard.LeaderboardDto;
import com.patina.codebloom.common.dto.user.UserWithScoreDto;
import com.patina.codebloom.common.lag.FakeLag;
import com.patina.codebloom.common.security.AuthenticationObject;
import com.patina.codebloom.common.security.Protector;
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
    private final Protector protector;
    private final LeaderboardManager leaderboardManager;

    public LeaderboardController(final LeaderboardRepository leaderboardRepository, final UserRepository userRepository, final Protector protector, final LeaderboardManager leaderboardManager) {
        this.leaderboardRepository = leaderboardRepository;
        this.userRepository = userRepository;
        this.protector = protector;
        this.leaderboardManager = leaderboardManager;
    }

    @GetMapping("/{leaderboardId}/metadata")
    @Operation(summary = "Unprotected route that fetches metadata of a leaderboard via leaderboard ID.", responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content(schema = @Schema(implementation = UnsafeGenericFailureResponse.class))) })

    public ResponseEntity<ApiResponder<LeaderboardDto>> getLeaderboardMetadataByLeaderboardId(
                    final @PathVariable String leaderboardId,
                    final HttpServletRequest request) {
        FakeLag.sleep(650);

        Leaderboard leaderboardData = leaderboardManager.getLeaderboardMetadata(leaderboardId);

        if (leaderboardData == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Leaderboard cannot be found or does not exist.");
        }

        return ResponseEntity.ok().body(ApiResponder.success("Leaderboard metadata found!", LeaderboardDto.fromLeaderboard(leaderboardData)));
    }

    @GetMapping("/{leaderboardId}/user/all")
    @Operation(summary = "Unprotected route that fetches metadata of a leaderboard via leaderboard ID.", responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content(schema = @Schema(implementation = UnsafeGenericFailureResponse.class))) })

    public ResponseEntity<ApiResponder<Page<Indexed<UserWithScoreDto>>>> getLeaderboardUsersById(
                    @PathVariable final String leaderboardId,
                    @Parameter(description = "Page index", example = "1") @RequestParam(required = false, defaultValue = "1") final int page,
                    @Parameter(description = "Page size (maximum of " + MAX_LEADERBOARD_PAGE_SIZE) @RequestParam(required = false, defaultValue = "" + MAX_LEADERBOARD_PAGE_SIZE) final int pageSize,
                    @Parameter(description = "Discord name", example = "tahmid") @RequestParam(required = false, defaultValue = "") final String query,
                    @Parameter(description = "Filter for Patina users") @RequestParam(required = false, defaultValue = "false") final boolean patina,
                    @Parameter(description = "Filter for Hunter College users") @RequestParam(required = false, defaultValue = "false") final boolean hunter,
                    @Parameter(description = "Filter for NYU users") @RequestParam(required = false, defaultValue = "false") final boolean nyu,
                    @Parameter(description = "Filter for Baruch College users") @RequestParam(required = false, defaultValue = "false") final boolean baruch,
                    @Parameter(description = "Filter for RPI users") @RequestParam(required = false, defaultValue = "false") final boolean rpi,
                    @Parameter(description = "Filter for GWC users") @RequestParam(required = false, defaultValue = "false") final boolean gwc,
                    @Parameter(description = "Filter for SBU users") @RequestParam(required = false, defaultValue = "false") final boolean sbu,
                    @Parameter(description = "Filter for CCNY users") @RequestParam(required = false, defaultValue = "false") final boolean ccny,
                    @Parameter(description = "Filter for Columbia users") @RequestParam(required = false, defaultValue = "false") final boolean columbia,
                    @Parameter(description = "Filter for Cornell users") @RequestParam(required = false, defaultValue = "false") final boolean cornell,
                    @Parameter(description = "Filter for BMCC users") @RequestParam(required = false, defaultValue = "false") final boolean bmcc,
                    @Parameter(description = "Enable global leaderboard index") @RequestParam(required = false, defaultValue = "false") final boolean globalIndex,
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
                        .baruch(baruch)
                        .rpi(rpi)
                        .gwc(gwc)
                        .sbu(sbu)
                        .ccny(ccny)
                        .columbia(columbia)
                        .cornell(cornell)
                        .bmcc(bmcc)
                        .build();

        List<Indexed<UserWithScore>> leaderboardData;
        // don't use globalIndex when there are no filters enabled.
        if (globalIndex && (patina || nyu || hunter || baruch || rpi || gwc || sbu || ccny || columbia || cornell || bmcc)) {
            leaderboardData = leaderboardRepository.getGlobalRankedIndexedLeaderboardUsersById(
                            leaderboardId, options);
        } else {
            leaderboardData = leaderboardRepository.getRankedIndexedLeaderboardUsersById(
                            leaderboardId, options);
        }

        int totalUsers = leaderboardRepository.getLeaderboardUserCountById(leaderboardId, options);
        int totalPages = (int) Math.ceil((double) totalUsers / parsedPageSize);
        boolean hasNextPage = page < totalPages;

        List<Indexed<UserWithScoreDto>> indexedUserWithScoreDtos = leaderboardData.stream()
                        .map(indexed -> Indexed.of(UserWithScoreDto.fromUserWithScore(indexed.getItem()), indexed.getIndex()))
                        .toList();

        Page<Indexed<UserWithScoreDto>> createdPage = new Page<>(hasNextPage, indexedUserWithScoreDtos, totalPages, MAX_LEADERBOARD_PAGE_SIZE);

        return ResponseEntity.ok().body(ApiResponder.success("All leaderboards found!", createdPage));
    }

    @GetMapping("/current/metadata")
    @Operation(summary = "Unprotected route that fetches the currently active leaderboard data, attaching only the top 5 users for each leaderboard.", responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content(schema = @Schema(implementation = UnsafeGenericFailureResponse.class))) })

    public ResponseEntity<ApiResponder<LeaderboardDto>> getCurrentLeaderboardMetadata(final HttpServletRequest request) {
        FakeLag.sleep(650);

        Leaderboard leaderboardData = leaderboardManager.getLeaderboardMetadata();

        return ResponseEntity.ok().body(ApiResponder.success("All leaderboards found!", LeaderboardDto.fromLeaderboard(leaderboardData)));
    }

    @GetMapping("/current/user/all")
    @Operation(summary = "Fetch the currently active leaderboard data, attaching the users for each leaderboard.", responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content(schema = @Schema(implementation = UnsafeGenericFailureResponse.class))) })
    public ResponseEntity<ApiResponder<Page<Indexed<UserWithScoreDto>>>> getCurrentLeaderboardUsers(final HttpServletRequest request,
                    @Parameter(description = "Page index", example = "1") @RequestParam(required = false, defaultValue = "1") final int page,
                    @Parameter(description = "Page size (maximum of " + MAX_LEADERBOARD_PAGE_SIZE) @RequestParam(required = false, defaultValue = "" + MAX_LEADERBOARD_PAGE_SIZE) final int pageSize,
                    @Parameter(description = "Discord name", example = "tahmid") @RequestParam(required = false, defaultValue = "") final String query,
                    @Parameter(description = "Filter for Patina users") @RequestParam(required = false, defaultValue = "false") final boolean patina,
                    @Parameter(description = "Filter for Hunter College users") @RequestParam(required = false, defaultValue = "false") final boolean hunter,
                    @Parameter(description = "Filter for NYU users") @RequestParam(required = false, defaultValue = "false") final boolean nyu,
                    @Parameter(description = "Filter for Baruch College users") @RequestParam(required = false, defaultValue = "false") final boolean baruch,
                    @Parameter(description = "Filter for RPI users") @RequestParam(required = false, defaultValue = "false") final boolean rpi,
                    @Parameter(description = "Filter for GWC users") @RequestParam(required = false, defaultValue = "false") final boolean gwc,
                    @Parameter(description = "Filter for SBU users") @RequestParam(required = false, defaultValue = "false") final boolean sbu,
                    @Parameter(description = "Filter for CCNY users") @RequestParam(required = false, defaultValue = "false") final boolean ccny,
                    @Parameter(description = "Filter for Columbia users") @RequestParam(required = false, defaultValue = "false") final boolean columbia,
                    @Parameter(description = "Filter for Cornell users") @RequestParam(required = false, defaultValue = "false") final boolean cornell,
                    @Parameter(description = "Filter for BMCC users") @RequestParam(required = false, defaultValue = "false") final boolean bmcc,
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
                        .baruch(baruch)
                        .rpi(rpi)
                        .gwc(gwc)
                        .sbu(sbu)
                        .ccny(ccny)
                        .columbia(columbia)
                        .cornell(cornell)
                        .bmcc(bmcc)
                        .build();

        Leaderboard currLeaderboard = leaderboardManager.getLeaderboardMetadata();
        int totalUsers = leaderboardRepository.getLeaderboardUserCountById(currLeaderboard.getId(), options);
        int totalPages = (int) Math.ceil((double) totalUsers / parsedPageSize);
        boolean hasNextPage = page < totalPages;

        String currentLeaderboardId = leaderboardRepository.getRecentLeaderboardMetadata().getId();
        List<Indexed<UserWithScore>> leaderboardData;
        // don't use globalIndex when there are no filters enabled.
        if (globalIndex && (patina || nyu || hunter || baruch || rpi || gwc || sbu || ccny || columbia || cornell || bmcc)) {
            leaderboardData = leaderboardRepository.getGlobalRankedIndexedLeaderboardUsersById(
                            currentLeaderboardId, options);
        } else {
            leaderboardData = leaderboardRepository.getRankedIndexedLeaderboardUsersById(
                            currentLeaderboardId, options);
        }

        List<Indexed<UserWithScoreDto>> indexedUserWithScoreDtos = leaderboardData.stream()
                        .map(indexed -> Indexed.of(UserWithScoreDto.fromUserWithScore(indexed.getItem()), indexed.getIndex()))
                        .toList();

        Page<Indexed<UserWithScoreDto>> createdPage = new Page<>(hasNextPage, indexedUserWithScoreDtos, totalPages, MAX_LEADERBOARD_PAGE_SIZE);

        return ResponseEntity.ok().body(ApiResponder.success("All leaderboards found!", createdPage));
    }

    @GetMapping("/current/user/{userId}")
    @Operation(summary = "Fetch the specific user data in the currently active leaderboard data.", responses = { @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content(schema = @Schema(implementation = UnsafeGenericFailureResponse.class))) })
    public ResponseEntity<ApiResponder<UserWithScoreDto>> getUserCurrentLeaderboardFull(final HttpServletRequest request,
                    @PathVariable final String userId) {
        FakeLag.sleep(650);

        Leaderboard leaderboardData = leaderboardRepository.getRecentLeaderboardMetadata();

        // we do not support point of time in this endpoint currently
        UserWithScore user = userRepository.getUserWithScoreByIdAndLeaderboardId(
                        userId,
                        leaderboardData.getId(),
                        UserFilterOptions.builder()
                                        .build());

        // if (user == null) {
        // return
        // ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponder.failure("This
        // user does not exist on this leaderboard."));
        // }

        return ResponseEntity.ok().body(ApiResponder.success("User found!", UserWithScoreDto.fromUserWithScore(user)));
    }

    @GetMapping("/current/user/rank")
    @Operation(summary = "Fetch the authenticated user's current rank/position on the active leaderboard.", responses = {
            @ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content(schema = @Schema(implementation = UnsafeGenericFailureResponse.class))),
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user rank"),
            @ApiResponse(responseCode = "404", description = "User not found on leaderboard")
    })
    public ResponseEntity<ApiResponder<Indexed<UserWithScoreDto>>> getUserCurrentLeaderboardRank(
                    final HttpServletRequest request,
                    @Parameter(description = "Filter for Patina users") @RequestParam(required = false, defaultValue = "false") final boolean patina,
                    @Parameter(description = "Filter for Hunter College users") @RequestParam(required = false, defaultValue = "false") final boolean hunter,
                    @Parameter(description = "Filter for NYU users") @RequestParam(required = false, defaultValue = "false") final boolean nyu,
                    @Parameter(description = "Filter for Baruch College users") @RequestParam(required = false, defaultValue = "false") final boolean baruch,
                    @Parameter(description = "Filter for RPI users") @RequestParam(required = false, defaultValue = "false") final boolean rpi,
                    @Parameter(description = "Filter for GWC users") @RequestParam(required = false, defaultValue = "false") final boolean gwc,
                    @Parameter(description = "Filter for SBU users") @RequestParam(required = false, defaultValue = "false") final boolean sbu,
                    @Parameter(description = "Filter for CCNY users") @RequestParam(required = false, defaultValue = "false") final boolean ccny,
                    @Parameter(description = "Filter for Cornell users") @RequestParam(required = false, defaultValue = "false") final boolean cornell,
                    @Parameter(description = "Filter for Columbia users") @RequestParam(required = false, defaultValue = "false") final boolean columbia,
                    @Parameter(description = "Filter for BMCC users") @RequestParam(required = false, defaultValue = "false") final boolean bmcc) {
        FakeLag.sleep(650);

        AuthenticationObject authenticationObject = protector.validateSession(request);
        String userId = authenticationObject.getUser().getId();

        Leaderboard leaderboardData = leaderboardRepository.getRecentLeaderboardMetadata();

        if (leaderboardData == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No active leaderboard found.");
        }

        Indexed<UserWithScore> userWithRank;

        if (!patina && !hunter && !nyu && !baruch && !rpi && !gwc && !sbu && !ccny && !columbia && !cornell && !bmcc) {
            // Use global ranking when no filters are applied
            userWithRank = leaderboardRepository.getGlobalRankedUserById(leaderboardData.getId(), userId);
        } else {
            // Use filtered ranking when filters are applied
            LeaderboardFilterOptions options = LeaderboardFilterOptions.builder()
                            .patina(patina)
                            .hunter(hunter)
                            .nyu(nyu)
                            .baruch(baruch)
                            .rpi(rpi)
                            .gwc(gwc)
                            .sbu(sbu)
                            .ccny(ccny)
                            .columbia(columbia)
                            .cornell(cornell)
                            .bmcc(bmcc)
                            .build();
            userWithRank = leaderboardRepository.getFilteredRankedUserById(leaderboardData.getId(), userId, options);
        }

        if (userWithRank == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found on the current leaderboard or does not match the specified filters.");
        }

        Indexed<UserWithScoreDto> indexedUserWithScoreDto = Indexed.of(UserWithScoreDto.fromUserWithScore(userWithRank.getItem()), userWithRank.getIndex());

        return ResponseEntity.ok().body(ApiResponder.success("User rank found!", indexedUserWithScoreDto));
    }

    @GetMapping("/all/metadata")
    @Operation(summary = "Returns the metadata for all leaderboards.", responses = { @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content(schema = @Schema(implementation = UnsafeGenericFailureResponse.class))) })
    public ResponseEntity<ApiResponder<Page<LeaderboardDto>>> getAllLeaderboardMetadata(final HttpServletRequest request,
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

        List<LeaderboardDto> leaderboardDtos = leaderboardMetaData.stream().map(l -> LeaderboardDto.fromLeaderboard(l)).toList();

        Page<LeaderboardDto> createdPage = new Page<>(hasNextPage, leaderboardDtos, totalPages, parsedPageSize);

        return ResponseEntity.ok().body(ApiResponder.success("All leaderboard metadatas have been found!", createdPage));
    }
}
