BEGIN;

CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- Cleaning unnecesary data...
DELETE FROM "ApiKey";
DELETE FROM "Auth";
DELETE FROM "Club";
DELETE FROM "Session";

-- Scramble non-admin user data
UPDATE "User"
SET
  "nickname" = CASE
    WHEN "nickname" IS NOT NULL THEN 'user_' || encode(gen_random_bytes(8), 'hex')
    ELSE NULL
  END,
  "verifyKey" = encode(gen_random_bytes(16), 'hex'),
  "schoolEmail" = CASE
    WHEN "schoolEmail" IS NOT NULL THEN 'test_' || encode(gen_random_bytes(4), 'hex') || '@example.com'
    ELSE NULL
  END,
  "profileUrl" = 'https://via.placeholder.com/150'
WHERE "admin" IS NOT TRUE;

-- Scramble admin user data
UPDATE "User"
SET
  "leetcodeUsername" = NULL
WHERE "admin" IS TRUE;

-- Update DiscordClubMetadata for 'Patina Network'
UPDATE "DiscordClubMetadata" m
SET
  "guildId" = '1389762654452580373',
  "leaderboardChannelId" = '1417161536014778388'
FROM "DiscordClub" c
WHERE c."id" = m."discordClubId"
  AND c."name" = 'Patina Network';

-- Update DiscordClubMetadata for 'MHC++'
UPDATE "DiscordClubMetadata" m
SET
  "guildId" = '1389762654452580373',
  "leaderboardChannelId" = '1417161536014778388'
FROM "DiscordClub" c
WHERE c."id" = m."discordClubId"
  AND c."name" = 'MHC++';

-- Update DiscordClubMetadata for 'GWC - Hunter College'
UPDATE "DiscordClubMetadata" m
SET
  "guildId" = '1389762654452580373',
  "leaderboardChannelId" = '1417161536014778388'
FROM "DiscordClub" c
WHERE c."id" = m."discordClubId"
  AND c."name" = 'GWC - Hunter College';

COMMIT;