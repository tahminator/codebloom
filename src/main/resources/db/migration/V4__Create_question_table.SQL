DO $$ BEGIN
    CREATE TYPE question_difficulty AS ENUM('Easy', 'Medium', 'Hard');
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

CREATE TABLE IF NOT EXISTS "Question" (
    id UUID PRIMARY KEY,
    "createdAt" TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    "questionSlug" VARCHAR(256) NOT NULL,
    -- Enum for questionDifficulty --
    "questionDifficulty" "question_difficulty" NOT NULL,
    "questionNumber" SMALLINT NOT NULL,
    "questionLink" TEXT NOT NULL,
    "pointsAwarded" INTEGER NULL,
    "userId" UUID NOT NULL,
    CONSTRAINT "fk_user" FOREIGN KEY ("userId") REFERENCES "User"(id) ON DELETE CASCADE ON UPDATE CASCADE
);