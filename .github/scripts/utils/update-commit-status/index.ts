import { Octokit } from "@octokit/rest";
import { RequestError } from "octokit";

const githubToken = (() => {
  const v = process.env.GH_TOKEN;
  return v;
})();

const [owner, repo] = (() => {
  const v = process.env.GITHUB_REPOSITORY;
  if (!v) {
    throw new Error("GITHUB_REPOSITORY is required");
  }
  return v.split("/") as [string, string];
})();

type CommitState = "error" | "failure" | "pending" | "success";

interface Options {
  sha: string;
  state: CommitState;
  description?: string;
  targetUrl?: string;
  context?: string;
}
export async function updateCommitStatus(options: Options) {
  const { sha, state, description, targetUrl, context } = options;

  try {
    if (!githubToken) {
      throw new Error("Some token should be set");
    }
    const client = new Octokit({
      auth: githubToken,
    });
    try {
      await client.rest.repos.createCommitStatus({
        owner,
        repo,
        sha,
        state,
        target_url: targetUrl,
        context: context,
        description: description,
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
  } catch (e) {
    console.error("Failed\n", e);
    process.exit(1);
  }
}
