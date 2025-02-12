package com.patina.codebloom.common.db.models.question;

import java.time.LocalDateTime;
import java.util.OptionalInt;

public class QuestionWithUser extends Question {
    private String discordName;
    private String leetcodeUsername;

    public QuestionWithUser(final String id, final String userId, final String questionSlug, final QuestionDifficulty questionDifficulty,
            final int questionNumber, final String questionLink, final OptionalInt pointsAwarded, final String questionTitle,
            final String description, final float acceptanceRate, final LocalDateTime createdAt, final LocalDateTime submittedAt,
            final String discordName, final String leetcodeUsername, final String runtime, final String memory, final String code,
            final String language) {
        super(id, userId, questionSlug, questionDifficulty, questionNumber, questionLink, pointsAwarded, questionTitle, description, acceptanceRate,
                createdAt, submittedAt, runtime, memory, code, language);
        this.discordName = discordName;
        this.leetcodeUsername = leetcodeUsername;
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

}
