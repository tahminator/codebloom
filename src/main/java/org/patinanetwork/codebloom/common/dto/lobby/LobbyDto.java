package org.patinanetwork.codebloom.common.dto.lobby;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;
import org.patinanetwork.codebloom.common.db.models.lobby.Lobby;
import org.patinanetwork.codebloom.common.db.models.lobby.LobbyStatus;

@Getter
@Builder
@Jacksonized
@ToString
@EqualsAndHashCode
public class LobbyDto {

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String id;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String joinCode;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private LobbyStatus status;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, nullable = true)
    private OffsetDateTime expiresAt;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private OffsetDateTime createdAt;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private int playerCount;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, nullable = true)
    private String winnerId;

    public static LobbyDto fromLobby(final Lobby lobby) {
        return LobbyDto.builder()
                .id(lobby.getId())
                .joinCode(lobby.getJoinCode())
                .status(lobby.getStatus())
                .expiresAt(lobby.getExpiresAt().orElse(null))
                .createdAt(lobby.getCreatedAt())
                .playerCount(lobby.getPlayerCount())
                .winnerId(lobby.getWinnerId().orElse(null))
                .build();
    }
}
