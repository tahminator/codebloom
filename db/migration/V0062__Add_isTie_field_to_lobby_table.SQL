ALTER TABLE "Lobby"
ADD COLUMN "tie" BOOLEAN NOT NULL DEFAULT false;

ALTER TABLE "Lobby"
ADD CONSTRAINT "chk_tie_logic" 
CHECK (
    ("tie" = false) OR 
    ("tie" = true AND "winnerId" IS NULL)
);