import { $ } from "bun";

let isGitCryptUnlocked = false;

/**
 * @param environments - List of environment files to load.
 * @param mask_PLZ_DO_NOT_TURN_OFF_UNLESS_YOU_KNOW_WHAT_UR_DOING - Should variables be masked. Defaults to `true`. __NOTE: This will only work in a GitHub Action runner.__
 *
 * @returns a map of the loaded environments as a key and value inside of a map.
 *
 * _Please note that duplicate environment variables will be overwritten, so
 * the order in which you define `environments` does matter._
 */
export async function getEnvVariables(
  environments: string[],
  mask_PLZ_DO_NOT_TURN_OFF_UNLESS_YOU_KNOW_WHAT_UR_DOING = true,
): Promise<Map<string, string>> {
  if (!isGitCryptUnlocked) {
    await $`git-crypt unlock`;
    isGitCryptUnlocked = true;
  }

  const loaded = new Map<string, string>();

  for (const env of environments) {
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
          const [key, value] = match as [string, string];
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
      console.warn(`Warning: ${envFile} not found`);
    }
  }

  if (mask_PLZ_DO_NOT_TURN_OFF_UNLESS_YOU_KNOW_WHAT_UR_DOING) {
    for (const [varName, value] of loaded.entries()) {
      console.log(`Masking ${varName}`);
      console.log(`::add-mask::${value}`);
    }
  }

  return loaded;
}
