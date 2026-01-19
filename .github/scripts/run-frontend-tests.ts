import { $ } from "bun";
import { getEnvVariables } from "load-secrets/env/load";
import { backend } from "utils/run-backend-instance";
import { db } from "utils/run-local-db";

async function main() {
  try {
    const env = await getEnvVariables(["ci-app"]);
    const $$ = $.env(Object.fromEntries(env));

    await db.start();
    await backend.start(env);

    await $`corepack enable pnpm`;
    await $`pnpm --dir js i --frozen-lockfile`;
    await $$`pnpm --dir js run generate`;
    await $`pnpm --dir js run test`;
  } finally {
    await backend.end();
    await db.end();
  }
}

main()
  .then(() => {
    process.exit(0);
  })
  .catch((e) => {
    console.error(e);
    process.exit(1);
  });
