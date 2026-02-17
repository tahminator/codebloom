import { sendMessage } from "utils/send-message";
import yargs from "yargs";
import { hideBin } from "yargs/helpers";

const { prId } = await yargs(hideBin(process.argv))
  .options("prId", {
    type: "number",
    describe: "Pull request number",
    default: 1,
  })
  .strict()
  .parse();

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
