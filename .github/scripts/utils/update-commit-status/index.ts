import { Octokit } from "@octokit/rest";

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
      const s = JSON.stringify(e);
      throw new Error(`GitHub API Error\n\n${s}`);
    }
  } catch (e) {
    console.error("Failed\n", e);
    process.exit(1);
  }
}
