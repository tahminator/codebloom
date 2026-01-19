import { $ } from "bun";
import { getEnvVariables } from "load-secrets/env/load";
import { db } from "utils/run-local-db";

async function main() {
  try {
    const env = await getEnvVariables(["ci-app"]);
    const $$ = $.env(Object.fromEntries(env));

    await db.start();

    await $`./mvnw -B install -D skipTests --no-transfer-progress`;

    await $`./mvnw -B exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install-deps"`;
    await $`./mvnw -B exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install firefox"`;

    await $`corepack enable pnpm`;
    await $`cd email && pnpm i --frozen-lockfile && ./email.sh && cd ..`;

    await $$`./mvnw clean verify -Dspring.profiles.active=ci`;
  } finally {
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
