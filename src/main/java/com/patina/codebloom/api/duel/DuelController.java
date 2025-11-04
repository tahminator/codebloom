package com.patina.codebloom.api.duel;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.patina.codebloom.common.components.DuelManager;
import com.patina.codebloom.common.dto.ApiResponder;
import com.patina.codebloom.common.dto.Empty;
import com.patina.codebloom.common.env.Env;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Live duel routes", description = """
                This controller houses the logic for live Leetcode duels. """)
@RequestMapping("/api/duel")
public class DuelController {

    private final Env env;
    private final DuelManager duelManager;

    public DuelController(final Env env, final DuelManager duelManager) {
        this.env = env;
        this.duelManager = duelManager;
    }

    @Operation(summary = "Join party", description = "WIP")
    @ApiResponse(responseCode = "403", description = "Endpoint is currently non-functional")
    @PostMapping("/party/join")
    public ResponseEntity<ApiResponder<Empty>> joinParty() {
        if (env.isProd()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Endpoint is currently non-functional");
        }

        return ResponseEntity.ok(ApiResponder.success("ok", Empty.of()));
    }

    @Operation(summary = "Leave party", description = "WIP")
    @ApiResponse(responseCode = "403", description = "Endpoint is currently non-functional")
    @PostMapping("/party/leave")
    public ResponseEntity<ApiResponder<Empty>> leaveParty() {
        if (env.isProd()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Endpoint is currently non-functional");
        }

        return ResponseEntity.ok(ApiResponder.success("ok", Empty.of()));
    }

    @Operation(summary = "Create party", description = "WIP")
    @ApiResponse(responseCode = "403", description = "Endpoint is currently non-functional")
    @PostMapping("/party/create")
    public ResponseEntity<ApiResponder<Empty>> createParty() {
        if (env.isProd()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Endpoint is currently non-functional");
        }

        return ResponseEntity.ok(ApiResponder.success("ok", Empty.of()));
    }
}
