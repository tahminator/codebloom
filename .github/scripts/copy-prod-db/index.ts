import { $ } from "bun";

type CopyProdDbOptions = {
  baseDir?: string;
};

type Env = {
  DATABASE_HOST: string;
  DATABASE_PORT: string;
  DATABASE_USER: string;
  DATABASE_PASSWORD: string;
  PRODUCTION_DATABASE_NAME: string;
  STAGING_DATABASE_NAME: string;
};

function loadEnv(): Env {
  const requiredKeys: (keyof Env)[] = [
    "DATABASE_HOST",
    "DATABASE_PORT",
    "DATABASE_USER",
    "DATABASE_PASSWORD",
    "PRODUCTION_DATABASE_NAME",
    "STAGING_DATABASE_NAME",
  ];

  const missing = requiredKeys.filter((key) => !process.env[key]);

  if (missing.length > 0) {
    throw new Error(
      `Missing required environment variables: ${missing.join(", ")}`,
    );
  }

  return {
    DATABASE_HOST: process.env.DATABASE_HOST!,
    DATABASE_PORT: process.env.DATABASE_PORT!,
    DATABASE_USER: process.env.DATABASE_USER!,
    DATABASE_PASSWORD: process.env.DATABASE_PASSWORD!,
    PRODUCTION_DATABASE_NAME: process.env.PRODUCTION_DATABASE_NAME!,
    STAGING_DATABASE_NAME: process.env.STAGING_DATABASE_NAME!,
  };
}

function resolveCwd(baseDir?: string) {
  return baseDir && baseDir.trim().length > 0 ? baseDir : ".";
}

export async function copyProdDb(opts: CopyProdDbOptions = {}) {
  const baseDir = opts.baseDir ?? "";
  const cwd = resolveCwd(baseDir);

  const env = loadEnv();

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

copyProdDb()
  .then(() => {
    process.exit();
  })
  .catch((e) => {
    console.error(e);
    process.exit(1);
  });
