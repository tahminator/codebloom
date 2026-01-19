import { sendMessage } from "utils/send-message";

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

export async function main() {
  await sendMessage(
    prId,
    `
### Available PR Commands

- \`/ai\` - Triggers all AI review commands at once
- \`/review\` - AI review of the PR changes
- \`/describe\` - AI-powered description of the PR
- \`/improve\` - AI-powered suggestions
- \`/deploy\` - Deploy to staging

See: https://github.com/tahminator/codebloom/wiki/CI-Commands
`.trim(),
  );
}

main()
  .then(() => {
    process.exit();
  })
  .catch((e) => {
    console.error(e);
    process.exit(1);
  });
