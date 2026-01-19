import { $ } from "bun";
import { getEnvVariables } from "load-secrets/env/load";
import { backend } from "utils/run-backend-instance";
import { frontend } from "utils/run-frontend-instance";
import { db } from "utils/run-local-db";
import { uploadBackendTests } from "utils/upload";

async function main() {
  try {
    const ciEnv = await getEnvVariables(["ci"]);
    const { codecovToken } = parseCiEnv(ciEnv);
    const ciAppEnv = await getEnvVariables(["ci-app"]);
    const localDbEnv = await db.start();

    // backend starts so we can generate schema, then kill it.
    await backend.start({ ...ciAppEnv, ...localDbEnv });
    await frontend.start(ciAppEnv);
    await backend.end();

    if (!localDbEnv) {
      throw new Error("Local db empty when it should not be");
    }

    const $$ = $.env({
      ...process.env,
      ...Object.fromEntries(ciAppEnv),
      ...localDbEnv,
    });

    await $`./mvnw -B install -D skipTests --no-transfer-progress`;

    await $`./mvnw -B exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install-deps"`;
    await $`./mvnw -B exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install firefox"`;

    await $`corepack enable pnpm`;
    await $`cd email && pnpm i --frozen-lockfile && ./email.sh && cd ..`;

    await $$`./mvnw clean verify -Dspring.profiles.active=ci`;

    await uploadBackendTests(codecovToken);
  } finally {
    await db.end();
    await frontend.end();
    await backend.end();
  }
}

function parseCiEnv(ciEnv: Map<string, string>) {
  const codecovToken = (() => {
    const v = ciEnv.get("CODECOV_TOKEN");
    if (!v) {
      throw new Error("Missing CODECOV_TOKEN from .env.ci");
    }
    return v;
  })();

  return { codecovToken };
}

main()
  .then(() => {
    process.exit(0);
  })
  .catch((e) => {
    console.error(e);
    process.exit(1);
  });
