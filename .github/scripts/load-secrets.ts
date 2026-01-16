import { $ } from "bun";

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

async function main() {
  await $`git-crypt unlock`;

  const envs = unloadEnvironments
    .split(",")
    .map((e) => e.trim())
    .filter(Boolean);

  const loaded = new Map<string, string>();

  for (const env of envs) {
    const envFile = Bun.file(`.env.${env}`);
    if (await envFile.exists()) {
      console.log(`Loading ${envFile.name}`);

      const content = await envFile.text();
      const lines = content.split("\n").filter((s) => s.length > 0);

      for (const line of lines) {
        const trimmed = line.trim();
        if (!trimmed || trimmed.startsWith("#")) continue;

        const match = trimmed.split("=").filter((s) => s.length > 0);
        if (match.length === 2) {
          const [key, value] = match;
          const cleanKey = key.trim();
          let cleanValue = value.trim();
          if (
            (cleanValue.startsWith('"') && cleanValue.endsWith('"')) ||
            (cleanValue.startsWith("'") && cleanValue.endsWith("'"))
          ) {
            cleanValue = cleanValue.slice(1, -1);
          }

          if (!loaded.has(cleanKey)) {
            loaded.set(cleanKey, cleanValue);
          }
        }
      }
    } else {
      console.log(`Warning: ${envFile} not found`);
    }
  }

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
