package com.patina.codebloom.common.db.models.question;

import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Setter
@Jacksonized
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@ToString
public class QuestionWithUser extends Question {
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String discordName;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String leetcodeUsername;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String nickname;
}