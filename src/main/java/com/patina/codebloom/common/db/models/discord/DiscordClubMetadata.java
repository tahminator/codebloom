package com.patina.codebloom.common.db.models.discord;

import java.util.Optional;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

@Setter
@Getter
@Builder
@ToString
@EqualsAndHashCode
@Jacksonized
public class DiscordClubMetadata {

    private String id;

    @Builder.Default
    private Optional<String> guildId = Optional.empty();

    @Builder.Default
    private Optional<String> leaderboardChannelId = Optional.empty();

    private String discordClubId;
}
