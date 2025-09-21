# Staging Environment

## TL:DR

The staging environment can be found at [stg.codebloom.patinanetwork.org](https://stg.codebloom.patinanetwork.org).

Comment `/deploy` on any PR to start the deployment process.

If the task was started successfully, the bot will leave a message on the PR indicating the commit hash that it will be deploying to staging, as well as a link to the GitHub Action.

## Action Details

The action will run a full suite of tests and CI/CD (similar to the GitHub Action we use in production) before we build the image and deploy it to the environment.

If you have any questions, you can view the documentation for the production environment [here](https://github.com/tahminator/codebloom/blob/main/docs/infra/production.md), or view the defined GitHub Action yaml file [here](https://github.com/tahminator/codebloom/blob/main/.github/workflows/deploy-stg.yml).

<span style="color:#FFAAAB">NOTE</span>: If the specific commit has already been built before, subsequent `/deploy` commands will skip all tests and simply promote the image to the staging environment.

### Building, Tagging & Deployment

Once the image is built, the following tags are assigned to it.

- `staging-latest` (DigitalOcean will deploy whatever image is assigned to this tag)
- `staging-2024.03.15-14.30.25` (timestamp for history)
- `staging-a1b2c3d` (The first 7 characters of the commit SHA)

All images live in our [Docker Hub registry](https://hub.docker.com/repository/docker/tahminator/codebloom/tags).

Once the image is tagged, we make a request to the DigitalOcean API to re-deploy the instance and pull the latest image from `staging-latest`

## Database

### Secrets & Profiles

Our `staging` GitHub environment is a duplicate of `production`, but we replace some of the values so we don't exhaust our production resources. For example, we have a separate Discord client so that the staging environment doesn't accidentally affect production.

You can check our `.env.example.staging` file, which will indicate all the required secrets that our `staging` environment expects.

View [.env.example.staging](https://github.com/tahminator/codebloom/blob/main/.env.example.staging)
