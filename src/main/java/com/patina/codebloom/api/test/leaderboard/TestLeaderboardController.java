package com.patina.codebloom.api.test.leaderboard;

import java.util.ArrayList;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.patina.codebloom.common.dto.ApiResponder;
import com.patina.codebloom.common.security.Protector;
import com.patina.codebloom.common.test.TestLeaderboardList;
import com.patina.codebloom.common.test.models.UserAndMetadata;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/test/leaderboard")
@Tag(name = "Test data routes")
public class TestLeaderboardController {
    private final Protector protector;

    public TestLeaderboardController(Protector protector) {
        this.protector = protector;
    }

    @GetMapping("/shallow")
    @Operation(summary = "Returns the top 5 leaderboard positions in order.")
    public ResponseEntity<?> getShallowLeaderboard() {
        ArrayList<UserAndMetadata> leaderboard = TestLeaderboardList.getTestDataShallowList();

        return ResponseEntity.ok().body(ApiResponder.success("Leaderboard found!", leaderboard));
    }

    @GetMapping("/all")
    public ResponseEntity<?> getFullLeaderboard(HttpServletRequest request) {
        protector.validateSession(request);

        ArrayList<UserAndMetadata> leaderboard = TestLeaderboardList.getTestDataLongList();

        return ResponseEntity.ok().body(ApiResponder.success("Leaderboard found!", leaderboard));
    }
}
