package com.patina.codebloom.common.dto.lobby;

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

    public static final DuelData DEFAULT = builder().build();
}
