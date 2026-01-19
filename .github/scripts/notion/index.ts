import { getEnvVariables } from "load-secrets/env/load";
import { _checkCommits } from "notion/commits";
import { checkNotionPrAndGetTask } from "notion/pr";

export * from "./pr";

const prId = (() => {
  const v = process.env.PR_ID;
  if (!v) {
    throw new Error("PR_ID is required");
  }
  const n = Number(v);
  if (isNaN(n)) {
    throw new Error("PR_ID is not a number");
  }
  return n;
})();

async function main() {
  const { notionDbId, notionSecret } = parseCiEnv(
    await getEnvVariables(["ci"]),
  );

  const { taskId, taskContent } = await checkNotionPrAndGetTask(
    notionSecret,
    prId,
    notionDbId,
  );

  console.log(taskContent);

  await _checkCommits(taskId, prId);
}

function parseCiEnv(ciEnv: Map<string, string>) {
  const notionDbId = (() => {
    const v = ciEnv.get("NOTION_TASK_DB_ID");
    if (!v) {
      throw new Error("Missing NOTION_TASK_DB_ID from .env.ci");
    }
    return v;
  })();

  const notionSecret = (() => {
    const v = ciEnv.get("NOTION_SECRET");
    if (!v) {
      throw new Error("Missing NOTION_SECRET from .env.ci");
    }
    return v;
  })();

  return {
    notionDbId,
    notionSecret,
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
