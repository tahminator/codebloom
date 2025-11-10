package com.patina.codebloom.common.db.repos.discord.discordClub;

import com.patina.codebloom.common.db.models.discord.DiscordClub;

public interface DiscordClubRepository {
    DiscordClub createDiscordClub(DiscordClub discordClub);

    DiscordClub getDiscordClubById(String id);

    void updateDiscordClub(DiscordClub discordClub);

    void deleteDiscordClub(DiscordClub discordClub);
}