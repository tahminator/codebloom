package com.patina.codebloom.common.db.models.leaderboard;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

@Getter
@Setter
@Jacksonized
@SuperBuilder
@ToString
@EqualsAndHashCode
public class Leaderboard {
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String id;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createdAt;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, nullable = true)
    private LocalDateTime deletedAt;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, nullable = true)
    private LocalDateTime shouldExpireBy;

    // public Leaderboard(final String name, final LocalDateTime createdAt, final
    // LocalDateTime shouldExpireBy) {
    // this.name = name;
    // this.createdAt = createdAt;
    // this.shouldExpireBy = shouldExpireBy;
    // }
    //
    // public Leaderboard(final String id, final String name, final LocalDateTime
    // createdAt, final LocalDateTime deletedAt, final LocalDateTime shouldExpireBy)
    // {
    // this.id = id;
    // this.name = name;
    // this.createdAt = createdAt;
    // this.deletedAt = deletedAt;
    // this.shouldExpireBy = shouldExpireBy;
    // }
}
