package com.patina.codebloom.common.db.repos.discord.club.metadata;

import com.patina.codebloom.common.db.models.discord.DiscordClubMetadata;

public interface DiscordClubMetadataRepository {
    /**
     * Creates a new DiscordClubMetadata in the database.
     * 
     * @note - This should be joined with an existing DiscordClub in the database.
     * 
     * @param discordClubMetadata - required fields:
     * <ul>
     * <li>discordClubId</li>
     * </ul>
     * optional fields:
     * <ul>
     * <li>guildId</li>
     * <li>leaderboardChannelId</li>
     * </ul>
     */
    void createDiscordClubMetadata(DiscordClubMetadata discordClubMetadata);

    DiscordClubMetadata getDiscordClubMetadataById(String id);

    /**
     * @param discordClubMetadata - overridable fields:
     * <ul>
     * <li>guildId</li>
     * <li>leaderboardChannelId</li>
     * <li>discordClubId</li>
     * </ul>
     */
    DiscordClubMetadata updateDiscordClubMetadata(DiscordClubMetadata discordClubMetadata);

    boolean deleteDiscordClubMetadataById(String id);

    DiscordClubMetadata getMetadataByClubId(String id);
}
