INSERT INTO "DiscordClub"
    (id, name, description, tag)
VALUES
    (gen_random_uuid(), 'GWC-Hunter', NULL, 'Gwc');


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
                    name = 'GWC-Hunter'
            )
            INSERT INTO "DiscordClubMetadata"
                (id, "guildId", "leaderboardChannelId", "discordClubId")
            SELECT
                gen_random_uuid(),
                '1066177903345278986',
                '1468417251274129452',
                CLUB.id
            FROM CLUB;

        ELSE
            WITH CLUB AS (
                SELECT
                    *
                FROM
                    "DiscordClub"
                WHERE
                    name = 'GWC-Hunter'
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
