package com.patina.codebloom.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import com.patina.codebloom.common.lag.FakeLag;
import com.patina.codebloom.dto.ApiResponder;
import com.patina.codebloom.dto._autogen.UnsafeGenericFailureResponse;
import com.patina.codebloom.dto.request.AuthBody;
import com.patina.codebloom.models.Session;
import com.patina.codebloom.models.user.User;
import com.patina.codebloom.repos.session.SessionRepository;
import com.patina.codebloom.repos.user.UserRepository;
import com.patina.codebloom.security.AuthenticationObject;
import com.patina.codebloom.security.Protector;

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
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final Protector protector;

    public AuthController(final UserRepository userRepository, final SessionRepository sessionRepository, final Protector protector) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.protector = protector;
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

    @Operation(summary = "Change nickname", description = """
            Change the user's nickname. If the user is in the Patina Discord server,
            and they change their nickname here to be different from their Discord nickname in the server,
            it will no longer do that sync on every re-authentication.
            """, responses = { @ApiResponse(responseCode = "200", description = "Name change complete"),
            @ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content(schema = @Schema(implementation = UnsafeGenericFailureResponse.class))) })
    @PostMapping("/set")
    public ResponseEntity<ApiResponder<Void>> setNickname(final HttpServletRequest request, @RequestBody final AuthBody authBody) {
        AuthenticationObject authenticationObject = protector.validateSession(request);

        User user = authenticationObject.getUser();

        String nickname = authBody.getNickname();
        user.setNickname(nickname == "" ? null : nickname);
        userRepository.updateUser(user);

        return ResponseEntity.ok().body(ApiResponder.success(nickname, null));
    }

}
