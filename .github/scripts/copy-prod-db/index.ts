import { $ } from "bun";
import { updateCommitStatus } from "utils/update-commit-status";

import { getEnvVariables } from "../load-secrets/env/load";

const AUTHORIZED_USER = "tahminator";

const username = (() => {
  const v = process.env.GITHUB_ACTOR;
  if (!v) {
    throw new Error("GITHUB_ACTOR is required");
  }
  return v;
})();

const sha = (() => {
  const v = process.env.SHA;
  if (!v) {
    throw new Error("SHA is required");
  }
  return v;
})();

const runUrl = (() => {
  const v = process.env.RUN_URL;
  if (!v) {
    throw new Error("RUN_URL is required");
  }
  return v;
})();

async function main() {
  try {
    await updateCommitStatus({
      sha,
      state: "pending",
      description: "Database copy in progress...",
      targetUrl: runUrl,
      context: "Copy Production DB to Staging",
    });

    if (username !== AUTHORIZED_USER) {
      throw new Error("You are not authorized!");
    }

    const env = await getEnvVariables(["production"], {
      baseDir: "infra",
    });

    const flywayEnv = {
      ...env,
      DATABASE_NAME: env.STAGING_DATABASE_NAME,
    };

    const pgEnv = {
      ...env,
      PGPASSWORD: env.DATABASE_PASSWORD,
    };

    console.log("Cleaning staging database...");
    await $.env(flywayEnv)`./mvnw flyway:clean -Dflyway.cleanDisabled=false`;

    console.log("Copying production database to staging...");
    await $.env(pgEnv)`PGPASSWORD="$DATABASE_PASSWORD" pg_dump \
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
          --single-transaction`;

    console.log("Cleaning unneccesary data...");
    await $.env(pgEnv)`PGPASSWORD="$DATABASE_PASSWORD" psql \
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

      -- Scramble admin user data
      UPDATE \"User\" 
      SET 
        \"leetcodeUsername\" = NULL
      WHERE \"admin\" IS TRUE;

      -- Update DiscordClubMetadata for 'Patina Network'
      UPDATE \"DiscordClubMetadata\" m
      SET 
        \"guildId\" = '1389762654452580373',
        \"leaderboardChannelId\" = '1417161536014778388'
      FROM \"DiscordClub\" c
      WHERE c.\"id\" = m.\"discordClubId\"
        AND c.\"name\" = 'Patina Network';

      -- Update DiscordClubMetadata for 'MHC++'
      UPDATE \"DiscordClubMetadata\" m
      SET
        \"guildId\" = '1389762654452580373',
        \"leaderboardChannelId\" = '1417161536014778388'
      FROM \"DiscordClub\" c
      WHERE c.\"id\" = m.\"discordClubId\"
        AND c.\"name\" = 'MHC++';

      -- Update DiscordClubMetadata for 'GWC - Hunter College'
      UPDATE \"DiscordClubMetadata\" m
      SET 
        \"guildId\" = '1389762654452580373',
        \"leaderboardChannelId\" = '1417161536014778388'
      FROM \"DiscordClub\" c
      WHERE c.\"id\" = m.\"discordClubId\"
        AND c.\"name\" = 'GWC - Hunter College';
    "`;

    await updateCommitStatus({
      sha,
      state: "success",
      description: "Database copy completed successfully",
      targetUrl: runUrl,
      context: "Copy Production DB to Staging",
    });
  } catch (e) {
    await updateCommitStatus({
      sha,
      state: "failure",
      description: "Database copy failed",
      targetUrl: runUrl,
      context: "Copy Production DB to Staging",
    });
    throw e;
  }
}

main()
  .then(() => {
    process.exit();
  })
  .catch(() => {
    process.exit(1);
  });
