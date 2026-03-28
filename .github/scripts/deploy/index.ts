import type { Environment } from "@tahminator/pipeline";
import type { Type } from "types";

import { GitHubClient, Utils } from "@tahminator/pipeline";
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
  const ciEnv = await Utils.getEnvVariables(["ci"]);
  const { githubPat } = parseCiEnv(ciEnv);
  const ghClient = new GitHubClient(githubPat);

  if (type === "web") {
    await ghClient.updateK8sTagWithPR({
      manifestRepo: ["tahminator", "k8s-personal"],
      originRepo: ["tahminator", "codebloom"],
      kustomizationFilePath: `apps/${environment}/codebloom/kustomization.yaml`,
      imageName: "docker.io/tahminator/codebloom",
      newTag: newTagVersion,
      environment,
    });
  }

  if (type === "standup-bot") {
    await ghClient.updateK8sTagWithPR({
      manifestRepo: ["tahminator", "k8s-personal"],
      originRepo: ["tahminator", "codebloom"],
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
