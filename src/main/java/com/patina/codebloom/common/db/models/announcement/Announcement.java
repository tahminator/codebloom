package com.patina.codebloom.common.db.models.announcement;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Builder
@Getter
@ToString
@EqualsAndHashCode
public class Announcement {
    // @Setter on some of the properties
    // so we can override id property when new object
    // created in database.
    private @Setter String id;
    private @Setter LocalDateTime createdAt;
    @NotNull
    private LocalDateTime expiresAt;
    private boolean showTimer;
    private String message;
}
