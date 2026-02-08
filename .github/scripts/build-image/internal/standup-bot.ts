import { $ } from "bun";
import { getEnvVariables } from "load-secrets/env/load";

process.env.TZ = "America/New_York";

const shouldDockerUpload = Boolean(process.env.DOCKER_UPLOAD) || false;

async function main() {
  const ciEnv = await getEnvVariables(["ci"]);
  const { dockerHubPat } = parseCiEnv(ciEnv);

  // copy old tz format from build-image.sh
  const timestamp = new Date()
    .toLocaleString("en-US", {
      timeZone: process.env.TZ,
      year: "numeric",
      month: "2-digit",
      day: "2-digit",
      hour: "2-digit",
      minute: "2-digit",
      second: "2-digit",
      hour12: false,
    })
    .replace(/(\d+)\/(\d+)\/(\d+),\s(\d+):(\d+):(\d+)/, "$3.$1.$2-$4.$5.$6");

  const gitSha = (await $`git rev-parse --short HEAD`.text()).trim();

  const tags = [
    `tahminator/codebloom-standup-bot:latest`,
    `tahminator/codebloom-standup-bot:${timestamp}`,
    `tahminator/codebloom-standup-bot:${gitSha}`,
  ];

  console.log("Building image with following tags:");
  tags.forEach((tag) => console.log(tag));

  if (dockerHubPat) {
    console.log("DOCKER_HUB_PAT found");
  } else {
    console.log("DOCKER_HUB_PAT missing or empty");
  }

  await $`echo ${dockerHubPat} | docker login -u tahminator --password-stdin`;

  try {
    await $`docker buildx create --use --name codebloom-standup-bot-builder`;
  } catch {
    await $`docker buildx use codebloom-standup-bot-builder`;
  }

  const buildMode = shouldDockerUpload ? "--push" : "--load";

  const tagArgs = tags.flatMap((tag) => ["--tag", tag]);

  console.log(`cwd is ${process.cwd()}`);

  await $`docker buildx build ${buildMode} \
              --platform linux/amd64 \
              --file internal/standup-bot/Dockerfile \
              --cache-from=type=gha \
              --cache-to=type=gha,mode=max \
              ${tagArgs} \
              .`;

  console.log("Image pushed successfully.");
}

function parseCiEnv(ciEnv: Record<string, string>) {
  const dockerHubPat = (() => {
    const v = ciEnv["DOCKER_HUB_PAT"];
    if (!v) {
      throw new Error("Missing DOCKER_HUB_PAT from .env.ci");
    }
    return v;
  })();

  return { dockerHubPat };
}

main()
  .then(() => {
    process.exit(0);
  })
  .catch((e) => {
    console.error(e);
    process.exit(1);
  });
