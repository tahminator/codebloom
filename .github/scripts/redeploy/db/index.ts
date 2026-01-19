import type { Environment } from "redeploy/types";

import { $ } from "bun";

export async function _migrateDb(environment: Environment) {
  environment.toUpperCase();
  await $`git fetch origin main:main`;
}
