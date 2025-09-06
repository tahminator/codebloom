package com.patina.codebloom.api.admin;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.patina.codebloom.api.admin.body.CreateAnnouncementBody;
import com.patina.codebloom.api.admin.body.DeleteAnnouncementBody;
import com.patina.codebloom.api.admin.body.NewLeaderboardBody;
import com.patina.codebloom.api.admin.body.UpdateAdminBody;
import com.patina.codebloom.api.admin.helper.PatinaDiscordMessageHelper;
import com.patina.codebloom.common.db.models.announcement.Announcement;
import com.patina.codebloom.common.db.models.leaderboard.Leaderboard;
import com.patina.codebloom.common.db.models.user.User;
import com.patina.codebloom.common.db.repos.announcement.AnnouncementRepository;
import com.patina.codebloom.common.db.repos.leaderboard.LeaderboardRepository;
import com.patina.codebloom.common.db.repos.user.UserRepository;
import com.patina.codebloom.common.dto.ApiResponder;
import com.patina.codebloom.common.dto.Empty;
import com.patina.codebloom.common.dto.autogen.UnsafeGenericFailureResponse;
import com.patina.codebloom.common.security.Protector;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@Tag(name = "Admin routes", description = "This controller is responsible for handling all admin routes.")
@RequestMapping("/api/admin")
public class AdminController {

    private final UserRepository userRepository;
    private final LeaderboardRepository leaderboardRepository;
    private final AnnouncementRepository announcementRepository;
    private final Protector protector;
    private final PatinaDiscordMessageHelper patinaDiscordMessageHelper;

    public AdminController(
                    final LeaderboardRepository leaderboardRepository,
                    final Protector protector,
                    final UserRepository userRepository,
                    final AnnouncementRepository announcementRepository,
                    final PatinaDiscordMessageHelper patinaDiscordMessageHelper) {
        this.leaderboardRepository = leaderboardRepository;
        this.protector = protector;
        this.userRepository = userRepository;
        this.announcementRepository = announcementRepository;
        this.patinaDiscordMessageHelper = patinaDiscordMessageHelper;
    }

    @Operation(summary = "Drops current leaderboard and add new one", description = """
                        BE SUPER CAREFUL WITH THIS ROUTE!!!!!!! It will drop the current leaderboard and add a new leaderboard based on the given parameters.
                    """)
    @PostMapping("/leaderboard/create")
    public ResponseEntity<ApiResponder<Empty>> createLeaderboard(
                    final HttpServletRequest request,
                    @Valid @RequestBody final NewLeaderboardBody newLeaderboardBody) {
        protector.validateAdminSession(request);

        final String name = newLeaderboardBody.getName().trim();

        if (name.isEmpty() || name.length() > 512) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(ApiResponder.failure("Leaderboard name must be between 1 and 512 characters."));
        }

        // BE VERY CAREFUL WITH THIS ROUTE. IT WILL DEACTIVATE THE PREVIOUS LEADERBOARD
        // (however, it should be in a recoverable state, as it just gets toggled to be
        // deactivated, not deleted).
        Leaderboard currentLeaderboard = leaderboardRepository.getRecentLeaderboardMetadata();
        if (currentLeaderboard != null) {
            patinaDiscordMessageHelper.sendLatestLeaderboardDiscordMessage();
            leaderboardRepository.disableLeaderboardById(currentLeaderboard.getId());
        }

        // TODO - Implement the logic to support shouldExpireBy
        Leaderboard newLeaderboard = Leaderboard.builder()
                        .name(name)
                        .build();

        leaderboardRepository.addNewLeaderboard(newLeaderboard);
        leaderboardRepository.addAllUsersToLeaderboard(newLeaderboard.getId());

        return ResponseEntity.ok(ApiResponder.success("Leaderboard was created successfully.", Empty.of()));
    }

    @Operation(summary = "Allows current admin to toggle another user's admin status", description = """
                    """)
    @PostMapping("/user/admin/toggle")
    public ResponseEntity<ApiResponder<User>> updateAdmin(
                    final HttpServletRequest request,
                    @Valid @RequestBody final UpdateAdminBody newAdminBody) {
        protector.validateAdminSession(request);

        final String userId = newAdminBody.getId();
        final boolean toggleTo = newAdminBody.getToggleTo();

        User user = userRepository.getUserById(userId);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(ApiResponder.failure("User has not been found."));
        }

        user.setAdmin(toggleTo);
        User updatedUser = userRepository.updateUser(user);

        if (updatedUser == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(ApiResponder.failure("Failed to update the admin."));
        }

        return ResponseEntity.ok(ApiResponder.success("User with Discord name of "
                        + updatedUser.getDiscordName() + " is "
                        + (toggleTo ? "now an admin!" : "no longer an admin."), updatedUser));
    }

    @Operation(summary = "Create a new announcement (only for admins).", responses = {
            @ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content(schema = @Schema(implementation = UnsafeGenericFailureResponse.class))),
            @ApiResponse(responseCode = "200", description = "Announcement successfully created"),
            @ApiResponse(responseCode = "500", description = "Something went wrong", content = @Content(schema = @Schema(implementation = UnsafeGenericFailureResponse.class)))
    })
    @PostMapping("/announcement/create")
    public ResponseEntity<ApiResponder<Announcement>> createNewAnnouncement(
                    @Valid @RequestBody final CreateAnnouncementBody createAnnouncementBody,
                    final HttpServletRequest request) {
        protector.validateAdminSession(request);

        boolean isInFuture = LocalDateTime.now().isBefore(createAnnouncementBody.getExpiresAt());

        if (!isInFuture) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The expiration date must be in the future.");
        }

        Announcement announcement = Announcement.builder()
                        .expiresAt(createAnnouncementBody.getExpiresAt())
                        .showTimer(createAnnouncementBody.isShowTimer())
                        .message(createAnnouncementBody.getMessage())
                        .build();

        boolean isSuccessful = announcementRepository.createAnnouncement(announcement);

        if (!isSuccessful) {
            return ResponseEntity
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(ApiResponder.failure("Hmm, something went wrong."));
        }

        return ResponseEntity.ok(ApiResponder.success("New announcement successfully created!", announcement));
    }

    @Operation(summary = "Create a delete announcement if exist (only for admins).", responses = {
            @ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content(schema = @Schema(implementation = UnsafeGenericFailureResponse.class))),
            @ApiResponse(responseCode = "200", description = "Announcement successfully Deleted"),
            @ApiResponse(responseCode = "500", description = "Something went wrong", content = @Content(schema = @Schema(implementation = UnsafeGenericFailureResponse.class)))
    })
    @PostMapping("/announcement/disable")
    public ResponseEntity<ApiResponder<Announcement>> deleteAnnouncement(@Valid @RequestBody final DeleteAnnouncementBody deleteAnnouncementBody, final HttpServletRequest request) {
        protector.validateAdminSession(request);
        Announcement announcement = announcementRepository.getAnnouncementById(deleteAnnouncementBody.getId());
        if (announcement == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Announcement does not exist");
        }
        announcement.setShowTimer(false);
        announcement.setExpiresAt(LocalDateTime.now());
        Announcement updatedAnnouncement = announcementRepository.updateAnnouncement(announcement);

        if (updatedAnnouncement == null) {
            return ResponseEntity
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(ApiResponder.failure("Hmm, something went wrong."));
        }
        return ResponseEntity.ok(ApiResponder.success("Announcement successfully disabled!", updatedAnnouncement));
    }

}
