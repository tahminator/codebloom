package com.patina.codebloom.api.user.v2;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.patina.codebloom.common.db.models.user.User;
import com.patina.codebloom.common.db.repos.question.QuestionRepository;
import com.patina.codebloom.common.db.repos.user.v2.UserV2Repository;
import com.patina.codebloom.common.dto.ApiResponder;
import com.patina.codebloom.common.dto.autogen.UnsafeGenericFailureResponse;
import com.patina.codebloom.common.lag.FakeLag;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@Tag(name = "General user routes", description = "This controller is responsible for handling general user data, such as user profile, user submissions, and more.")
@RequestMapping("/api/user/v2")
public class UserV2Controller {
    private final QuestionRepository questionRepository;
    private final UserV2Repository userV2Repository;

    public UserV2Controller(final QuestionRepository questionRepository, final UserV2Repository userV2Repository) {
        this.questionRepository = questionRepository;
        this.userV2Repository = userV2Repository;
    }

    @Operation(summary = "Public route that returns the given user's profile", description = """
                        Unprotected endpoint that returns the user profile of the LeetCode username that is passed to the endpoint's path.
                    """, responses = {
            @ApiResponse(responseCode = "404", description = "User profile has not been found", content = @Content(schema = @Schema(implementation = UnsafeGenericFailureResponse.class))),
            @ApiResponse(responseCode = "200", description = "User profile has been found")
    })
    @GetMapping("{leetcodeUsername}/profile")
    public ResponseEntity<ApiResponder<User>> getUserProfileByLeetcodeUsername(final HttpServletRequest request, @PathVariable final String leetcodeUsername) {
        FakeLag.sleep(650);

        User user = userV2Repository.getUserByLeetcodeUsername(leetcodeUsername);

        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Failed to find user profile.");
        }

        return ResponseEntity.ok().body(ApiResponder.success("User profile found!", user));
    }
}
