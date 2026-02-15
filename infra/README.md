# `infra/`

> [!NOTE]
> Our staging to production migration script has been moved [here](https://github.com/tahminator/codebloom/tree/main/.github/scripts/copy-prod-db/index.ts#L28).

This directory contains the Dockerfile used to build the main Codebloom image, which is then uploaded to [hub.docker.com/r/tahminator/codebloom](https://hub.docker.com/r/tahminator/codebloom).

The image is then deployed to the [DigitalOcean App Platform](https://www.digitalocean.com/products/app-platform) see [`.do/`](../.do/) to view the DigitalOcean app spec and more detailed documentation regarding DigitalOcean and deployments.

There is a Bun Shell script which helps us manage the workflow for deployments across production and staging, which can be found at [`.github/scripts/redeploy/index.ts`](../.github/scripts/redeploy/index.ts).
