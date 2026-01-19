import type { Environment } from "redeploy/types";

import { getEnvVariables } from "load-secrets/env/load";
import { _migrateDb } from "redeploy/db";
import { _migrateDo } from "redeploy/do";

const sha = process.env.SHA;

const projectId = (() => {
  const v = process.env.DIGITALOCEAN_PROJECT_ID;
  if (!v) {
    throw new Error("Project ID is required");
  }
  return v;
})();

const token = (() => {
  const v = process.env.DIGITALOCEAN_PAT;
  if (!v) {
    throw new Error("DigitalOcean PAT is required");
  }
  return v;
})();

const environment: Environment = (() => {
  const v = process.env.ENVIRONMENT;
  if (!v) {
    throw new Error("Environment is required");
  }

  if (v !== "staging" && v !== "production") {
    throw new Error('Environment must be "staging" or "production"');
  }

  return v;
})();

async function main() {
  // should already be called
  // await $`git-crypt unlock`;
  const envVariables = await getEnvVariables([environment]);

  await _migrateDb({
    envVariables,
    environment,
    sha,
  });

  await _migrateDo({
    projectId,
    token,
    environment,
    envVariables,
  });
}

main()
  .then(() => {
    process.exit(0);
  })
  .catch((e) => {
    console.error(e);
    process.exit(1);
  });
