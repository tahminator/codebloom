package org.patinanetwork.codebloom.common.db.repos.discord.club.metadata;

import java.util.Optional;
import org.patinanetwork.codebloom.common.db.models.discord.DiscordClubMetadata;

public interface DiscordClubMetadataRepository {
    /**
     * Creates a new DiscordClubMetadata in the database.
     *
     * @note - This should be joined with an existing DiscordClub in the database.
     * @param discordClubMetadata - required fields:
     *     <ul>
     *       <li>discordClubId
     *     </ul>
     *     optional fields:
     *     <ul>
     *       <li>guildId
     *       <li>leaderboardChannelId
     *     </ul>
     */
    void createDiscordClubMetadata(DiscordClubMetadata discordClubMetadata);

    Optional<DiscordClubMetadata> getDiscordClubMetadataById(String id);

    Optional<DiscordClubMetadata> getDiscordClubMetadataByClubId(String clubId);

    /**
     * @param discordClubMetadata - overridable fields:
     *     <ul>
     *       <li>guildId
     *       <li>leaderboardChannelId
     *       <li>discordClubId
     *     </ul>
     */
    boolean updateDiscordClubMetadata(DiscordClubMetadata discordClubMetadata);

    boolean deleteDiscordClubMetadataById(String id);
}
