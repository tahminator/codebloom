package org.patinanetwork.codebloom.common.db.models.lobby.player;

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
public class LobbyPlayer {

    private String id;

    private String lobbyId;

    private String playerId;

    private int points;
}
