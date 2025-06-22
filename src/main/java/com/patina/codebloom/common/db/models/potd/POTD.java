package com.patina.codebloom.common.db.models.potd;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class POTD {
    private String id;
    private String title;
    private String slug;
    private float multiplier;
    private LocalDateTime createdAt;

    public POTD(final String title, final String slug, final float multiplier, final LocalDateTime createdAt) {
        this.title = title;
        this.slug = slug;
        this.multiplier = multiplier;
        this.createdAt = createdAt;
    }

}
