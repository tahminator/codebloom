package com.patina.codebloom.api.announcement;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import com.patina.codebloom.api.announcement.body.CreateAnnouncementBody;
import com.patina.codebloom.common.db.models.announcement.Announcement;
import com.patina.codebloom.common.db.repos.announcement.AnnouncementRepository;
import com.patina.codebloom.common.dto.ApiResponder;
import com.patina.codebloom.common.dto.autogen.UnsafeGenericFailureResponse;
import com.patina.codebloom.common.security.Protector;
import com.patina.codebloom.common.time.StandardizedLocalDateTime;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@Tag(name = "Announcement routes", description = """
                    These routes house the logic for anyone to get the current announcement,
                    as well as admins being able to set a new announcement.
                """)
@RequestMapping("/api/announcement")
public class AnnouncementController {
    private final Protector protector;
    private final AnnouncementRepository announcementRepository;

    public AnnouncementController(final Protector protector, final AnnouncementRepository announcementRepository) {
        this.protector = protector;
        this.announcementRepository = announcementRepository;
    }

    @Operation(summary = "Fetches the latest announcement, if available and/or not expired.", responses = {
            @ApiResponse(responseCode = "204", description = "No announcement found", content = @Content(schema = @Schema(implementation = UnsafeGenericFailureResponse.class))),
            @ApiResponse(responseCode = "200", description = "Announcement found")
    })
    @GetMapping("")
    public ResponseEntity<ApiResponder<Announcement>> getLatestAnnouncement() {
        Announcement announcement = announcementRepository.getRecentAnnouncement();

        if (announcement == null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                            .body(ApiResponder.failure("No announcement available: check back later."));
        }

        boolean isExpired = announcement.getExpiresAt().isBefore(StandardizedLocalDateTime.now());

        if (isExpired) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                            .body(ApiResponder.failure("No announcement available: check back later."));
        }

        return ResponseEntity.ok(ApiResponder.success("Announcement found!", announcement));
    }

    @Operation(summary = "Create a new announcement (only for admins).", responses = {
            @ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content(schema = @Schema(implementation = UnsafeGenericFailureResponse.class))),
            @ApiResponse(responseCode = "200", description = "Announcement successfully created"),
            @ApiResponse(responseCode = "500", description = "Something went wrong", content = @Content(schema = @Schema(implementation = UnsafeGenericFailureResponse.class)))
    })
    @PostMapping("")
    public ResponseEntity<ApiResponder<Announcement>> createNewAnnouncement(
                    @Valid @RequestBody final CreateAnnouncementBody createAnnouncementBody,
                    final HttpServletRequest request) {
        protector.validateAdminSession(request);

        Announcement announcement = Announcement.builder()
                        .expiresAt(createAnnouncementBody.getExpiresAt())
                        .showTimer(createAnnouncementBody.isShowTimer())
                        .message(createAnnouncementBody.getMessage())
                        .build();

        boolean isSuccessful = announcementRepository.createAnnouncement(announcement);

        if (!isSuccessful) {
            return ResponseEntity
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(ApiResponder.failure("Hmm, something went wrong."));
        }

        return ResponseEntity.ok(ApiResponder.success("New announcement successfully created!", announcement));
    }
}
