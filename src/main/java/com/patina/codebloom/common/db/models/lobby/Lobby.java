package com.patina.codebloom.common.db.models.lobby;

import java.time.OffsetDateTime;
import java.util.Optional;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@Builder
@ToString
@EqualsAndHashCode
public class Lobby {

    private String id;

    private String joinCode;

    private LobbyStatus status;

    private OffsetDateTime createdAt;

    @Builder.Default
    private Optional<OffsetDateTime> expiresAt = Optional.empty();

    private int playerCount;

    @Builder.Default
    private Optional<String> winnerId = Optional.empty();

    @Builder.Default
    private boolean tie = false;
}
