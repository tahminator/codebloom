package org.patinanetwork.codebloom.common.db.models.lobby;

import java.time.OffsetDateTime;
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
public class LobbyQuestion {

    private String id;

    private String lobbyId;

    private String questionBankId;

    private OffsetDateTime createdAt;

    private int userSolvedCount;
}
