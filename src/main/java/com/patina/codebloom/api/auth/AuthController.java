package com.patina.codebloom.api.auth;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import com.patina.codebloom.common.db.models.Session;
import com.patina.codebloom.common.db.repos.session.SessionRepository;
import com.patina.codebloom.common.dto.ApiResponse;
import com.patina.codebloom.common.security.AuthenticationObject;
import com.patina.codebloom.common.security.Protector;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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

    // Decided to make this redirect to routes, with a message query if needed,
    // keeping it inline with the logic of the authentication handler.
    @GetMapping("/logout")
    public RedirectView logout(HttpServletRequest request, HttpServletResponse response) {
        try {
            AuthenticationObject authenticationObject = protector.validateSession(request);

            Session session = authenticationObject.getSession();

            boolean sessionDeleted = sessionRepository.deleteSessionById(session.getId());

            if (sessionDeleted == false) {
                return new RedirectView("/login?success=false&message=You are not logged in.");

            }

            ResponseCookie strippedCookie = ResponseCookie.from("session_token", "").path("/").httpOnly(true).maxAge(0)
                    .build();
            response.addHeader(HttpHeaders.SET_COOKIE, strippedCookie.toString());

            return new RedirectView("/login?success=true&message=You have been logged out!");
        } catch (Exception e) {
            return new RedirectView("/login?success=false&message=You are not logged in.");
        }

    }

}
