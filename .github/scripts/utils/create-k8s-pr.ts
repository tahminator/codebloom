import type { Environment } from "types";

import { Octokit } from "octokit";
import { generateShortId } from "utils/short-id";
import yaml from "yaml";
import { z } from "zod";

const MANIFEST_REPO = "k8s-personal";
const MANIFEST_REPO_OWNER = "tahminator";

// this should point to this repo name
const ORIGIN = "tahminator/codebloom";

const kustomizeSchema = z.object({
  kind: z.literal("Kustomization"),
  images: z
    .array(
      z.object({
        name: z.string(),
        newTag: z.string(),
      }),
    )
    .optional(),
});

/**
 *
 * Update k8s manifest repo with new tag version.
 *
 * @note `kustomizationFile` must look like this:
 *
 * ```yaml
 * apiVersion: kustomize.config.k8s.io/v1beta1
 * kind: Kustomization
 * resources:
 *   - deployment.yaml
 *   - secrets.yaml
 *   - service.yaml
 *   - monitor.yaml
 * commonLabels:
 *   app: codebloom
 *   environment: production
 * # This part specifically
 * images:
 *   - name: docker.io/tahminator/codebloom
 *     newTag: a70ee0e
 * ```
 */
export async function updateK8sTagWithPR({
  githubPat,
  newTag,
  imageName,
  kustomizationFilePath,
  environment,
}: {
  githubPat: string;
  newTag: string;
  imageName: string;
  kustomizationFilePath: string;
  environment: Environment;
}) {
  const client = new Octokit({
    auth: githubPat,
  });

  const newBranchName = `${imageName}-${newTag}-${generateShortId()}`;

  const { data: repo } = await client.rest.repos.get({
    owner: MANIFEST_REPO_OWNER,
    repo: MANIFEST_REPO,
  });

  const baseBranch = repo.default_branch;

  const { data: ref } = await client.rest.git.getRef({
    owner: MANIFEST_REPO_OWNER,
    repo: MANIFEST_REPO,
    ref: `heads/${baseBranch}`,
  });

  await client.rest.git.createRef({
    owner: MANIFEST_REPO_OWNER,
    repo: MANIFEST_REPO,
    ref: `refs/heads/${newBranchName}`,
    sha: ref.object.sha,
  });

  const { data: file } = await client.rest.repos.getContent({
    owner: MANIFEST_REPO_OWNER,
    repo: MANIFEST_REPO,
    path: kustomizationFilePath,
    ref: newBranchName,
  });

  if (Array.isArray(file)) throw new Error("Unexpected file shape found");
  if (!file) throw new Error("Kustomization file not found");
  if (file.type !== "file") throw new Error("Unexpected file type found");
  if (!file.content) throw new Error("Kustomization file is empty");

  const currentYaml = Buffer.from(file.content ?? "", "base64").toString();
  const doc = yaml.parseDocument(currentYaml);
  const yamlObj = kustomizeSchema.parse(doc.toJS());

  const targetImage = yamlObj.images?.find((img) => img.name === imageName);
  if (!targetImage) {
    console.debug(yamlObj);
    throw new Error("Target image could not be found.");
  }

  targetImage.newTag = newTag;
  doc.set("images", yamlObj.images);

  const updatedYaml = doc.toString();

  await client.rest.repos.createOrUpdateFileContents({
    owner: MANIFEST_REPO_OWNER,
    repo: MANIFEST_REPO,
    path: kustomizationFilePath,
    message: `deploy: update ${imageName} to ${newTag}`,
    content: Buffer.from(updatedYaml).toString("base64"),
    sha: file.sha,
    branch: newBranchName,
  });

  const { data: pr } = await client.rest.pulls.create({
    owner: MANIFEST_REPO_OWNER,
    repo: MANIFEST_REPO,
    title: `Deploying ${newTag} for ${imageName} in ${environment}`,
    head: newBranchName,
    base: baseBranch,
    body: `Automated image tag change to ${newTag} for ${imageName} in ${environment} triggered by [${ORIGIN}](https://github.com/${ORIGIN}).`,
  });

  await client.rest.pulls.merge({
    owner: MANIFEST_REPO_OWNER,
    repo: MANIFEST_REPO,
    pull_number: pr.number,
    merge_method: "squash",
  });
}
