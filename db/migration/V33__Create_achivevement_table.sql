DROP TABLE IF EXISTS "Achievement";

CREATE TABLE "Achievement" (
    id UUID PRIMARY KEY,
    "userId" UUID NOT NULL,
    CONSTRAINT "fk_user" FOREIGN KEY ("userId") REFERENCES "User"(id) ON DELETE CASCADE ON UPDATE CASCADE
    "iconUrl" VARCHAR(255),
    "title" VARCHAR(255) NOT NULL,
    "description" TEXT,
    "isActive" BOOLEAN DEFAULT true,
    "createdAt" TIMESTAMPTZ DEFAULT NOW(),
    "deletedAt" TIMESTAMPTZ
);