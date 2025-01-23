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
import com.patina.codebloom.common.dto.ApiResponder;
import com.patina.codebloom.common.dto.autogen.__DO_NOT_USE_UNLESS_YOU_KNOW_WHAT_YOU_ARE_DOING_GENERIC_FAILURE_RESPONSE;
import com.patina.codebloom.common.lag.FakeLag;
import com.patina.codebloom.common.security.AuthenticationObject;
import com.patina.codebloom.common.security.Protector;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@Tag(name = "Authentication Routes")
@RequestMapping("/api/auth")
public class AuthController {
    private final SessionRepository sessionRepository;
    private final Protector protector;

    public AuthController(SessionRepository sessionRepository, Protector protector) {
        this.sessionRepository = sessionRepository;
        this.protector = protector;
    }

    @Operation(summary = "Validate if the user is authenticated or not.", responses = {
            @ApiResponse(responseCode = "200", description = "Authenticated"),
            @ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content(schema = @Schema(implementation = __DO_NOT_USE_UNLESS_YOU_KNOW_WHAT_YOU_ARE_DOING_GENERIC_FAILURE_RESPONSE.class)))
    })
    @GetMapping("/validate")
    public ResponseEntity<ApiResponder<AuthenticationObject>> validateAuth(HttpServletRequest request) {
        FakeLag.sleep(350);

        AuthenticationObject authenticationObject = protector.validateSession(request);

        return ResponseEntity.ok().body(ApiResponder.success("You are authenticated!", authenticationObject));
    }

    @Operation(summary = "Logs user out", description = "Logs the user out if currently authenticated. This is a Redirect route that does redirects as responses.", responses = {
            @ApiResponse(responseCode = "302", description = "Redirect to `/login?success=true&message=\"Successful logout message here.\"` on successful authentication.", content = @Content())
    })
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
