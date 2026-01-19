import type { Environment } from "types";

import { $ } from "bun";
import { getEnvVariables } from "load-secrets/env/load";

const environment: Environment = (() => {
  const v = process.env.ENVIRONMENT;
  if (!v) {
    throw new Error("Environment is required");
  }

  if (v !== "staging" && v !== "production") {
    throw new Error(
      'Environment must be the string literal "staging" or "production"',
    );
  }

  return v;
})();

export async function main() {
  // make sure you checkout the repo first.

  const appEnv = await getEnvVariables([
    environment === "staging" ? "staging" : "production-ro",
  ]);

  const onlyCheckPendingMigrationFlag =
    environment === "staging" ?
      "-Dflyway.ignoreMigrationPatterns=*:pending"
    : "";

  await $.env({
    ...process.env,
    ...Object.fromEntries(appEnv),
  })`./mvnw flyway:validate -Dflyway.locations=filesystem:./db/migration ${onlyCheckPendingMigrationFlag}`;
}

main()
  .then(() => {
    process.exit(0);
  })
  .catch((e) => {
    console.error(e);
    process.exit(1);
  });
