import { checkNotionPrAndGetTask } from "notion/pr";

export * from "./pr";

const notionPat = (() => {
  const v = process.env.NOTION_PAT;
  if (!v) {
    throw new Error("NOTION_PAT is required");
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
  const _ = checkNotionPrAndGetTask(notionPat, prId, notionDbId);
}

main()
  .then(() => {
    process.exit(0);
  })
  .catch((e) => {
    console.error(e);
    process.exit(1);
  });
