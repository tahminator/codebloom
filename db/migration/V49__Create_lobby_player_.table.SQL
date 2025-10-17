CREATE TABLE IF NOT EXISTS "LobbyPlayer" (
    id UUID PRIMARY KEY,
    "lobbyId" UUID NOT NULL,
    "playerId" UUID NOT NULL,
    CONSTRAINT "fk_lobbyPlayer_playerId_user" FOREIGN KEY ("playerId") REFERENCES "User"(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT "fk_lobbyPlayer_lobbyId_user" FOREIGN KEY ("lobbyId") REFERENCES "Lobby"(id) ON DELETE CASCADE ON UPDATE CASCADE,
    points INTEGER NOT NULL DEFAULT 0
);
