package com.patina.codebloom.common.db.models.question;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Setter
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode
@ToString
public class Question {
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String id;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String userId;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String questionSlug;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String questionTitle;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private QuestionDifficulty questionDifficulty;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private int questionNumber;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String questionLink;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, nullable = true)
    private String description;

    /**
     * Optional for the case of future proofing. We might end up using AI to award
     * some points, so there might be a case where we create the DB entry and then
     * pass it to a message queue to use AI and calculate a score.
     */

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, nullable = true)
    private Integer pointsAwarded;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private float acceptanceRate;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createdAt;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime submittedAt;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, nullable = true)
    private String runtime;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, nullable = true)
    private String memory;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, nullable = true)
    private String code;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, nullable = true)
    private String language;

    // Not every submission will have this.

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, nullable = true)
    private String submissionId;

}