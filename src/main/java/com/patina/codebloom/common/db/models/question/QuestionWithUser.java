package com.patina.codebloom.common.db.models.question;

import java.time.LocalDateTime;
import java.util.OptionalInt;

public class QuestionWithUser extends Question {
    private String discordName;
    private String leetcodeUsername;

    public QuestionWithUser(String id, String userId, String questionSlug, QuestionDifficulty questionDifficulty,
            int questionNumber,
            String questionLink, OptionalInt pointsAwarded, String questionTitle, String description,
            float acceptanceRate, LocalDateTime createdAt, LocalDateTime submittedAt, String discordName,
            String leetcodeUsername, String runtime, String memory, String code,
            String language) {
        super(id, userId, questionSlug, questionDifficulty, questionNumber, questionLink, pointsAwarded, questionTitle,
                description, acceptanceRate, createdAt, submittedAt, runtime, memory, code, language);
        this.discordName = discordName;
        this.leetcodeUsername = leetcodeUsername;
    }

    public String getDiscordName() {
        return discordName;
    }

    public void setDiscordName(String discordName) {
        this.discordName = discordName;
    }

    public String getLeetcodeUsername() {
        return leetcodeUsername;
    }

    public void setLeetcodeUsername(String leetcodeUsername) {
        this.leetcodeUsername = leetcodeUsername;
    }

}
