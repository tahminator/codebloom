import { $ } from "bun";
import { getEnvVariables } from "load-secrets/env/load";
import { backend } from "utils/run-backend-instance";
import { db } from "utils/run-local-db";
import { uploadFrontendTests } from "utils/upload";

async function main() {
  try {
    const shouldUploadCoverage = process.env.UPLOAD_TEST_COV === "true";
    const ciEnv =
      shouldUploadCoverage ? await getEnvVariables(["ci"]) : undefined;
    const codecovToken = ciEnv ? parseCiEnv(ciEnv).codecovToken : undefined;

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

    if (shouldUploadCoverage && codecovToken) {
      await uploadFrontendTests(codecovToken);
    }
  } finally {
    await backend.end();
    await db.end();
  }
}

function parseCiEnv(ciEnv: Record<string, string>) {
  const codecovToken = (() => {
    const v = ciEnv["CODECOV_TOKEN"];
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
