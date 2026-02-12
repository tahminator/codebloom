import { getEnvVariables } from "load-secrets/env/load";
import yargs from "yargs";
import { hideBin } from "yargs/helpers";

const excludedVars = [
  "PATH",
  "HOME",
  "PWD",
  "SHELL",
  "USER",
  "DEBUG",
  "LOG_LEVEL",
  "CI",
  "JAVA_HOME",
];

const argv = await yargs(hideBin(process.argv))
  .option("envs", {
    type: "string",
    describe: "Env names (ex: prod,staging,dev)",
    demandOption: true,
  })
  .option("githubEnv", {
    type: "string",
    describe: "Path to GITHUB_ENV",
    default: process.env.GITHUB_ENV,
  })
  .strict()
  .parse();

/**
 * @deprecated this is no longer a supported flow.
 */
async function main() {
  const envs = argv.envs
    .split(",")
    .map((e) => e.trim())
    .filter(Boolean);

  const loaded = await getEnvVariables(envs, {
    mask_PLZ_DO_NOT_TURN_OFF_UNLESS_YOU_KNOW_WHAT_UR_DOING: false,
  });

  const githubEnv = argv.githubEnv;
  if (!githubEnv) {
    console.log("Warning: GITHUB_ENV not set, skipping variable export");
    return;
  }

  const githubEnvFileWriter = Bun.file(githubEnv).writer();

  for (const [varName, value] of Object.entries(loaded)) {
    githubEnvFileWriter.write(`${varName}=${value}\n`);
    if (excludedVars.includes(varName)) {
      console.log(`Not masking ${varName}: Excluded`);
      continue;
    }

    if (value === "true" || value === "false" || value === "") {
      console.log(`Not masking ${varName}: true/false/empty value`);
      continue;
    }

    console.log(`Masking ${varName}`);
    console.log(`::add-mask::${value}`);
  }

  githubEnvFileWriter.flush();
}

main()
  .then(() => {
    process.exit(0);
  })
  .catch((e) => {
    console.error(e);
    process.exit(1);
  });
