import { getEnvVariables } from "load-secrets/env/load";

// UNLOAD_ENVIRONMENTS="prod,staging,dev"
const unloadEnvironments = process.env.UNLOAD_ENVIRONMENTS || "";

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

/**
 * @deprecated this is no longer a supported flow.
 */
async function main() {
  const envs = unloadEnvironments
    .split(",")
    .map((e) => e.trim())
    .filter(Boolean);

  const loaded = await getEnvVariables(envs, false);

  const githubEnv = process.env.GITHUB_ENV;
  if (!githubEnv) {
    console.log("Warning: GITHUB_ENV not set, skipping variable export");
    return;
  }

  const githubEnvFileWriter = Bun.file(githubEnv).writer();

  for (const [varName, value] of loaded.entries()) {
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
