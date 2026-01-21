import { $ } from "bun";
import { getEnvVariables } from "../load-secrets/env/load";

type CopyProdDbOptions = {
  baseDir?: string;
  environments?: string[];
};

type Env = {
  DATABASE_HOST: string;
  DATABASE_PORT: string;
  DATABASE_USER: string;
  DATABASE_PASSWORD: string;
  PRODUCTION_DATABASE_NAME: string;
  STAGING_DATABASE_NAME: string;
};

async function loadEnv(environments: string[], baseDir: string): Promise<Env> {
  const envVars = await getEnvVariables(environments, baseDir, false);

  const requiredKeys: (keyof Env)[] = [
    "DATABASE_HOST",
    "DATABASE_PORT",
    "DATABASE_USER",
    "DATABASE_PASSWORD",
    "PRODUCTION_DATABASE_NAME",
    "STAGING_DATABASE_NAME",
  ];

  const missing = requiredKeys.filter((key) => !envVars[key]);

  if (missing.length > 0) {
    throw new Error(
      `Missing required environment variables: ${missing.join(", ")}`,
    );
  }

  return envVars as Env;
}

function resolveCwd(baseDir?: string) {
  return baseDir && baseDir.trim().length > 0 ? baseDir : ".";
}

export async function copyProdDb(opts: CopyProdDbOptions = {}) {
  const baseDir = opts.baseDir ?? "";
  const environments = opts.environments ?? ["production"];
  const cwd = resolveCwd(baseDir);

  const env = await loadEnv(environments, baseDir);

  const flywayEnv = {
    ...process.env,
    DATABASE_NAME: env.STAGING_DATABASE_NAME,
  } as Record<string, string>;

  const pgEnv = {
    ...process.env,
    PGPASSWORD: env.DATABASE_PASSWORD,
  } as Record<string, string>;

  console.log("Cleaning staging database...");
  await $.env(
    flywayEnv,
  )`cd ${cwd} && ./mvnw flyway:clean -Dflyway.cleanDisabled=false`;

  console.log("Copying production database to staging...");
  await $.env(pgEnv)`cd ${cwd} && pg_dump \
    --host=${env.DATABASE_HOST} \
    --port=${env.DATABASE_PORT} \
    --username=${env.DATABASE_USER} \
    --dbname=${env.PRODUCTION_DATABASE_NAME} \
    --verbose \
    --clean \
    --if-exists \
    --format=plain \
    | sed '/SET transaction_timeout/d' \
    | psql \
        --host=${env.DATABASE_HOST} \
        --port=${env.DATABASE_PORT} \
        --username=${env.DATABASE_USER} \
        --dbname=${env.STAGING_DATABASE_NAME} \
        --echo-errors \
        --single-transaction`;

  console.log("Database copy completed successfully!");
}

copyProdDb({ baseDir: "infra", environments: ["production"] })
  .then(() => {
    process.exit();
  })
  .catch((e) => {
    console.error(e);
    process.exit(1);
  });
