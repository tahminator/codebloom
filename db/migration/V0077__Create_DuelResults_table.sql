CREATE TABLE IF NOT EXISTS "DuelResults" (
    id UUID PRIMARY KEY,
    "LobbyQuestionId" UUID NOT NULL,
    "QuestionId" UUID NOT NULL,
    CONSTRAINT "fk_lobbyquestion" FOREIGN KEY ("LobbyQuestionId") REFERENCES "LobbyQuestion"(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT "fk_question" FOREIGN KEY ("QuestionId") REFERENCES "Question"(id) ON DELETE CASCADE ON UPDATE CASCADE
)