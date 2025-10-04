package com.patina.codebloom.api.announcement;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import com.patina.codebloom.common.db.models.announcement.Announcement;
import com.patina.codebloom.common.db.repos.announcement.AnnouncementRepository;
import com.patina.codebloom.common.dto.ApiResponder;
import com.patina.codebloom.common.security.Protector;
import com.patina.codebloom.common.dto.announcement.AnnouncementDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

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

    @Operation(summary = "Fetches the latest announcement, if available and/or not expired.", description = """
                    NOTE - 200 does not mean an announcement was found. The correct way to determine whether there is an announcement or not
                    is to use the success key of the payload and check whether it is true (announcement found) or false (no announcement currently)
                    """, responses = {
            @ApiResponse(responseCode = "200", description = "Successful response (may or may not be announcement, check success key)")
    })
    @GetMapping("")
    public ResponseEntity<ApiResponder<AnnouncementDto>> getLatestAnnouncement() {
        Announcement announcement = announcementRepository.getRecentAnnouncement();

        if (announcement == null) {
            return ResponseEntity.ok()
                            .body(ApiResponder.failure("No announcement available: check back later."));
        }
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        boolean isExpired = announcement.getExpiresAt().isBefore(now);

        if (isExpired) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                            .body(ApiResponder.failure("No announcement available: check back later."));
        }

        return ResponseEntity.ok(ApiResponder.success("Announcement found!", AnnouncementDto.fromAnnouncement(announcement)));
    }

}
