CREATE TABLE IF NOT EXISTS "DiscordClubMetadata" (
    id UUID NOT NULL PRIMARY KEY,
    "guildId" TEXT NULL, -- null indicates not configured correctly (we won't even fetch this club)
    "leaderboardChannelId" TEXT NULL, -- null indicates not configured correctly (we skip sending leaderboard for this club)
    "discordClubId" UUID NOT NULL,
    CONSTRAINT "fk_discordClub" FOREIGN KEY ("discordClubId") REFERENCES "DiscordClub"(id) ON DELETE CASCADE ON UPDATE CASCADE
)