import { $ } from "bun";
import type { Environment } from "redeploy/types";

export async function _migrateDb(environment: Environment) {
  await $`git fetch origin main:main`;
}
