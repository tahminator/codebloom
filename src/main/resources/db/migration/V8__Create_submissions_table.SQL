CREATE TABLE IF NOT EXISTS "Submissions" (
    id SERIAL PRIMARY KEY,
    "leetcodeUsername" VARCHAR(255) NOT NULL,
    "questionSlug" VARCHAR(255) NOT NULL,
    "timestamp" VARCHAR(255),
    "statusDisplay" VARCHAR(255),
    "lang" VARCHAR(255)
);