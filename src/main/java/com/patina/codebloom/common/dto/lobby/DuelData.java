package com.patina.codebloom.common.dto.lobby;

import java.util.List;

import com.patina.codebloom.common.db.models.lobby.player.LobbyPlayerQuestion;

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
public class DuelData {

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private LobbyDto lobby;

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<LobbyPlayerQuestion> questions;

    public static final DuelData DEFAULT = builder().build();
}
