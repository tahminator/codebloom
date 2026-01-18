import { $ } from "bun";
import {
  createDigitalOceanClient,
  DigitalOceanApiKeyAuthenticationProvider,
  FetchRequestAdapter,
  type App_response,
  type App_variable_definition,
} from "@digitalocean/dots";
import { prodSpec, stgSpec } from "../../.do/specs";
import { getEnvVariables } from "./load-secrets";

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

type Environment = "staging" | "production";

async function main() {
  console.log("start setup of do");
  const authProvider = new DigitalOceanApiKeyAuthenticationProvider(token);
  const adapter = new FetchRequestAdapter(authProvider);
  const client = createDigitalOceanClient(adapter);
  console.log("step of do created");

  console.log("env load pls");
  await $`git-crypt unlock`;
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

  let res: App_response | undefined;
  try {
    res = await client.v2.apps.post({
      spec,
      projectId,
    });
  } catch (e) {
    console.error(e);
    return process.exit(1);
  }

  console.log(res?.app ?? "undefined");

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
