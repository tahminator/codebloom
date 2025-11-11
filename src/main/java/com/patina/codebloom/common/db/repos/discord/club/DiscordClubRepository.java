package com.patina.codebloom.common.db.repos.discord.club;

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
     * optional fields:
     * <ul>
     * <li>description</li>
     * </ul>
     */
    void createDiscordClub(DiscordClub discordClub);

    DiscordClub getDiscordClubById(String id);

    /**
     * @param discordClub - overridable fields:
     * <ul>
     * <li>name</li>
     * <li>description</li>
     * <li>tag</li>
     * </ul>
     */
    DiscordClub updateDiscordClub(DiscordClub discordClub);

    boolean deleteDiscordClubById(String id);

}