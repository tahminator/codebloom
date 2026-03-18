import { $ } from "bun";
import { sendMessage } from "utils/send-message";
import yargs from "yargs";
import { hideBin } from "yargs/helpers";

const { prId } = await yargs(hideBin(process.argv))
  .option("prId", {
    type: "number",
    describe: "Pull request number",
    demandOption: true,
  })
  .strict()
  .parse();

async function main() {
  const res = await $`gh pr view ${prId} --json body`.text();
  const { body } = JSON.parse(res) as { body?: string | null };

  const descriptionContent = (() => {
    const match = (body ?? "").match(
      /## Description of changes([\s\S]*?)(?=\n##|$)/,
    );
    let text = match?.[1] ?? "";
    let prev: string;
    do {
      prev = text;
      text = text.replace(/<!--[\s\S]*?-->/g, "");
    } while (text !== prev);
    return text.trim();
  })();

  if (descriptionContent) {
    console.log("PR description is filled out");
    return;
  }

  await sendMessage(
    prId,
    `
### PR Description Required
Please fill out the \`Description of changes\` section of the PR.`.trim(),
  );

  console.error("PR description is empty.");
  process.exit(1);
}

main()
  .then(() => {
    process.exit(0);
  })
  .catch((e) => {
    console.error(e);
    process.exit(1);
  });
