import type { Environment } from "types";

import { $ } from "bun";

export async function _migrateDb({
  environment,
  env,
  sha,
}: {
  environment: Environment;
  env: Record<string, string>;
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
    env,
  )`./mvnw flyway:migrate -Dflyway.locations=filesystem:./db/migration`;
}
