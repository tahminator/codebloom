package com.patina.codebloom.common.dto.question;

import java.time.LocalDateTime;
import java.util.List;

import com.patina.codebloom.common.db.models.question.QuestionDifficulty;
import com.patina.codebloom.common.db.models.question.QuestionWithUser;
import com.patina.codebloom.common.dto.question.topic.QuestionTopicDto;

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
public class QuestionWithUserDto {
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
    private List<QuestionTopicDto> topics;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, nullable = true)
    private String discordName;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, nullable = true)
    private String leetcodeUsername;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, nullable = true)
    private String nickname;

    public static QuestionWithUserDto fromQuestionWithUser(final QuestionWithUser questionWithUser) {
        return QuestionWithUserDto.builder()
                        .id(questionWithUser.getId())
                        .userId(questionWithUser.getUserId())
                        .questionSlug(questionWithUser.getQuestionSlug())
                        .questionTitle(questionWithUser.getQuestionTitle())
                        .questionDifficulty(questionWithUser.getQuestionDifficulty())
                        .questionNumber(questionWithUser.getQuestionNumber())
                        .questionLink(questionWithUser.getQuestionLink())
                        .description(questionWithUser.getDescription())
                        .pointsAwarded(questionWithUser.getPointsAwarded())
                        .acceptanceRate(questionWithUser.getAcceptanceRate())
                        .createdAt(questionWithUser.getCreatedAt())
                        .submittedAt(questionWithUser.getSubmittedAt())
                        .runtime(questionWithUser.getRuntime())
                        .memory(questionWithUser.getMemory())
                        .code(questionWithUser.getCode())
                        .language(questionWithUser.getLanguage())
                        .submissionId(questionWithUser.getSubmissionId())
                        .topics(questionWithUser.getTopics().stream().map(q -> QuestionTopicDto.fromQuestionTopic(q)).toList())
                        .discordName(questionWithUser.getDiscordName())
                        .leetcodeUsername(questionWithUser.getLeetcodeUsername())
                        .nickname(questionWithUser.getNickname())
                        .build();
    }
}
