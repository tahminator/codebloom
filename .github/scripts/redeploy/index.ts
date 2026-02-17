import type { Environment } from "types";

import { getEnvVariables } from "load-secrets/env/load";
import { _migrateDb } from "redeploy/db";
import { _migrateDo } from "redeploy/do";
import yargs from "yargs";
import { hideBin } from "yargs/helpers";

const { environment: rawEnvironment, sha } = await yargs(hideBin(process.argv))
  .option("environment", {
    type: "string",
    describe: "Deployment environment (staging or production)",
    default: "staging",
  })
  .option("sha", {
    type: "string",
    describe: "Commit SHA (required for staging)",
    default: "",
  })
  .strict()
  .parse();

const environment: Environment = (() => {
  const v = rawEnvironment;
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

async function main() {
  // should already be called
  // await $`git-crypt unlock`;

  const { token, projectId } = parseCiEnv(await getEnvVariables(["ci"]));
  const appEnv = await getEnvVariables([environment]);

  await _migrateDb({
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
