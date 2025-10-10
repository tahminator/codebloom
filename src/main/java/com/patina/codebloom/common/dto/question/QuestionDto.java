package com.patina.codebloom.common.dto.question;

import java.time.LocalDateTime;
import java.util.List;

import com.patina.codebloom.common.db.models.question.Question;
import com.patina.codebloom.common.db.models.question.QuestionDifficulty;
import com.patina.codebloom.common.db.models.question.topic.QuestionTopic;

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
public class QuestionDto {
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
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, nullable = true)
    private String submissionId;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private List<QuestionTopic> topics;

    public static QuestionDto fromQuestion(final Question question) {
        return QuestionDto.builder()
                        .id(question.getId())
                        .userId(question.getUserId())
                        .questionSlug(question.getQuestionSlug())
                        .questionTitle(question.getQuestionTitle())
                        .questionDifficulty(question.getQuestionDifficulty())
                        .questionNumber(question.getQuestionNumber())
                        .questionLink(question.getQuestionLink())
                        .description(question.getDescription())
                        .pointsAwarded(question.getPointsAwarded())
                        .acceptanceRate(question.getAcceptanceRate())
                        .createdAt(question.getCreatedAt())
                        .submittedAt(question.getSubmittedAt())
                        .runtime(question.getRuntime())
                        .memory(question.getMemory())
                        .code(question.getCode())
                        .language(question.getLanguage())
                        .submissionId(question.getSubmissionId())
                        .topics(question.getTopics())
                        .build();
    }
}
