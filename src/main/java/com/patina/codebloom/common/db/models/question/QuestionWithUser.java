package com.patina.codebloom.common.db.models.question;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuestionWithUser extends Question {
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String discordName;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String leetcodeUsername;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String nickname;

    public QuestionWithUser(final String id, final String userId, final String questionSlug, final QuestionDifficulty questionDifficulty, final int questionNumber, final String questionLink,
                    final Integer pointsAwarded, final String questionTitle, final String description, final float acceptanceRate, final LocalDateTime createdAt, final LocalDateTime submittedAt,
                    final String discordName, final String leetcodeUsername, final String runtime, final String memory, final String code, final String language, final String nickname,
                    final String submissionId) {
        super(id, userId, questionSlug, questionTitle, questionDifficulty, questionNumber, questionLink, description, pointsAwarded, acceptanceRate, createdAt, submittedAt, runtime, memory, code,
                        language, submissionId);
        this.discordName = discordName;
        this.leetcodeUsername = leetcodeUsername;
        this.nickname = nickname;
    }

    public String getDiscordName() {
        return discordName;
    }

    public void setDiscordName(final String discordName) {
        this.discordName = discordName;
    }

    public String getLeetcodeUsername() {
        return leetcodeUsername;
    }

    public void setLeetcodeUsername(final String leetcodeUsername) {
        this.leetcodeUsername = leetcodeUsername;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(final String nickname) {
        this.nickname = nickname;
    }

}
