import type { Environment } from "types";

import {
  DigitalOceanApiKeyAuthenticationProvider,
  FetchRequestAdapter,
  createDigitalOceanClient,
  type App_variable_definition,
  type App_response,
} from "@digitalocean/dots";
import { getEnvVariables } from "load-secrets/env/load";
import { _createAppAndgetAppId } from "redeploy/do/apps/create";
import { _getAppId } from "redeploy/do/apps/get";

import { prodSpec, stgSpec } from "../../../../.do/specs";

function parseCiEnv(ciEnv: Record<string, string>) {
  const user = (() => {
    const v = ciEnv["OPENSEARCH_USER"];
    if (!v) {
      throw new Error("Missing OPENSEARCH_USER from .env.ci");
    }
    return v;
  })();

  const pass = (() => {
    const v = ciEnv["OPENSEARCH_PASSWORD"];
    if (!v) {
      throw new Error("Missing OPENSEARCH_PASSWORD from .env.ci");
    }
    return v;
  })();

  return { user, pass };
}

export async function _migrateDo({
  token,
  environment,
  projectId,
  env,
}: {
  token: string;
  environment: Environment;
  projectId: string;
  env: Record<string, string>;
}): Promise<void> {
  const authProvider = new DigitalOceanApiKeyAuthenticationProvider(token);
  const adapter = new FetchRequestAdapter(authProvider);
  const client = createDigitalOceanClient(adapter);

  const envs: App_variable_definition[] = Object.entries(env).map(
    ([key, value]) => {
      const env: App_variable_definition = {
        key,
        value,
        scope: "RUN_TIME",
        type: "SECRET",
      };
      return env;
    },
  );

  const { user, pass } = parseCiEnv(await getEnvVariables(["ci"]));

  const spec =
    environment === "staging" ?
      stgSpec(envs, user, pass)
    : prodSpec(envs, user, pass);

  const appId = await (async () => {
    const v = await _getAppId(client, projectId, spec);

    if (v) {
      return v;
    }

    const v2 = await _createAppAndgetAppId(client, projectId, spec);
    if (!v2) {
      throw new Error(
        "App not found and failed to create as well. Please alert Codebloom team.",
      );
    }

    return v2;
  })();

  let res: App_response | undefined;
  try {
    res = await client.v2.apps.byApp_Id(appId).put({
      spec,
      updateAllSourceVersions: true,
    });
  } catch (e) {
    console.error(e);
    return process.exit(1);
  }

  const pendingDeploymentId = res?.app?.pendingDeployment?.id;

  if (!pendingDeploymentId) {
    console.error("Failed to find pending deployment.");
    process.exit(1);
  }

  console.log("Deployment ID:", pendingDeploymentId);

  let ready = false;
  const attempts = 60;

  for (let i = 1; i <= attempts; i++) {
    const res = await client.v2.apps
      .byApp_Id(appId)
      .deployments.byDeployment_id(pendingDeploymentId)
      .get();

    const phase = res?.deployment?.phase ?? "UNKNOWN";

    console.log("Deployment phase:", phase);

    if (phase === "SUPERSEDED") {
      console.error("Deployment was superseded by another deployment.");
      process.exit(1);
    }

    if (phase === "ACTIVE") {
      console.log("Deployment has completed!");
      ready = true;
      break;
    }

    console.log(`Waiting for deployment to complete... (${i}/${attempts})`);
    await Bun.sleep(10000);
  }

  if (!ready) {
    console.error("Deployment did not reach a valid state within 10 minutes.");
    process.exit(1);
  }
}
