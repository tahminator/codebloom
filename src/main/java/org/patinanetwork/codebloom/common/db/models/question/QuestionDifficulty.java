package org.patinanetwork.codebloom.common.db.models.question;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "QuestionDifficulty")
public enum QuestionDifficulty {
    Easy,
    Medium,
    Hard,
}
