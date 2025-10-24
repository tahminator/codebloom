DO $$ BEGIN 
CREATE TYPE "LobbyStatusEnum" AS ENUM (
    'CLOSED',
    'AVAILABLE',
    'ACTIVE',
    'COMPLETED'
);
EXCEPTION
    WHEN duplicate_object THEN NULL;
END $$;

CREATE TABLE IF NOT EXISTS "Lobby" (
    id UUID PRIMARY KEY,
    "joinCode" TEXT NOT NULL,
    status "LobbyStatusEnum" NOT NULL,
    "createdAt" TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    "expiresAt" TIMESTAMP WITH TIME ZONE NOT NULL,
    "playerCount" INTEGER NOT NULL DEFAULT 0 CHECK ("playerCount" IN (0, 1, 2)),
    "winnerId" UUID NULL,
    CONSTRAINT "fk_lobby_winnerId_user" FOREIGN KEY ("winnerId") REFERENCES "User"(id) ON DELETE CASCADE ON UPDATE CASCADE
);