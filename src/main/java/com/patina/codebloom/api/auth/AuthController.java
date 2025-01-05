package com.patina.codebloom.api.auth;

import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.patina.codebloom.common.db.models.Session;
import com.patina.codebloom.common.db.repos.session.SessionRepository;
import com.patina.codebloom.common.dto.ApiResponse;
import com.patina.codebloom.common.security.AuthenticationObject;
import com.patina.codebloom.common.security.Protector;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final SessionRepository sessionRepository;
    private final Protector protector;

    public AuthController(SessionRepository sessionRepository, Protector protector) {
        this.sessionRepository = sessionRepository;
        this.protector = protector;
    }

    @GetMapping("/validate")
    public ResponseEntity<ApiResponse<AuthenticationObject>> validateAuth(HttpServletRequest request) {
        AuthenticationObject authenticationObject = protector.validateSession(request);

        return ResponseEntity.ok().body(ApiResponse.success("You are authenticated!", authenticationObject));
    }

    // TODO - Decide how we are actually going to do auth, and logout specifically
    @GetMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request) {
        AuthenticationObject authenticationObject = protector.validateSession(request);

        Session session = authenticationObject.getSession();

        boolean sessionDeleted = sessionRepository.deleteSessionById(session.getId());

        if (sessionDeleted == false) {
            return ResponseEntity.status(500).body(ApiResponse.failure("You are not logged in."));
        }

        ResponseCookie strippedCookie = ResponseCookie.from("session_token", "").path("/").httpOnly(true).maxAge(0)
                .build();

        return ResponseEntity.ok().header("Set-Cookie", strippedCookie.toString())
                .body(ApiResponse.success("You have been successfully logged out!", null));

    }

}
