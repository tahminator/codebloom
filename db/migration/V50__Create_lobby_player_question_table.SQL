CREATE TABLE IF NOT EXISTS "LobbyPlayerQuestion" (
    id UUID PRIMARY KEY,
    "lobbyPlayerId" UUID NOT NULL,
    "questionId" UUID NULL,
    "points" INTEGER NULL,
    CONSTRAINT "fk_lobbyPlayerQuestion_lobbyPlayerId_user" FOREIGN KEY ("lobbyPlayerId") REFERENCES "LobbyPlayer"(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT "fk_lobbyPlayerQuestion_questionId_user" FOREIGN KEY ("questionId") REFERENCES "Question"(id) ON DELETE CASCADE ON UPDATE CASCADE
);