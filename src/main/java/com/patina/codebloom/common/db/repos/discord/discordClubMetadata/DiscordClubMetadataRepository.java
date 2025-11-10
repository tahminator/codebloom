package com.patina.codebloom.common.db.repos.discord.discordClubMetadata;

import com.patina.codebloom.common.db.models.discord.DiscordClubMetadata;

public interface DiscordClubMetadataRepository {
    DiscordClubMetadata createDiscordClubMetadata(DiscordClubMetadata discordClubMetadata);

    DiscordClubMetadata getDiscordClubMetadataById(String id);

    void updateDiscordClubMetadata(DiscordClubMetadata discordClubMetadata);

    void deleteDiscordClubMetadata(DiscordClubMetadata discordClubMetadata);
}
