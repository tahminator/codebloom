package com.patina.codebloom.common.db.repos.discord.club;

import com.patina.codebloom.common.db.models.discord.DiscordClub;
import java.util.List;
import java.util.Optional;

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
}
