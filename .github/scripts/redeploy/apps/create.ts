import type { App_spec } from "@digitalocean/dots";
import type { DigitalOceanClient } from "@digitalocean/dots/src/dots/digitalOceanClient";
import type { Environment } from "redeploy/types";

export async function _createAppAndgetAppId(
  client: DigitalOceanClient,
  projectId: string,
  spec: App_spec,
): Promise<string | null> {
  const res = await client.v2.apps.post({
    spec,
    projectId,
  });

  if (!res) {
    return null;
  }

  const { app } = res;

  if (!app) {
    return null;
  }

  return app.id ?? null;
}
