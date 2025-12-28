ALTER TABLE "Lobby"
ADD COLUMN "isTie" BOOLEAN NOT NULL DEFAULT false;

ALTER TABLE "Lobby"
ADD CONSTRAINT "chk_tie_logic" 
CHECK (
    ("isTie" = false) OR 
    ("isTie" = true AND "winnerId" IS NULL)
);