import { _checkCommits } from "notion/commits";
import { checkNotionPrAndGetTask } from "notion/pr";

export * from "./pr";

const notionSecret = (() => {
  const v = process.env.NOTION_SECRET;
  if (!v) {
    throw new Error("NOTION_SECRET is required");
  }
  return v;
})();

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

const notionDbId = (() => {
  const v = process.env.NOTION_TASK_DB_ID;
  if (!v) {
    throw new Error("NOTION_TASK_DB_ID is required");
  }
  return v;
})();

async function main() {
  const { taskId, taskContent } = await checkNotionPrAndGetTask(
    notionSecret,
    prId,
    notionDbId,
  );

  console.log(taskContent);

  await _checkCommits(taskId, prId);
}

main()
  .then(() => {
    process.exit(0);
  })
  .catch((e) => {
    console.error(e);
    process.exit(1);
  });
