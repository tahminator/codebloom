#!/usr/bin/env bash
set -euxo pipefail

# TODO clean up for CI/command use.

# Environment variables required:
# DATABASE_HOST, DATABASE_PORT, DATABASE_USER, DATABASE_PASSWORD
# PRODUCTION_DATABASE_NAME, STAGING_DATABASE_NAME

# may need libpq
# brew install libpq

export DATABASE_NAME="$STAGING_DATABASE_NAME"

echo "Cleaning staging database..."
./mvnw flyway:clean -Dflyway.cleanDisabled=false

echo "Copying production database to staging..."
PGPASSWORD="$DATABASE_PASSWORD" pg_dump \
    --host="$DATABASE_HOST" \
    --port="$DATABASE_PORT" \
    --username="$DATABASE_USER" \
    --dbname="$PRODUCTION_DATABASE_NAME" \
    --verbose \
    --clean \
    --if-exists \
    --format=plain \
    | sed '/SET transaction_timeout/d' \
    | PGPASSWORD="$DATABASE_PASSWORD" psql \
        --host="$DATABASE_HOST" \
        --port="$DATABASE_PORT" \
        --username="$DATABASE_USER" \
        --dbname="$STAGING_DATABASE_NAME" \
        --echo-errors \
        --single-transaction

echo "Cleaning unnecessary data..."
PGPASSWORD="$DATABASE_PASSWORD" psql \
    --host="$DATABASE_HOST" \
    --port="$DATABASE_PORT" \
    --username="$DATABASE_USER" \
    --dbname="$STAGING_DATABASE_NAME" \
    --command="
    DELETE FROM \"ApiKey\";
    DELETE FROM \"Auth\";
    DELETE FROM \"Club\";
    DELETE FROM \"Session\";
    
    -- Scramble non-admin user data
    UPDATE \"User\" 
    SET 
      \"nickname\" = CASE 
        WHEN \"nickname\" IS NOT NULL THEN 'user_' || encode(gen_random_bytes(8), 'hex')
        ELSE NULL 
      END,
      \"verifyKey\" = encode(gen_random_bytes(16), 'hex'),
      \"schoolEmail\" = CASE 
        WHEN \"schoolEmail\" IS NOT NULL THEN 'test_' || encode(gen_random_bytes(4), 'hex') || '@example.com'
        ELSE NULL 
      END,
      \"profileUrl\" = 'https://via.placeholder.com/150'
    WHERE \"admin\" IS NOT TRUE;

    -- Update DiscordClubMetadata for 'Patina Network'
    UPDATE \"DiscordClubMetadata\" m
    SET 
      \"guildId\" = '1389762654452580373',
      \"leaderboardChannelId\" = '1401739528057655436'
    FROM \"DiscordClub\" c
    WHERE c.\"id\" = m.\"discordClubId\"
      AND c.\"name\" = 'Patina Network';
  "

echo "Database copy completed successfully!"
