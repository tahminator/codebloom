package com.patina.codebloom.common.db.models.question;
import java.util.UUID;
import lombok.*;
import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Setter
@AllArgsConstructor
@Builder
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

    /*
     * // Constructors commented out to be refactored: public Question(final String
     * userId, final String questionSlug, final QuestionDifficulty
     * questionDifficulty, final int questionNumber, final String questionLink,
     * final String questionTitle, final String description, final Integer
     * pointsAwarded, final float acceptanceRate, final LocalDateTime submittedAt) {
     * // ... }
     * 
     * public Question(final String userId, final String questionSlug, final
     * QuestionDifficulty questionDifficulty, final int questionNumber, final String
     * questionLink, final String questionTitle, final String description, final
     * Integer pointsAwarded, final float acceptanceRate, final LocalDateTime
     * submittedAt, final String runtime, final String memory, final String code,
     * final String language, final String submissionId) { // ... }
     * 
     * public Question(final String id, final String userId, final String
     * questionSlug, final QuestionDifficulty questionDifficulty, final int
     * questionNumber, final String questionLink, final Integer pointsAwarded, final
     * String questionTitle, final String description, final float acceptanceRate,
     * final LocalDateTime createdAt, final LocalDateTime submittedAt, final String
     * runtime, final String memory, final String code, final String language, final
     * String submissionId) { // ... }
     * 
     * public Question(final String id, final String userId, final String
     * questionSlug, final QuestionDifficulty questionDifficulty, final int
     * questionNumber, final String questionLink, final Integer pointsAwarded, final
     * String questionTitle, final String description, final float acceptanceRate,
     * final LocalDateTime createdAt, final LocalDateTime submittedAt, final String
     * submissionId) { // ... }
     */
}