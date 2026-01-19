import { $ } from "bun";
import { sendMessage } from "utils/send-message";

export async function _checkCommits(taskId: number) {
  const taskIdString = taskId.toString();

  const res = await $`gh pr view ${taskId} --json commits`.text();
  const { commits } = JSON.parse(res) as {
    commits?: { messageHeadline: string }[];
  };

  if (!commits || commits.length === 0) {
    console.log("No commits found in PR");
    return;
  }

  const failedCommits = commits
    .map((commit) => commit.messageHeadline)
    .filter((message) => !message.startsWith(taskIdString));

  if (failedCommits.length == 0) {
    console.log("All commits are valid");
    return;
  }

  for (const message of failedCommits) {
    console.warn(`Invalid commit: ${message}`);
  }

  const failedList = failedCommits.join("\n");

  await sendMessage(
    taskId,
    `
### Commit Validation Failed
The following commits do not start with the required Notion ID \`${taskIdString}\`:

\`\`\`
${failedList}
\`\`\`

Please rebase and update your commit messages.
All messages should be of the following format: \`${taskIdString}: Example commit\``.trim(),
  );

  console.error("One or more commits do not match the Notion ID.");
  process.exit(1);
}
