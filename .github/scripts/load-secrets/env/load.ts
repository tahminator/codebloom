/**
 * @param environments - List of environment files to load.
 * @param mask - Should variables be masked. Defaults to `true`. __NOTE: This will only work in a GitHub Action runner.__
 *
 * __NOTE: Be very careful of setting `mask` to `false`.__
 *
 * @returns a map of the loaded environments as a key and value inside of a map.
 *
 * _Please note that duplicate environment variables will be overwritten, so
 * the order in which you define `environments` does matter._
 */
export async function getEnvVariables(
  environments: string[],
  mask = true,
): Promise<Map<string, string>> {
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
      console.log(`Warning: ${envFile} not found`);
    }
  }

  if (mask) {
    for (const [varName, value] of loaded.entries()) {
      console.log(`Masking ${varName}`);
      console.log(`::add-mask::${value}`);
    }
  }

  return loaded;
}
