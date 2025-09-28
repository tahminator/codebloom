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
import com.patina.codebloom.common.dto.Empty;
import com.patina.codebloom.common.dto.autogen.UnsafeGenericFailureResponse;
import com.patina.codebloom.common.jwt.JWTClient;
import com.patina.codebloom.common.lag.FakeLag;
import com.patina.codebloom.common.reporter.Reporter;
import com.patina.codebloom.common.reporter.report.Report;
import com.patina.codebloom.common.schools.SchoolEnum;
import com.patina.codebloom.common.schools.magic.MagicLink;
import com.patina.codebloom.common.security.AuthenticationObject;
import com.patina.codebloom.common.security.Protector;
import com.patina.codebloom.common.email.options.SendEmailOptions;
import com.patina.codebloom.api.auth.body.EmailBody;
import com.patina.codebloom.common.email.client.codebloom.OfficialCodebloomEmail;
import com.patina.codebloom.common.email.error.EmailException;
import com.patina.codebloom.common.url.ServerUrlUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import java.time.Duration;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import com.patina.codebloom.common.db.models.user.PrivateUser;
import com.patina.codebloom.common.db.repos.user.UserRepository;
import com.patina.codebloom.common.db.repos.usertag.UserTagRepository;
import com.patina.codebloom.common.db.models.usertag.UserTag;

@RestController
@Tag(name = "Authentication Routes")
@RequestMapping("/api/auth")
public class AuthController {
    private final SessionRepository sessionRepository;
    private final Protector protector;
    private final JWTClient jwtClient;
    private final UserRepository userRepository;
    private final OfficialCodebloomEmail emailClient;
    private final ServerUrlUtils serverUrlUtils;
    private final UserTagRepository userTagRepository;
    private final Reporter reporter;

    public AuthController(final SessionRepository sessionRepository, final Protector protector, final JWTClient jwtClient, final UserRepository userRepository,
                    final OfficialCodebloomEmail emailClient, final ServerUrlUtils serverUrlUtils, final UserTagRepository userTagRepository, final Reporter reporter) {
        this.sessionRepository = sessionRepository;
        this.protector = protector;
        this.userRepository = userRepository;
        this.jwtClient = jwtClient;
        this.emailClient = emailClient;
        this.serverUrlUtils = serverUrlUtils;
        this.userTagRepository = userTagRepository;
        this.reporter = reporter;
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
            @ApiResponse(responseCode = "200", description = "email send successfully"),
            @ApiResponse(responseCode = "500", description = "not implemented")
    })
    @PostMapping("/school/enroll")
    public ResponseEntity<ApiResponder<Empty>> enrollSchool(@Valid @RequestBody final EmailBody emailBody, final HttpServletRequest request) {
        AuthenticationObject authenticationObject = protector.validateSession(request);
        String email = emailBody.getEmail();
        String domain = email.substring(email.indexOf("@")).toLowerCase();
        Set<String> supportedDomains = Stream.of(SchoolEnum.values())
                        .map(school -> school.getEmailDomain())
                        .collect(Collectors.toSet());

        if (!supportedDomains.contains(domain)) {
            String supportedSchools = String.join(", ", supportedDomains);
            throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "The email is not part of our supported schools domains: " + supportedSchools);
        }

        PrivateUser user = authenticationObject.getUser();
        String userId = user.getId();

        MagicLink magicLink = new MagicLink(email, userId);
        try {
            String token = jwtClient.encode(magicLink, Duration.ofHours(1));
            String verificationLink = serverUrlUtils.getUrl()
                            + "/api/auth/school/verify?state=" + token;
            emailClient.sendMessage(
                            SendEmailOptions.builder()
                                            .recipientEmail(email)
                                            .subject("Hello from Codebloom!")
                                            .body(String.format("""
                                                            Please click on this link to verify your school email with Codebloom: %s.

                                                            Note: This link will expire in 1 hour. If it expires, you'll need to request a new one.
                                                            """, verificationLink))
                                            .build());
            return ResponseEntity.ok().body(ApiResponder.success("Magic link sent! Check your school inbox to continue.", Empty.of()));
        } catch (EmailException e) {
            throw new ResponseStatusException(
                            HttpStatus.INTERNAL_SERVER_ERROR,
                            "Failed to send email.");
        } catch (Exception e) {
            throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Error processing request: not implemented");
        }
    }

    @Operation(summary = "Verifies the JWT", description = "Verifies the magic link sent to the user's email. If successful, the user will be enrolled with the school tag.", responses = {
            @ApiResponse(responseCode = "302", description = "Redirect to /settings with success or error message"),
    })
    @GetMapping("/school/verify")
    public RedirectView verifySchoolEmail(final HttpServletRequest request) {
        AuthenticationObject authenticationObject;
        Session session;
        try {
            authenticationObject = protector.validateSession(request);
            session = authenticationObject.getSession();
        } catch (Exception e) {
            return new RedirectView("/login?success=false&message=You are not logged in.");
        }
        if (session == null) {
            return new RedirectView("/settings?success=false&message=You are not logged in");
        }

        String token = request.getParameter("state");
        MagicLink magicLink;
        try {
            magicLink = jwtClient.decode(token, MagicLink.class);
        } catch (Exception e) {
            return new RedirectView("/settings?success=false&message=Invalid or expired token");
        }

        String magicLinkId = magicLink.getUserId();
        String currentUserId = authenticationObject.getUser().getId();
        if (!magicLinkId.equals(currentUserId)) {
            return new RedirectView("/settings?success=false&message=ID does not match current user");
        }

        PrivateUser user = userRepository.getPrivateUserById(magicLinkId);
        user.setSchoolEmail(magicLink.getEmail());
        userRepository.updateUser(user);

        String emailDomain = magicLink.getEmail().substring(magicLink.getEmail().indexOf("@")).toLowerCase();

        SchoolEnum schoolEnum = Stream.of(SchoolEnum.values())
                        .filter(school -> school.getEmailDomain().equals(emailDomain))
                        .findFirst()
                        .orElse(null);
        if (schoolEnum == null) {
            return new RedirectView("/settings?success=false&message=This email is not supported");
        }

        UserTag schoolTag = UserTag.builder()
                        .userId(user.getId())
                        .tag(schoolEnum.getInternalTag())
                        .build();

        if (user.getTags().stream()
                        .anyMatch(tag -> tag.getTag().name()
                                        .equals(schoolEnum.getInternalTag().name()))) {
            reporter.log(Report.builder()
                            .data(String.format("User %s\nAlready has tag %s",
                                            user.getNickname() != null ? user.getNickname() : user.getDiscordName(),
                                            schoolEnum.getInternalTag().name()))
                            .build());
        } else {
            userTagRepository.createTag(schoolTag);
        }

        return new RedirectView("/settings?success=true&message=The email has been verified!");
    }
}
