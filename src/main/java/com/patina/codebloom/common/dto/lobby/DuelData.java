package com.patina.codebloom.common.dto.lobby;

import com.patina.codebloom.common.dto.question.QuestionBankDto;
import com.patina.codebloom.common.dto.question.QuestionDto;
import com.patina.codebloom.common.dto.user.UserDto;
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

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<QuestionBankDto> questions;

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<UserDto> players;

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Map<String, List<QuestionDto>> playerQuestions;

    public static final DuelData DEFAULT = builder().build();
}
