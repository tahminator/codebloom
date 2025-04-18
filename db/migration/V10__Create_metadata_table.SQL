CREATE TABLE IF NOT EXISTS "Metadata" (
    id UUID PRIMARY KEY,
    "userId" UUID NOT NULL,
    "leaderboardId" UUID NOT NULL,

    "createdAt" TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    "totalScore" INTEGER NOT NULL CHECK ("totalScore" >= 0),
    CONSTRAINT "fk_user" FOREIGN KEY ("userId") REFERENCES "User"(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT "fk_leaderboard" FOREIGN KEY ("leaderboardId") REFERENCES "Leaderboard"(id) ON DELETE CASCADE ON UPDATE CASCADE

)