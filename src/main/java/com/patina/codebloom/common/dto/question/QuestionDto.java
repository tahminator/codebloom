package com.patina.codebloom.common.dto.question;

import com.patina.codebloom.common.db.models.question.Question;
import com.patina.codebloom.common.db.models.question.QuestionDifficulty;

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
                        .build();
    }
}