package com.patina.codebloom.api.announcement.body;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class CreateAnnouncementBody {
    @NotBlank
    @Size(min = 1, max = 230)
    private String message;

    @NotNull
    private LocalDateTime expiresAt;

    private boolean showTimer;
}
