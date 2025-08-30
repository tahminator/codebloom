DO $$ 
BEGIN 
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'ApiKeyAccessEnum') THEN 
        CREATE TYPE "ApiKeyAccessEnum" AS ENUM ('GWC_READ_BY_USER'); 
    END IF; 
END $$;

CREATE TABLE IF NOT EXISTS "ApiKey" (
    id UUID PRIMARY KEY,
    "apiKeyHash" TEXT NOT NULL UNIQUE,
    access "ApiKeyAccessEnum" NOT NULL,
    "expiresAt" TIMESTAMP WITH TIME ZONE NOT NULL,
    "createdAt" TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    "updatedAt" TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    "updatedBy" UUID NOT NULL,
    CONSTRAINT "fk_apiKey_updatedBy_user" FOREIGN KEY ("updatedBy") REFERENCES "User"(id) ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS "ApiKeyAccess" (
    id UUID PRIMARY KEY,
    "apiKeyId" UUID NOT NULL,
    access "ApiKeyAccessEnum" NOT NULL,
    CONSTRAINT "uq_api_key_access_pair" UNIQUE ("apiKeyId", access),
    CONSTRAINT "fk_api_key_access_key" FOREIGN KEY ("apiKeyId") REFERENCES "ApiKey"(id) ON DELETE CASCADE ON UPDATE CASCADE
);