package com.patina.codebloom.common.db.models.announcement;

import java.time.OffsetDateTime;

import com.patina.codebloom.common.db.helper.annotations.NotNullColumn;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@ToString
@EqualsAndHashCode
public class Announcement {
    @NotNullColumn
    private String id;

    @NotNullColumn
    private OffsetDateTime createdAt;

    private OffsetDateTime expiresAt;

    @NotNullColumn
    private boolean showTimer;

    @NotNullColumn
    private String message;
}