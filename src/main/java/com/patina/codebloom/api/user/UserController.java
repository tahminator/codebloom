package com.patina.codebloom.api.user;

import java.util.ArrayList;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.patina.codebloom.common.db.models.question.Question;
import com.patina.codebloom.common.db.models.user.User;
import com.patina.codebloom.common.db.repos.question.QuestionRepository;
import com.patina.codebloom.common.db.repos.user.UserRepository;
import com.patina.codebloom.common.dto.ApiResponder;
import com.patina.codebloom.common.dto.autogen.UnsafeGenericFailureResponse;
import com.patina.codebloom.common.lag.FakeLag;
import com.patina.codebloom.common.page.Page;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@Tag(name = "General user routes", description = "This controller is responsible for handling general user data, such as user profile, user submissions, and more.")
@RequestMapping("/api/user")
public class UserController {
    /* Page size for submissions */
    private static final int SUBMISSIONS_PAGE_SIZE = 20;

    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;

    public UserController(final QuestionRepository questionRepository, final UserRepository userRepository) {
        this.questionRepository = questionRepository;
        this.userRepository = userRepository;
    }

    @Operation(summary = "Public route that returns the given user's profile", description = """
                    Unprotected endpoint that returns the user profile of the user ID that is passed to the endpoint's path.
                    """, responses = {
            @ApiResponse(responseCode = "404", description = "User profile has not been found", content = @Content(schema = @Schema(implementation = UnsafeGenericFailureResponse.class))),
            @ApiResponse(responseCode = "200", description = "User profile has been found")
    })
    @GetMapping("{userId}/profile")
    public ResponseEntity<ApiResponder<User>> getUserProfileByUserId(final HttpServletRequest request, @PathVariable final String userId) {
        FakeLag.sleep(650);

        User user = userRepository.getUserById(userId);

        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Failed to find user profile.");
        }

        return ResponseEntity.ok().body(ApiResponder.success("User profile found!", user));
    }

    @Operation(summary = "Returns a list of the questions successfully submitted by the user.", description = """
                    Protected endpoint that returns the list of questions completed by the user.
                    These questions are guaranteed to be completed by the user.
                    """, responses = { @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content(schema = @Schema(implementation = UnsafeGenericFailureResponse.class))) })
    @GetMapping("{userId}/submissions")
    public ResponseEntity<ApiResponder<Page<Question>>> getAllQuestionsForUser(final HttpServletRequest request,
                    @Parameter(description = "Page index", example = "1") @RequestParam(required = false, defaultValue = "1") final int page,
                    @Parameter(description = "Question Title", example = "Two") @RequestParam(required = false, defaultValue = "") final String query,
                    @Parameter(description = "Page size (maximum of " + SUBMISSIONS_PAGE_SIZE) @RequestParam(required = false, defaultValue = "" + SUBMISSIONS_PAGE_SIZE) final int pageSize,
                    @Parameter(description = "Filter to hide questions with 0 points awarded") @RequestParam(required = false, defaultValue = "false") final boolean pointFilter,
                    @PathVariable final String userId) {
        FakeLag.sleep(500);

        final int parsedPageSize = Math.min(pageSize, SUBMISSIONS_PAGE_SIZE);

        ArrayList<Question> questions = questionRepository.getQuestionsByUserId(userId, page, parsedPageSize, query, pointFilter);

        int totalQuestions = questionRepository.getQuestionCountByUserId(userId, query, pointFilter);
        int totalPages = (int) Math.ceil((double) totalQuestions / SUBMISSIONS_PAGE_SIZE);
        boolean hasNextPage = page < totalPages;

        Page<Question> createdPage = new Page<>(hasNextPage, questions, totalPages, parsedPageSize);

        return ResponseEntity.ok().body(ApiResponder.success("All questions have been fetched!", createdPage));
    }

    @Operation(summary = "Public route that returns a list of all the users' metadata.", description = """
                        Unprotected endpoint that returns basic metadata for all users.
                    """, responses = {
            @ApiResponse(responseCode = "404", description = "All users' metadata has not been found.", content = @Content(schema = @Schema(implementation = UnsafeGenericFailureResponse.class))),
            @ApiResponse(responseCode = "200", description = "All users' metadata has been found.")
    })
    @GetMapping("/all")
    public ResponseEntity<ApiResponder<Page<User>>> getAllUsers(final HttpServletRequest request,
                    @Parameter(description = "Page index", example = "1") @RequestParam(required = false, defaultValue = "1") final int page,
                    @Parameter(description = "Question Title", example = "Two") @RequestParam(required = false, defaultValue = "") final String query,
                    @Parameter(description = "Page size (maximum of " + SUBMISSIONS_PAGE_SIZE) @RequestParam(required = false, defaultValue = "" + SUBMISSIONS_PAGE_SIZE) final int pageSize) {
        FakeLag.sleep(650);

        final int parsedPageSize = Math.min(pageSize, SUBMISSIONS_PAGE_SIZE);

        ArrayList<User> users = userRepository.getAllUsers(page, parsedPageSize, query);

        int totalUsers = userRepository.getUserCount(query);
        int totalPages = (int) Math.ceil((double) totalUsers / parsedPageSize);
        boolean hasNextPage = page < totalPages;

        Page<User> createdPage = new Page<>(hasNextPage, users, totalPages, parsedPageSize);

        return ResponseEntity.ok().body(ApiResponder.success("All users have been successfully fetched!", createdPage));
    }
}
