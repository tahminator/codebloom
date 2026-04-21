import { GitHubClient, Utils } from "@tahminator/pipeline";

import { getEnvVariables } from "../load-secrets/env/load";

export async function main() {
  const { githubAppAppId, githubAppInstallationId, githubAppPrivateKeyB64 } =
    parseCiEnv(await getEnvVariables(["ci"]));

  const ghClient = await GitHubClient.createWithGithubAppToken({
    appId: githubAppAppId,
    installationId: githubAppInstallationId,
    privateKey: await Utils.decodeBase64EncodedString(githubAppPrivateKeyB64),
  });

  await ghClient.createTag({
    onPreTagCreate: async (tag) => {
      await Utils.updateAllPackageJsonsWithVersion(tag);
    },
  });
}

function parseCiEnv(ciEnv: Record<string, string>) {
  const githubAppAppId = (() => {
    const v = ciEnv["GITHUB_APP_APP_ID"];
    if (!v) {
      throw new Error("Missing GITHUB_APP_APP_ID from .env.ci");
    }
    return v;
  })();

  const githubAppInstallationId = (() => {
    const v = ciEnv["GITHUB_APP_INSTALLATION_ID"];
    if (!v) {
      throw new Error("Missing GITHUB_APP_INSTALLATION_ID from .env.ci");
    }
    return v;
  })();

  const githubAppPrivateKeyB64 = (() => {
    const v = ciEnv["GITHUB_APP_PRIVATE_KEY_B64"];
    if (!v) {
      throw new Error("Missing GITHUB_APP_PRIVATE_KEY_B64 from .env.ci");
    }
    return v;
  })();

  return { githubAppAppId, githubAppInstallationId, githubAppPrivateKeyB64 };
}

main()
  .then(() => {
    process.exit(0);
  })
  .catch((e) => {
    console.error(e);
    process.exit(1);
  });