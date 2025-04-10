package com.patina.codebloom.api.admin;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.patina.codebloom.api.admin.body.NewLeaderboardBody;
import com.patina.codebloom.api.admin.body.UpdateAdminBody;
import com.patina.codebloom.common.db.models.leaderboard.Leaderboard;
import com.patina.codebloom.common.db.models.user.User;
import com.patina.codebloom.common.db.repos.leaderboard.LeaderboardRepository;
import com.patina.codebloom.common.db.repos.user.UserRepository;
import com.patina.codebloom.common.dto.ApiResponder;
import com.patina.codebloom.common.security.Protector;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@Tag(name = "Admin routes", description = "This controller is responsible for handling all admin routes.")
@RequestMapping("/api/admin")
public class AdminController {

    private final LeaderboardRepository leaderboardRepository;
    private final Protector protector;

    public AdminController(final LeaderboardRepository leaderboardRepository, final Protector protector) {
        this.leaderboardRepository = leaderboardRepository;
        this.protector = protector;
        this.userRepository = null;
    }

    @Operation(summary = "Drops current leaderboard and add new one", description = """
                        BE SUPER CAREFUL WITH THIS ROUTE!!!!!!! It will drop the current leaderboard and add a new leaderboard based on the given parameters.
                    """)
    @PostMapping("/leaderboard/create")
    public ResponseEntity<ApiResponder<Void>> createLeaderboard(
                    final HttpServletRequest request,
                    @Valid @RequestBody final NewLeaderboardBody newLeaderboardBody) {

        // This checks if user is an admin.
        protector.validateAdminSession(request);

        final String name = newLeaderboardBody.getName().trim();

        // This checks if the leaderboard name is not an empty string or longer than 512
        // characters.

        if (name.isEmpty() || name.length() > 512) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(ApiResponder.failure("Leaderboard name must be between 1 and 512 characters."));
        }

        // This checks if there is no current leaderboard.

        // BE VERY CAREFUL WITH THIS ROUTE. IT WILL DEACTIVATE THE PREVIOUS LEADERBOARD
        // (however, it is in a recoverable state).
        Leaderboard currentLeaderboard = leaderboardRepository.getRecentLeaderboardMetadata();
        if (currentLeaderboard != null) {
            leaderboardRepository.disableLeaderboardById(currentLeaderboard.getId());
        }

        // If there is no current leaderboard, then a new leaderboard is created.

        Leaderboard newLeaderboard = new Leaderboard(name, null);
        newLeaderboard.setId(UUID.randomUUID().toString());
        newLeaderboard.setName(name);
        newLeaderboard.setCreatedAt(LocalDateTime.now());

        boolean success = leaderboardRepository.addNewLeaderboard(newLeaderboard);
        if (!success) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(ApiResponder.failure("Failed to create leaderboard due to an internal error."));
        }
        leaderboardRepository.addAllUsersToLeaderboard(newLeaderboard.getId());

        return ResponseEntity.ok(ApiResponder.success("Leaderboard was created successfully.", null));
    }

    private final UserRepository userRepository;

    public AdminController(
                    final Protector protector,
                    final UserRepository userRepository) {
        this.leaderboardRepository = null;
        this.userRepository = userRepository;
        this.protector = protector;
    }

    @Operation(summary = "Allows current admin to toggle another user's admin status", description = """
                        BE SUPER CAREFUL WITH THIS ROUTE!!!!!!! It allows an existing admin to give another user admin.
                    """)
    @PostMapping("/user/admin/toggle")
    public ResponseEntity<ApiResponder<Void>> updateAdmin(
                    final HttpServletRequest request,
                    @Valid @RequestBody final UpdateAdminBody newAdminBody) {

        // This checks if user is an admin.
        protector.validateAdminSession(request);

        final String userId = newAdminBody.getId();
        final boolean toggleTo = newAdminBody.getToggleTo();

        User user = userRepository.getUserById(userId);

        // This checks if the user exists.
        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(ApiResponder.failure("User has not been found."));
        }

        // This sets the toggle and updates the adminif the user exists.
        user.setAdmin(toggleTo);
        User updatedUser = userRepository.updateUser(user);

        if (updatedUser == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(ApiResponder.failure("Failed to update the admin."));
        }

        return ResponseEntity.ok(ApiResponder.success("Admin status was updated successfully.", null));
    }
}
