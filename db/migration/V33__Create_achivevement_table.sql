DROP TABLE IF EXISTS "Achievement";

CREATE TABLE "Achievement" (
    id UUID PRIMARY KEY,
    "userId" UUID NOT NULL,
    "iconUrl" VARCHAR(255),
    "title" VARCHAR(255) NOT NULL,
    "description" TEXT,
    "isActive" BOOLEAN DEFAULT true,
    "createdAt" TIMESTAMPTZ DEFAULT NOW(),
    "deletedAt" TIMESTAMPTZ
);