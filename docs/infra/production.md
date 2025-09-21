# Production Environment

## TL:DR

The production environment can be found at [codebloom.patinanetwork.org](https://codebloom.patinanetwork.org).

The production environment is deployed whenever something gets merged into the `main` branch. This includes any pending database migrations.

View the defined GitHub Action yaml file [here](https://github.com/tahminator/codebloom/blob/main/.github/workflows/ci-cd.yml)

## Action Details

The action will run a full suite of tests and CI/CD before we build the image and deploy it to the environment.

We currently run the following jobs:

- `backendPreTest` - This is a basic test that ensures that the backend has no compile-time issues & migrate the CI database, so we can catch it before we run our parallelized tests.

- `backendTests` - This will:
  - Run Checkstyle formatter
  - Build backend
  - Run tests, which include but are not limited to:
    - Unit tests on our Leetcode client
    - Integration tests for most of our database repositories
    - Integration tests on some of our endpoints
  - <span style="color:#FFAAAB">NOTE:</span> The CI database that we use for our integration tests will be automatically migrated with any database files you have inside of your branch. This happens inside of `backendPreTest`.
  - <span style="color:#FFAAAB">NOTE:</span> The CI database only contains mock data, so if you ever need to drop it, simply type `/drop-ci-db` inside of the PR to start the process. The bot will leave a message which will link you to the related GitHub Action.

- `frontendTests` - This will:
  - Run the backend
    - The backend will run so we can use the OpenAPI schema can be used to generate the types for our frontend layer.
  - Generate the types from the backend
  - Run full suite of tests, linters & autoformatters
    - This currently means: `tsc`, `eslint`, `postcss`, `prettier`, `stylelint`.
    - <span style="color:#FFAAAB">NOTE:</span> You can run `pnpm run fix` which will fix any formatting / styling / auto-fixable lint issues. This does not include `tsc`, you will have to fix TypeScript issues yourself.
  - Kill the backend server at the end, regardless of whether or not the frontend tests passed.

- `validateDBSchema` - This task will validate any pending migration files inside of your branch against the production database and ensure that we do not have any schema drift.

- `buildImage` - This will:
  - Run the backend again
  - Generate the types from the backend
  - Build the images, and add the following tags to the image
    - `latest` (this will always get deployed to production)
    - `latest-2024.03.15-14.30.25` (timestamp for history)
    - `latest-a1b2c3d` (The first 7 characters of the commit SHA)

- `redeploy` - Basic task that will trigger a POST request to the DigitalOcean API and deploy the `latest` image tag from our Docker Hub registry, which you can find [here](https://hub.docker.com/repository/docker/tahminator/codebloom/tags)

## Environment

We have a basic DigitalOcean instance that simply pulls `latest` from our Docker registry and deploy it with all our production environment credentials.

Our production credentials are updated manually, so please get in touch with Tahmid if you need to do so.

View [.env.example.production](https://github.com/tahminator/codebloom/blob/main/.env.example.production) for our production environment secrets
View [.env.example.ci](https://github.com/tahminator/codebloom/blob/main/.env.example.ci) for our Github Actions repository secrets
