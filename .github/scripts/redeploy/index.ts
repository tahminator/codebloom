import { $ } from "bun";
import {
  createDigitalOceanClient,
  DigitalOceanApiKeyAuthenticationProvider,
  FetchRequestAdapter,
  type App_response,
  type App_variable_definition,
} from "@digitalocean/dots";
import { prodSpec, stgSpec } from "../../../.do/specs";
import { getEnvVariables } from "load-secrets/env/load";
import type { Environment } from "redeploy/types";
import { _createAppAndgetAppId } from "redeploy/apps/create";
import { _getAppId } from "redeploy/apps/get";

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
  console.log("start setup of do");
  const authProvider = new DigitalOceanApiKeyAuthenticationProvider(token);
  const adapter = new FetchRequestAdapter(authProvider);
  const client = createDigitalOceanClient(adapter);
  console.log("step of do created");

  console.log("env load pls");

  // should already be called
  // await $`git-crypt unlock`;
  const loaded = await getEnvVariables([environment]);
  console.log("env load done");

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
    const v = await _getAppId(client, environment, projectId);

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
  console.log(res?.app ?? "failed");

  await migrateDb(environment);
}

async function migrateDb(environment: Environment) {
  await $`git fetch origin main:main`;
}

main()
  .then(() => {
    process.exit(0);
  })
  .catch((e) => {
    console.error(e);
    process.exit(1);
  });
