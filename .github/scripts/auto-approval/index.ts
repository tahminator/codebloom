import type { RestEndpointMethodTypes } from "@octokit/rest";

import { Octokit } from "octokit";
import yargs from "yargs";
import { hideBin } from "yargs/helpers";

const AUTHORIZED_USER = "tahminator";

const {
  githubToken,
  repo: rawRepo,
  prId,
} = await yargs(hideBin(process.argv))
  .option("githubToken", {
    type: "string",
    describe: "GitHub token",
    default: process.env.GH_TOKEN,
    demandOption: true,
  })
  .option("repo", {
    type: "string",
    describe: "Repository in owner/repo form",
    default: process.env.GITHUB_REPOSITORY,
    demandOption: true,
  })
  .option("prId", {
    type: "number",
    describe: "Pull request number",
    demandOption: true,
  })
  .strict()
  .parse();

const [owner, repo] = (() => {
  const v = rawRepo;
  if (!v) {
    throw new Error("GITHUB_REPOSITORY is required");
  }
  return v.split("/") as [string, string];
})();

async function main() {
  const client = new Octokit({
    auth: githubToken,
  });

  let res: RestEndpointMethodTypes["pulls"]["get"]["response"];
  try {
    res = await client.rest.pulls.get({
      owner,
      repo,
      pull_number: prId,
    });
  } catch (e) {
    const s = JSON.stringify(e);
    throw new Error(`GitHub API Error\n\n${s}`);
  }

  const { login: username } = res.data.user;

  if (username !== AUTHORIZED_USER) {
    console.warn("This PR will not be auto-approved, exiting...");
    return;
  }

  try {
    await client.rest.pulls.createReview({
      owner,
      repo,
      pull_number: prId,
      event: "APPROVE",
    });
  } catch (e) {
    const s = JSON.stringify(e);
    throw new Error(`GitHub API Error\n\n${s}`);
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
