package com.patina.codebloom.api.auth;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import com.patina.codebloom.common.db.models.Session;
import com.patina.codebloom.common.db.repos.session.SessionRepository;
import com.patina.codebloom.common.dto.ApiResponder;
import com.patina.codebloom.common.dto.autogen.UnsafeGenericFailureResponse;
import com.patina.codebloom.common.jwt.JWTClient;
import com.patina.codebloom.common.lag.FakeLag;
import com.patina.codebloom.common.schools.SupportedSchools;
import com.patina.codebloom.common.schools.magic.MagicLink;
import com.patina.codebloom.common.security.AuthenticationObject;
import com.patina.codebloom.common.security.Protector;
import com.patina.codebloom.api.auth.body.EmailBody;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import com.patina.codebloom.common.db.models.user.User;
import com.patina.codebloom.common.db.repos.user.UserRepository;

@RestController
@Tag(name = "Authentication Routes")
@RequestMapping("/api/auth")
public class AuthController {
    private final SessionRepository sessionRepository;
    private final Protector protector;
    private final JWTClient jwtClient;
    private final UserRepository userRepository;

    public AuthController(final SessionRepository sessionRepository, final Protector protector, final JWTClient jwtClient, final UserRepository userRepository) {
        this.sessionRepository = sessionRepository;
        this.protector = protector;
        this.userRepository = userRepository;
        this.jwtClient = jwtClient;

    }

    @Operation(summary = "Validate if the user is authenticated or not.", responses = { @ApiResponse(responseCode = "200", description = "Authenticated"),
            @ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content(schema = @Schema(implementation = UnsafeGenericFailureResponse.class))) })
    @GetMapping("/validate")
    public ResponseEntity<ApiResponder<AuthenticationObject>> validateAuth(final HttpServletRequest request) {
        FakeLag.sleep(350);

        AuthenticationObject authenticationObject = protector.validateSession(request);

        return ResponseEntity.ok().body(ApiResponder.success("You are authenticated!", authenticationObject));
    }

    // Decided to make this redirect to routes, with a message query if needed,
    // keeping it inline with the logic of the authentication handler.
    @Operation(summary = "Logs user out", description = "Logs the user out if currently authenticated. This is a Redirect route that does redirects as responses.", responses = {
            @ApiResponse(responseCode = "302", description = "Redirect to `/login?success=true&message=\"Successful logout message here.\"` on successful authentication.", content = @Content()) })
    @GetMapping("/logout")
    public RedirectView logout(final HttpServletRequest request, final HttpServletResponse response) {
        try {
            AuthenticationObject authenticationObject = protector.validateSession(request);

            Session session = authenticationObject.getSession();

            boolean sessionDeleted = sessionRepository.deleteSessionById(session.getId());

            if (!sessionDeleted) {
                return new RedirectView("/login?success=false&message=You are not logged in.");
            }

            ResponseCookie strippedCookie = ResponseCookie.from("session_token", "").path("/").httpOnly(true).maxAge(0).build();
            response.addHeader(HttpHeaders.SET_COOKIE, strippedCookie.toString());

            return new RedirectView("/login?success=true&message=You have been logged out!");
        } catch (Exception e) {
            return new RedirectView("/login?success=false&message=You are not logged in.");
        }
    }

    @Operation(summary = "Enroll with a school email (if supported)", description = """
                    Allows users to submit a school-specific email if supported. Emails will be verified with a magic link sent to their email.
                        """, responses = {
            @ApiResponse(responseCode = "500", description = "not implemented")
    })
    @PostMapping("/school/enroll")
    public ResponseEntity<ApiResponder<Object>> enrollSchool(@Valid @RequestBody final EmailBody emailBody, final HttpServletRequest request) {
        AuthenticationObject authenticationObject = protector.validateSession(request);
        String email = emailBody.getEmail();
        String domain = email.substring(email.indexOf("@")).toLowerCase();
        Set<String> supportedDomains = SupportedSchools.getList().stream()
                        .map(school -> school.getEmailDomain())
                        .collect(Collectors.toSet());

        if (!supportedDomains.contains(domain)) {
            String supportedSchools = String.join(", ", supportedDomains);
            throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "The email is not part of our supported schools domains: " + supportedSchools);
        }

        User user = authenticationObject.getUser();
        String userId = user.getId();

        MagicLink magicLink = new MagicLink(email, userId);
        // TODO - Integrate email client to send the magic link with the token
        try {
            String token = jwtClient.encode(magicLink);
        } catch (Exception e) {
            throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Error processing request: not implemented");
        }

        return ResponseEntity.status(500).body(ApiResponder.failure("not implemented"));
    }
}
