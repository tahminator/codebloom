package com.patina.codebloom.common.db.models.lobby;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "LobbyStatus")
public enum LobbyStatus {
    CLOSED,
    AVAILABLE,
    ACTIVE,
    COMPLETED,
}
