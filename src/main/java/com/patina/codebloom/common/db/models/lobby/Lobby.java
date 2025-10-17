package com.patina.codebloom.common.db.models.lobby;

import java.time.OffsetDateTime;

import com.patina.codebloom.common.db.helper.annotations.NotNullColumn;
import com.patina.codebloom.common.db.helper.annotations.NullColumn;

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
    @NotNullColumn
    private String id;

    @NotNullColumn
    private String joinCode;

    @NotNullColumn
    private LobbyStatus status;

    @NotNullColumn
    private OffsetDateTime createdAt;

    @NotNullColumn
    private OffsetDateTime expiresAt;

    @NotNullColumn
    private int playerCount;

    @NullColumn
    private String winnerId;
}