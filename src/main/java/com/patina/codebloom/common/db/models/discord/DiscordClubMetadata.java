package com.patina.codebloom.common.db.models.discord;

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
public class DiscordClubMetadata {
    @NotNullColumn
    private String id;

    @NullColumn
    private String guildId;

    @NullColumn
    private String leaderboardChannelId;

    @NotNullColumn
    private String discordClubId;

}
