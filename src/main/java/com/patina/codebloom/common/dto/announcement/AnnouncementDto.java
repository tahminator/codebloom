package com.patina.codebloom.common.dto.announcement;

import java.time.OffsetDateTime;

import com.patina.codebloom.common.db.models.announcement.Announcement;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

@Getter
@Builder
@Jacksonized
@ToString
@EqualsAndHashCode
public class AnnouncementDto {
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String id;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private OffsetDateTime createdAt;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private OffsetDateTime expiresAt;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, nullable = true)
    private boolean showTimer;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String message;

    public static AnnouncementDto fromAnnouncement(final Announcement announcement) {
        return AnnouncementDto.builder()
                        .id(announcement.getId())
                        .createdAt(announcement.getCreatedAt())
                        .expiresAt(announcement.getExpiresAt())
                        .showTimer(announcement.isShowTimer())
                        .message(announcement.getMessage())
                        .build();
    }
}
