import type { App_spec } from "@digitalocean/dots";
import type { DigitalOceanClient } from "@digitalocean/dots/src/dots/digitalOceanClient";

export async function _getAppId(
  client: DigitalOceanClient,
  projectId: string,
  spec: App_spec,
): Promise<string | null> {
  const appName = spec.name;

  if (!appName) {
    throw new Error("App spec name missing, can't find app");
  }

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
