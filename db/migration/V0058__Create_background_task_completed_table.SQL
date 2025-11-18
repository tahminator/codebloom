CREATE TYPE "BackgroundTaskEnum" AS ENUM (
    'LEETCODE_QUESTION_BANK'
);

CREATE TABLE "BackgroundTask" (
    id UUID PRIMARY KEY,
    task "BackgroundTaskEnum" NOT NULL,
    "completedAt" TIMESTAMP WITH TIME ZONE NOT NULL
);
