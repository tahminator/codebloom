package com.patina.codebloom.api.auth;

import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.patina.codebloom.api.auth.infra.session.SessionRepository;
import com.patina.codebloom.api.auth.infra.user.UserRepository;
import com.patina.codebloom.common.db.models.Session;
import com.patina.codebloom.common.db.models.User;
import com.patina.codebloom.common.dto.ApiResponse;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;

    public AuthController(@Qualifier("UserSqlRepository") UserRepository userRepository,
            @Qualifier("SessionSqlRepository") SessionRepository sessionRepository) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
    }

    @GetMapping("/validate")
    public ResponseEntity<ApiResponse<Map<String, Object>>> validateAuth(
            @CookieValue(value = "session_token", required = false) String sessionToken) {
        try {
            if (sessionToken == null) {
                return ResponseEntity.status(401)
                        .body(new ApiResponse<Map<String, Object>>(false, "You are not logged in.", null));
            }

            Session session = sessionRepository.getSessionById(sessionToken);
            if (session == null) {
                return ResponseEntity.status(401)
                        .body(new ApiResponse<Map<String, Object>>(false, "You are not logged in.", null));
            }
            User user = userRepository.getUserById(session.getUserId());
            if (user == null) {
                throw new RuntimeException("Error that should not be happening: session found but not user");
            }

            Map<String, Object> data = Map.of("session", session, "user", user);

            return ResponseEntity.ok().body(new ApiResponse<Map<String, Object>>(true, "You are logged in!", data));

        } catch (Exception e) {
            throw new RuntimeException("Error while validating authentication", e);
        }

    }

    // TODO - Decide how we are actually going to do auth, and logout specifically
    @GetMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @CookieValue(value = "session_token", required = false) String sessionToken) {
        if (sessionToken == null) {
            return ResponseEntity.status(401).body(new ApiResponse<>(false, "You are not logged in.", null));
        }

        Session session = sessionRepository.getSessionById(sessionToken);
        if (session == null) {
            return ResponseEntity.status(401)
                    .body(new ApiResponse<>(false, "You are not logged in.", null));
        }

        User user = userRepository.getUserById(session.getUserId());
        if (user == null) {
            throw new RuntimeException("Error that should not be happening: session found but not user");
        }

        Boolean sessionDeleted = sessionRepository.deleteSessionById(session.getId());

        if (!sessionDeleted.booleanValue()) {
            return ResponseEntity.status(500).body(new ApiResponse<>(false, "Internal server error."));
        }

        ResponseCookie strippedCookie = ResponseCookie.from("session_token", "").path("/").httpOnly(true).maxAge(0)
                .build();

        return ResponseEntity.ok().header("Set-Cookie", strippedCookie.toString())
                .body(new ApiResponse<>(true, "You have been logged out!", null));
    }

}
