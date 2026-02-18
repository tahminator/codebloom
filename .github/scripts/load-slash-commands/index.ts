import { COMMANDS } from "load-slash-commands/commands";
import yargs from "yargs";
import { hideBin } from "yargs/helpers";

const { getGhaOutput, githubOutput: githubOutputFile } = await yargs(
  hideBin(process.argv),
)
  .option("getGhaOutput", {
    type: "boolean",
    describe: "Enable GitHub Actions output",
    default: false,
  })
  .option("githubOutput", {
    type: "string",
    describe: "Path to GITHUB_OUTPUT",
    default: process.env.GITHUB_OUTPUT,
  })
  .strict()
  .parse();

async function main() {
  const cmds = COMMANDS.filter((c) => c.length > 0).join("\n");

  if (getGhaOutput && githubOutputFile) {
    console.log("Outputting cmds context...");
    const w = Bun.file(githubOutputFile).writer();
    await w.write(`commands<<EOF\n${cmds}\nEOF\n`);
    await w.flush();
    await w.end();
  } else if (getGhaOutput && !githubOutputFile) {
    throw new Error("GITHUB_OUTPUT or github-output arg is not supplied.");
  }
}

main()
  .then(() => {
    process.exit();
  })
  .catch(() => {
    process.exit(1);
  });
