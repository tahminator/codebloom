package com.patina.codebloom.common.db.repos.discord.discordClub;

import com.patina.codebloom.common.db.models.discord.DiscordClub;

public interface DiscordClubRepository {
    /**
     * Creates a new DiscordClub in the database.
     * 
     * @param discordClub - required fields:
     * <ul>
     * <li>name</li>
     * <li>tag</li>
     * </ul>
     */
    DiscordClub createDiscordClub(DiscordClub discordClub);

    DiscordClub getDiscordClubById(String id);

    DiscordClub updateDiscordClub(DiscordClub discordClub);

    boolean deleteDiscordClubById(String id);
}