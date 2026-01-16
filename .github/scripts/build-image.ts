import { $ } from "bun";
import { db } from "./fn/run-local-db";
import { backend } from "./fn/run-backend-instance";

process.env.TZ = "America/New_York";

const tagPrefix = process.env.TAG_PREFIX || "";
const shouldDockerUpload = Boolean(process.env.DOCKER_UPLOAD) || false;
const serverProfiles = process.env.SERVER_PROFILES || "prod";

const dockerHubPat = process.env.DOCKER_HUB_PAT;
if (!dockerHubPat) {
  throw new Error("DOCKER_HUB_PAT is required.");
}

async function main() {
  try {
    await db.start();
    await backend.start();

    await $`corepack enable pnpm`;
    await $`pnpm --dir js i -D --frozen-lockfile`;
    await $`pnpm --dir js run generate`;

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
      `tahminator/codebloom:${tagPrefix}latest`,
      `tahminator/codebloom:${tagPrefix}${timestamp}`,
      `tahminator/codebloom:${tagPrefix}${gitSha}`,
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
      await $`docker buildx create --use --name codebloom-builder`;
    } catch {
      await $`docker buildx use codebloom-builder`;
    }

    const buildMode = shouldDockerUpload ? "--push" : "--load";

    const viteStagingArg =
      serverProfiles === "stg" ? "--build-arg VITE_STAGING=true" : "";

    const tagArgs = tags.map((tag) => `--tag ${tag}`).join(" ");

    await $`docker buildx build ${buildMode} \
              --file infra/Dockerfile \
              --build-arg SERVER_PROFILES=${serverProfiles} \
              --build-arg COMMIT_SHA=${gitSha} \
              --cache-from=type=gha \
              --cache-to=type=gha,mode=max \
              ${viteStagingArg} \
              ${tagArgs} \
              .`.quiet();

    console.log("Image pushed successfully.");
  } finally {
    await backend.end();
    await db.end();
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
