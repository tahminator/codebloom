ALTER TABLE "LobbyPlayerQuestion"
ADD CONSTRAINT "uq_questionId_and_lobbyPlayerId" UNIQUE ("lobbyPlayerId", "questionId");