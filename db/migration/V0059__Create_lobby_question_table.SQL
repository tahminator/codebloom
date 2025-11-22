CREATE TABLE IF NOT EXISTS "LobbyQuestion" (
    id UUID PRIMARY KEY,
    "lobbyId" UUID NOT NULL,
    "questionBankId" UUID NOT NULL,
    "userSolvedCount" INTEGER NOT NULL DEFAULT 0,
    "createdAt" TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL,
    CONSTRAINT "fk_lobbyQuestion_lobbyId_user" FOREIGN KEY ("lobbyId") REFERENCES "Lobby"(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT "fk_lobbyQuestion_questionBankId_user" FOREIGN KEY ("questionBankId") REFERENCES "QuestionBank"(id) ON DELETE CASCADE ON UPDATE CASCADE
)