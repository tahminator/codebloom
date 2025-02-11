ALTER TABLE IF EXISTS "Question"
ADD COLUMN "acceptanceRate" FLOAT NOT NULL CHECK ("acceptanceRate" > 0);