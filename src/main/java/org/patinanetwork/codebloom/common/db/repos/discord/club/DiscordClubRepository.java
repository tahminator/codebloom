package org.patinanetwork.codebloom.common.db.repos.discord.club;

import java.util.List;
import java.util.Optional;
import org.patinanetwork.codebloom.common.db.models.discord.DiscordClub;


public interface DiscordClubRepository {
    /**
     * Creates a new DiscordClub in the database.
     *
     * @param discordClub - required fields:
     *     <ul>
     *       <li>name
     *       <li>tag
     *     </ul>
     *     optional fields:
     *     <ul>
     *       <li>description
     *     </ul>
     */
    void createDiscordClub(DiscordClub discordClub);

    Optional<DiscordClub> getDiscordClubById(String id);

    List<DiscordClub> getAllActiveDiscordClubs();

    /**
     * @param discordClub - overridable fields:
     *     <ul>
     *       <li>name
     *       <li>description
     *       <li>tag
     *       <li>deletedAt
     *     </ul>
     */
    boolean updateDiscordClubById(DiscordClub discordClub);

    boolean deleteDiscordClubById(String id);

    Optional<DiscordClub> getDiscordClubByGuildId(String guildId);
}
