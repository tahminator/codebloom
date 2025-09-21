#!/usr/bin/env bash
set -euxo pipefail

# Environment variables required:
# DATABASE_HOST, DATABASE_PORT, DATABASE_USER, DATABASE_PASSWORD
# PRODUCTION_DATABASE_NAME, STAGING_DATABASE_NAME

# may need libpq
# brew install libpq

export DATABASE_NAME="$STAGING_DATABASE_NAME"

echo "Cleaning staging database..."
./mvnw flyway:clean -Dflyway.cleanDisabled=false

echo "Copying production database to staging..."
pg_dump \
    --host="$DATABASE_HOST" \
    --port="$DATABASE_PORT" \
    --username="$DATABASE_USER" \
    --dbname="$PRODUCTION_DATABASE_NAME" \
    --no-password \
    --verbose \
    --clean \
    --if-exists \
    --format=custom |
    pg_restore \
        --host="$DATABASE_HOST" \
        --port="$DATABASE_PORT" \
        --username="$DATABASE_USER" \
        --dbname="$STAGING_DATABASE_NAME" \
        --no-password \
        --verbose \
        --clean \
        --if-exists

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
  "

echo "Database copy completed successfully!"
