import { $ } from "bun";
import { getEnvVariables } from "load-secrets/env/load";
import { backend } from "utils/run-backend-instance";
import { db } from "utils/run-local-db";
import { uploadFrontendTests } from "utils/upload";

const shouldUploadCoverage = process.env.UPLOAD_TEST_COV === "true";

async function main() {
  try {
    const ciAppEnv = await getEnvVariables(["ci-app"]);
    const localDbEnv = await db.start();
    await backend.start({ ...ciAppEnv, ...localDbEnv });

    const $$ = $.env({
      ...process.env,
      ...ciAppEnv,
      ...localDbEnv,
    });

    await $`corepack enable pnpm`;
    await $`pnpm --dir js i --frozen-lockfile`;
    await $$`pnpm --dir js run generate`;
    await $$`pnpm --dir js run test`;

    if (shouldUploadCoverage) {
      const ciEnv = await getEnvVariables(["ci"]);
      const { sonarToken } = parseCiEnv(ciEnv);

      await uploadFrontendTests(sonarToken);
    }
  } finally {
    await backend.end();
    await db.end();
  }
}

function parseCiEnv(ciEnv: Record<string, string>) {
  const sonarToken = (() => {
    const v = ciEnv["SONAR_TOKEN"];
    if (!v) {
      throw new Error("Missing SONAR_TOKEN from .env.ci");
    }
    return v;
  })();

  return { sonarToken };
}

main()
  .then(() => {
    process.exit(0);
  })
  .catch((e) => {
    console.error(e);
    process.exit(1);
  });
