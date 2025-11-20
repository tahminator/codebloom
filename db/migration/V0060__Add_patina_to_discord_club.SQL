INSERT INTO "DiscordClub"
    (id, name, description, tag)
VALUES
    (gen_random_uuid(), 'Patina Network', NULL, 'Patina');


DO $$
BEGIN
    CASE current_database()
        WHEN 'codebloom-prod' THEN
            WITH PATINA_CLUB AS (
                SELECT
                    *
                FROM
                    "DiscordClub"
                WHERE
                    name = 'Patina Network'
            )
            INSERT INTO "DiscordClubMetadata"
                (id, "guildId", "leaderboardChannelId", "discordClubId")
            SELECT
                gen_random_uuid(),
                '1246865559019065505',
                '1320473999989538956',
                PATINA_CLUB.id
            FROM PATINA_CLUB;

        ELSE
            WITH PATINA_CLUB AS (
                SELECT
                    *
                FROM
                    "DiscordClub"
                WHERE
                    name = 'Patina Network'
            )
            INSERT INTO "DiscordClubMetadata"
                (id, "guildId", "leaderboardChannelId", "discordClubId")
            SELECT
                gen_random_uuid(),
                '1389762654452580373',
                '1401739528057655436',
                PATINA_CLUB.id
            FROM PATINA_CLUB;
    END CASE;
END $$;
