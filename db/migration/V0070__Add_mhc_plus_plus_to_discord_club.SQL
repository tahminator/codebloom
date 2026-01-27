INSERT INTO "DiscordClub"
    (id, name, description, tag)
VALUES
    (gen_random_uuid(), 'MHC++', NULL, 'MHCPlusPlus');


DO $$
BEGIN
    CASE current_database()
        WHEN 'codebloom-prod' THEN
            WITH CLUB AS (
                SELECT
                    *
                FROM
                    "DiscordClub"
                WHERE
                    name = 'MHC++'
            )
            INSERT INTO "DiscordClubMetadata"
                (id, "guildId", "leaderboardChannelId", "discordClubId")
            SELECT
                gen_random_uuid(),
                '1048355383728672829',
                '1074033436404228206',
                CLUB.id
            FROM CLUB;

        ELSE
            WITH CLUB AS (
                SELECT
                    *
                FROM
                    "DiscordClub"
                WHERE
                    name = 'MHC++'
            )
            INSERT INTO "DiscordClubMetadata"
                (id, "guildId", "leaderboardChannelId", "discordClubId")
            SELECT
                gen_random_uuid(),
                '1389762654452580373',
                '1463703700697518113',
                CLUB.id
            FROM CLUB;
    END CASE;
END $$;
