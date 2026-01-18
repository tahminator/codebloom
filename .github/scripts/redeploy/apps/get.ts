import type { DigitalOceanClient } from "@digitalocean/dots/src/dots/digitalOceanClient";
import type { Environment } from "redeploy/types";

export async function _getAppId(
  client: DigitalOceanClient,
  environment: Environment,
  projectId: string,
): Promise<string | null> {
  const appName = (() => {
    if (environment === "staging") {
      return "codebloom-staging";
    }

    if (environment === "production") {
      return "codebloom-prod";
    }

    throw new Error("This environment is not currently supported");
  })();

  const res = await client.v2.apps.get({
    queryParameters: {
      withProjects: true,
    },
  });

  if (!res) {
    return null;
  }

  const { apps } = res;

  if (!apps) {
    return null;
  }

  const foundApp = apps.filter(
    (app) => app.projectId == projectId && app.spec?.name == appName,
  )[0];

  if (!foundApp) {
    return null;
  }

  return foundApp.id ?? null;
}
