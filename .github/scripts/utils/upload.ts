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
    await $`./codecov --help`;
  } catch {
    await $`curl -Os https://cli.codecov.io/latest/linux/codecov`;
    await $`sudo chmod +x codecov`;
    await $`./codecov --help`;
  }

  const $$ = $.env({
    ...process.env,
    CODECOV_TOKEN: token,
  });

  await $$`./codecov upload-process`.nothrow();

  try {
    console.log(`Uploading reports from ${dir} to Codecov...`);
    await $$`./codecov do-upload --dir ${dir} --non-interactive`;
    console.log("Codecov upload successful.");
  } catch (error) {
    console.error("Failed to upload to Codecov:", error);
    throw error;
  }
}

// can only run in github actions.
async function uploadArtifact(dir: string, artifactName: string) {
  const client = new DefaultArtifactClient();

  console.log("rootDir", process.cwd());

  const { id, size } = await client.uploadArtifact(
    artifactName,
    [dir],
    process.cwd(),
  );

  console.log(`Artifact ID ${id} uploaded (Size: ${size} bytes)`);
}
