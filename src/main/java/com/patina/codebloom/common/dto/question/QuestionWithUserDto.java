package com.patina.codebloom.common.dto.question;

import com.patina.codebloom.common.db.models.question.QuestionDifficulty;
import com.patina.codebloom.common.db.models.question.QuestionWithUser;

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
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String discordName;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String leetcodeUsername;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
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
                        .discordName(questionWithUser.getDiscordName())
                        .leetcodeUsername(questionWithUser.getLeetcodeUsername())
                        .nickname(questionWithUser.getNickname())
                        .build();
    }
}
