import type { RestEndpointMethodTypes } from "@octokit/rest";

import { Octokit, RequestError } from "octokit";

const AUTHORIZED_USER = "tahminator";

const githubToken = (() => {
  const v = process.env.GH_TOKEN;
  if (!v) {
    throw new Error("GH_TOKEN is required");
  }
  return v;
})();

const [owner, repo] = (() => {
  const v = process.env.GITHUB_REPOSITORY;
  if (!v) {
    throw new Error("GITHUB_REPOSITORY is required");
  }
  return v.split("/") as [string, string];
})();

const prId = (() => {
  const v = process.env.PR_ID;
  if (!v) {
    throw new Error("PR_ID is required");
  }
  const n = Number(v);
  if (Number.isNaN(n)) {
    throw new Error("PR_ID must be a number");
  }
  return n;
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
    let s: string;
    if (e instanceof RequestError) {
      s = JSON.stringify(e.response?.data);
    } else {
      s = String(e);
    }
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
    let s: string;
    if (e instanceof RequestError) {
      s = JSON.stringify(e.response?.data);
    } else {
      s = String(e);
    }
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
