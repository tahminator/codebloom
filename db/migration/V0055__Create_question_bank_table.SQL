CREATE TABLE IF NOT EXISTS "QuestionBank" (
    id UUID PRIMARY KEY,
    "questionSlug" VARCHAR(256) NOT NULL,
    "questionDifficulty" "question_difficulty" NOT NULL,
    "questionLink" TEXT NOT NULL,
    "questionNumber" SMALLINT NOT NULL,
    "questionTitle" TEXT NOT NULL,
    description TEXT,
    "acceptanceRate" REAL NOT NULL,

    "createdAt" TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL
);