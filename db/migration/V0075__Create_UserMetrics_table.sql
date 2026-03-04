CREATE TABLE IF NOT EXISTS "UserMetrics" (
    id UUID NOT NULL PRIMARY KEY,
    "userId" UUID NOT NULL,
    points INTEGER NOT NULL,
    "createdAt" TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    "deletedAt" TIMESTAMP WITH TIME ZONE NULL, -- used to delete any bad metrics if we ever need to invalidate them
    CONSTRAINT "fk_user" FOREIGN KEY ("userId") REFERENCES "User"(id) ON DELETE CASCADE ON UPDATE CASCADE
);

COMMENT ON TABLE "UserMetrics" IS 'Tracks various metrics / snapshots of a user in a given day';
