import type { Environment } from "types";

import { getEnvVariables } from "load-secrets/env/load";
import { _migrateDb } from "redeploy/db";
import { _migrateDo } from "redeploy/do";
import yargs from "yargs";
import { hideBin } from "yargs/helpers";

const { environment, sha } = await yargs(hideBin(process.argv))
  .option("environment", {
    enum: ["staging", "production"] satisfies Environment[],
    describe: "Deployment environment (staging or production)",
    default: "staging" satisfies Environment as Environment,
  })
  .option("sha", {
    type: "string",
    describe: "Commit SHA (required for staging)",
    default: "",
  })
  .check(({ sha, environment }) => {
    if (environment === "staging" && !sha) {
      throw new Error(
        "SHA must be available in ENV if script is being run in staging environment.",
      );
    } else {
      return true;
    }
  })
  .strict()
  .parse();

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
