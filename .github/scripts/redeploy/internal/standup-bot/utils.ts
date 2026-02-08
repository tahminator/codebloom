import type { Coolify } from "coolify/src";
import type { Data } from "coolify/src/models/operations";

export async function _getOrCreateStandupBotResource({
  client,
  projectUuid,
  serverUuid,
}: {
  client: Coolify;
  projectUuid: string;
  serverUuid: string;
}) {
  const project = await client.projects.get({
    uuid: projectUuid,
  });

  if (!project) {
    throw new Error("This project does not exist");
  }

  let apps = await client.applications.list();

  if (
    !apps ||
    !apps.length ||
    !apps.find((app) => app.name === "codebloom-standup-bot")
  ) {
    await client.applications.createDockerImage({
      projectUuid,
      name: "codebloom-standup-bot",
      dockerRegistryImageName: "tahminator/codebloom-standup-bot",
      dockerRegistryImageTag: "latest",
      serverUuid,
      description: "codebloom-standup-bot",
      environmentName: "production",
      portsExposes: "8080",
    });

    apps = await client.applications.list();
  }

  if (!apps || !apps.length) {
    throw new Error("No apps found in project even after creation step");
  }

  const app = apps.find((app) => app.name === "codebloom-standup-bot");

  if (!app) {
    throw new Error("Cannot find codebloom-standup-bot resource");
  }

  if (!app.uuid) {
    throw new Error("codebloom-standup-bot is missing UUID");
  }

  return app.uuid;
}

export async function _updateStandupBotAppEnvs({
  client,
  appUuid,
  envs,
}: {
  client: Coolify;
  appUuid: string;
  envs: Record<string, string>;
}) {
  const data: Data[] = Object.entries(envs).map(
    ([key, value]) =>
      ({
        key,
        value,
        isShownOnce: true,
        isBuildTime: false,
        isLiteral: false,
        isMultiline: false,
        isPreview: false,
      }) as Data,
  );

  await client.applications.updateEnvsBulk({
    uuid: appUuid,
    requestBody: {
      data,
    },
  });
}

export async function _triggerStandupBotDeployment({
  client,
  appUuid,
}: {
  client: Coolify;
  appUuid: string;
}) {
  const body = await client.deployments.deployByTagOrUuid({
    uuid: appUuid,
  });

  if (!body.deployments) {
    throw new Error("Failed to get back deployment body");
  }

  if (!body.deployments[0]) {
    throw new Error("Failed to find deployment");
  }

  if (!body.deployments[0].deploymentUuid) {
    throw new Error("Failed to find deployment UUID");
  }

  return body.deployments[0].deploymentUuid;
}
