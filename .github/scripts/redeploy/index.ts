import type { Environment } from "types";

import { getEnvVariables } from "load-secrets/env/load";
import { _migrateDb } from "redeploy/db";
import { _migrateDo } from "redeploy/do";

const environment: Environment = (() => {
  const v = process.env.ENVIRONMENT;
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

const sha = (() => {
  const v = process.env.SHA;
  if (environment === "staging" && !v) {
    throw new Error(
      "SHA must be available in ENV if script is being run in staging environment.",
    );
  }
  return v;
})();

async function main() {
  // should already be called
  // await $`git-crypt unlock`;

  const { token, projectId } = parseCiEnv(await getEnvVariables(["ci"]));
  const appEnv = await getEnvVariables([environment]);

  await _migrateDb({
    env: appEnv,
    environment,
    sha,
  });

  await _migrateDo({
    projectId,
    token,
    environment,
    env: appEnv,
  });
}

function parseCiEnv(ciEnv: Record<string, string>) {
  const token = (() => {
    const v = ciEnv["DIGITALOCEAN_PAT"];
    if (!v) {
      throw new Error("Missing DIGITALOCEAN_PAT from .env.ci");
    }
    return v;
  })();

  const projectId = (() => {
    const v = ciEnv["DIGITALOCEAN_PROJECT_ID"];
    if (!v) {
      throw new Error("Missing DIGITALOCEAN_PROJECT_ID from .env.ci");
    }
    return v;
  })();

  return {
    token,
    projectId,
  };
}

main()
  .then(() => {
    process.exit(0);
  })
  .catch((e) => {
    console.error(e);
    process.exit(1);
  });
