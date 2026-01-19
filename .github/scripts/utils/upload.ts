import { DefaultArtifactClient } from "@actions/artifact";
import { $ } from "bun";
import path from "node:path";

export async function uploadBackendTests(token: string) {
  const dir = path.join(process.cwd(), "target/site/jacoco/");

  await uploadArtifact(dir, "backend-jacoco-report");
  await uploadToCodecov(token, dir);
}

async function uploadToCodecov(token: string, dir: string) {
  try {
    const { exitCode } = Bun.spawnSync(["./codecov", "--help"]);
    if (exitCode != 0) {
      throw Error();
    }
  } catch {
    console.log("Codecov not installed, installing now...");
    await $`curl -Os https://cli.codecov.io/latest/linux/codecov`;
    await $`chmod +x codecov`;
  }

  const env = {
    ...process.env,
    CODECOV_TOKEN: token,
  };

  console.log("Setting up Codecov in CI...");
  const p1 = Bun.spawnSync(["./codecov", "upload-process"], {
    env,
  });

  if (p1.exitCode != 0) {
    console.error(p1.stderr);
    throw new Error(`Failed to load Codecov process\n\n${p1.stderr}`);
  }

  try {
    console.log(`Uploading reports from ${dir} to Codecov...`);
    const p2 = Bun.spawnSync(["./codecov", "do-upload", "--dir", dir]);
    if (p2.exitCode != 0) {
      throw new Error(`Failed to load Codecov process\n\n${p2.stderr}`);
    }
    console.log("Codecov upload successful.");
  } catch (error) {
    console.error("Failed to upload to Codecov:", error);
    throw error;
  }
}

/*
 * can only run in github actions.
 */
async function uploadArtifact(dir: string, artifactName: string) {
  const client = new DefaultArtifactClient();

  const { id, size } = await client.uploadArtifact(
    artifactName,
    [dir],
    process.cwd(),
  );

  console.log(`Artifact ID ${id} uploaded (Size: ${size} bytes)`);
}
