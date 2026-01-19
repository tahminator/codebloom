import type { Environment } from "redeploy/types";

import {
  createDigitalOceanClient,
  DigitalOceanApiKeyAuthenticationProvider,
  FetchRequestAdapter,
  type App_response,
  type App_variable_definition,
} from "@digitalocean/dots";
import { getEnvVariables } from "load-secrets/env/load";
import { _createAppAndgetAppId } from "redeploy/apps/create";
import { _getAppId } from "redeploy/apps/get";
import { _migrateDb } from "redeploy/db";

import { prodSpec, stgSpec } from "../../../.do/specs";

const projectId = (() => {
  const v = process.env.DIGITALOCEAN_PROJECT_ID;
  if (!v) {
    throw new Error("Project ID is required");
  }
  return v;
})();

const token = (() => {
  const v = process.env.DIGITALOCEAN_PAT;
  if (!v) {
    throw new Error("DigitalOcean PAT is required");
  }
  return v;
})();

const environment: Environment = (() => {
  const v = process.env.ENVIRONMENT;
  if (!v) {
    throw new Error("Environment is required");
  }

  if (v !== "staging" && v !== "production") {
    throw new Error('Environment must be "staging" or "production"');
  }

  return v;
})();

async function main() {
  const authProvider = new DigitalOceanApiKeyAuthenticationProvider(token);
  const adapter = new FetchRequestAdapter(authProvider);
  const client = createDigitalOceanClient(adapter);

  // should already be called
  // await $`git-crypt unlock`;
  const loaded = await getEnvVariables([environment]);

  const envs: App_variable_definition[] = loaded
    .entries()
    .map(([key, value]) => {
      const env: App_variable_definition = {
        key,
        value,
        scope: "RUN_TIME",
        type: "SECRET",
      };
      return env;
    })
    .toArray();

  const spec = environment === "staging" ? stgSpec(envs) : prodSpec(envs);

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

main()
  .then(() => {
    process.exit(0);
  })
  .catch((e) => {
    console.error(e);
    process.exit(1);
  });
