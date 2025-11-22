package com.patina.codebloom.common.db.models.lobby.player;

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
public class LobbyPlayerQuestion {
    private String id;

    private String lobbyPlayerId;

    @Builder.Default
    private Optional<String> questionId = Optional.empty();

    @Builder.Default
    private Optional<Integer> points = Optional.empty();
}
