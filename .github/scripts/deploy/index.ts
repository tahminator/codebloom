import type { Environment, Type } from "types";

import { getEnvVariables } from "load-secrets/env/load";
import { updateK8sTagWithPR } from "utils/create-k8s-pr";
import yargs from "yargs";
import { hideBin } from "yargs/helpers";

const { environment, newTagVersion, type } = await yargs(hideBin(process.argv))
  .option("newTagVersion", {
    type: "string",
    demandOption: true,
  })
  .option("environment", {
    choices: ["staging", "production"] satisfies Environment[],
    describe: "Deployment environment (staging or production)",
    demandOption: true,
  })
  .option("type", {
    choices: ["web", "standup-bot"] satisfies Type[],
    describe: "Service type to deploy",
    demandOption: true,
  })
  .strict()
  .parse();

async function main() {
  const ciEnv = await getEnvVariables(["ci"]);
  const { githubPat } = parseCiEnv(ciEnv);

  if (type === "web") {
    await updateK8sTagWithPR({
      githubPat,
      kustomizationFilePath: `apps/${environment}/codebloom/kustomization.yaml`,
      imageName: "docker.io/tahminator/codebloom",
      newTag: newTagVersion,
      environment,
    });
  }

  if (type === "standup-bot") {
    await updateK8sTagWithPR({
      githubPat,
      kustomizationFilePath: `apps/${environment}/codebloom-standup-bot/kustomization.yaml`,
      imageName: "docker.io/tahminator/codebloom-standup-bot",
      newTag: newTagVersion,
      environment,
    });
  }
}

function parseCiEnv(ciEnv: Record<string, string>) {
  const githubPat = (() => {
    const v = ciEnv["GITHUB_PAT"];
    if (!v) {
      throw new Error("Missing GITHUB_PAT from .env.ci");
    }
    return v;
  })();

  return { githubPat };
}

main()
  .then(() => {
    process.exit(0);
  })
  .catch((e) => {
    console.error(e);
    process.exit(1);
  });
