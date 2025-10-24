
DO $$ BEGIN
    CREATE TYPE "Tag" AS ENUM ('Gwc');
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

CREATE TABLE IF NOT EXISTS "Club" (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    slug VARCHAR(255) UNIQUE NOT NULL,
    "splashIconUrl" VARCHAR(500),
    password VARCHAR(255) NOT NULL,
    tag "Tag" NOT NULL
);