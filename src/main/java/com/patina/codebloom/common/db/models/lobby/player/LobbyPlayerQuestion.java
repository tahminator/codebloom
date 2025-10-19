package com.patina.codebloom.common.db.models.lobby.player;

import com.patina.codebloom.common.db.helper.annotations.NotNullColumn;

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
public class LobbyPlayerQuestion {
    @NotNullColumn
    private String id;

    @NotNullColumn
    private String lobbyPlayerId;

    @NotNullColumn
    private String questionId;

    @NotNullColumn
    private int points;
}