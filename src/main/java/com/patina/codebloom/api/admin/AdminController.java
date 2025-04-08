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
import com.patina.codebloom.common.db.models.leaderboard.Leaderboard;
import com.patina.codebloom.common.db.repos.leaderboard.LeaderboardRepository;
import com.patina.codebloom.common.dto.ApiResponder;
import com.patina.codebloom.common.security.Protector;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@Tag(name = "Admin routes", description = "This controller is responsible for handling all admin routes.")
@RequestMapping("/api/admin")
public class AdminController {

    private final LeaderboardRepository leaderboardRepository;
    private final Protector protector;

    public AdminController(final LeaderboardRepository leaderboardRepository,final Protector protector) {
        this.leaderboardRepository = leaderboardRepository;
        this.protector = protector;
    }

    @PostMapping("/leaderboard/create")
    public ResponseEntity<ApiResponder<Void>> createLeaderboard(
                    final HttpServletRequest request,
                    @Valid @RequestBody final NewLeaderboardBody newLeaderboardBody) {
        /**
         * This checks if user is an admin.
         */
        protector.validateAdminSession(request);

        final String name = newLeaderboardBody.getName().trim();

        /**
         * This checks if the leaderboard name is not an empty string or longer than 512
         * characters.
         */

        if (name.isEmpty() || name.length() > 512) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(ApiResponder.failure("Leaderboard name must be between 1 and 512 characters."));
        }

        /**
         * This checks if there is no current leaderboard.
         */

        Leaderboard currentLeaderboard = leaderboardRepository.getRecentLeaderboardMetadata();
        if (currentLeaderboard != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                            .body(ApiResponder.failure("A leaderboard is currently active. Please deactivate it before creating a new one."));
        }

        /**
         * If there is no current leaderboard, then a new leaderboard is created.
         */

        Leaderboard newLeaderboard = new Leaderboard(name, null);
        newLeaderboard.setId(UUID.randomUUID().toString());
        newLeaderboard.setName(name);
        newLeaderboard.setCreatedAt(LocalDateTime.now());

        boolean success = leaderboardRepository.addNewLeaderboard(newLeaderboard) != null;
        if (!success) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(ApiResponder.failure("Failed to create leaderboard due to an internal error."));
        }
        leaderboardRepository.addAllUsersToLeaderboard(newLeaderboard.getId());

        return ResponseEntity.ok(ApiResponder.success("Leaderboard was created successfully.", null));
    }
}
