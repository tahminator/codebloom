package com.patina.codebloom.common.dto.lobby;

import com.patina.codebloom.common.db.models.question.Question;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Map;
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
public class DuelData {

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private LobbyDto lobby;

    @Schema(
        requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        nullable = true,
        description = "Unique questions being worked on in this duel"
    )
    private List<Question> questions;

    @Schema(
        requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        nullable = true,
        description = "Map of player IDs to the questions they have solved"
    )
    private Map<String, List<Question>> playerQuestions;

    public static final DuelData DEFAULT = builder().build();
}
