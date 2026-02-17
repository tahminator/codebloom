import type { Environment } from "types";

import { $ } from "bun";
import { getEnvVariables } from "load-secrets/env/load";
import yargs from "yargs";
import { hideBin } from "yargs/helpers";

const { environment: rawEnvironment, sha } = await yargs(hideBin(process.argv))
  .option("environment", {
    type: "string",
    describe: "Deployment environment (staging or production)",
    default: "Staging",
  })
  .option("sha", {
    type: "string",
    describe: "Commit SHA (required for staging)",
    default: "",
  })
  .strict()
  .parse();

const environment: Environment = (() => {
  const v = rawEnvironment;
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

  if (environment === "staging") {
    await $`git fetch origin main:main`;
    const diffOutput = await $`git diff --name-only main...${sha}`.text();
    const files = diffOutput.split("\n");

    const hasDbChanges = files.some((file) => file.startsWith("db/"));
    if (!hasDbChanges) {
      console.log("in staging, skipping db migration.");
      return;
    }
  }

  const appEnv = await getEnvVariables([
    environment === "staging" ? "staging" : "production-ro",
  ]);

  await $.env({
    ...process.env,
    ...appEnv,
  })`bash -c "./mvnw flyway:validate -Dflyway.locations=filesystem:./db/migration -Dflyway.ignoreMigrationPatterns='*:pending'"`;
}

main()
  .then(() => {
    process.exit(0);
  })
  .catch((e) => {
    console.error(e);
    process.exit(1);
  });
