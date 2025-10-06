package com.patina.codebloom.common.dto.potd;

import java.time.LocalDateTime;

import com.patina.codebloom.common.db.models.potd.POTD;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

@Getter
@Builder
@Jacksonized
@EqualsAndHashCode
@ToString
public class PotdDto {
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String id;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String slug;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private float multiplier;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createdAt;

    public static PotdDto fromPOTD(final POTD potd) {
        return PotdDto.builder()
                        .id(potd.getId())
                        .title(potd.getTitle())
                        .slug(potd.getSlug())
                        .multiplier(potd.getMultiplier())
                        .createdAt(potd.getCreatedAt())
                        .build();
    }
}
