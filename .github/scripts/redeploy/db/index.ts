import type { Environment } from "redeploy/types";

import { $ } from "bun";

export async function _migrateDb({
  environment,
  envVariables,
  sha,
}: {
  environment: Environment;
  envVariables: Map<string, string>;
  sha?: string;
}): Promise<void> {
  await $`git fetch origin main:main`;

  if (environment === "staging") {
    const diffOutput = await $`git diff --name-only main...${sha}`.text();
    const files = diffOutput.split("\n");

    const hasDbChanges = files.some((file) => file.startsWith("db/"));
    if (!hasDbChanges) {
      console.log("in staging, skipping db migration.");
      return;
    }
  }

  await $.env(
    Object.fromEntries(envVariables),
  )`./mvnw flyway:migrate -Dflyway.locations=filesystem:./db/migration`;
}
