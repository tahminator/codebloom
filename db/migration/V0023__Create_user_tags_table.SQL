-- Create the 'Tag' enum if it doesn't exist, with the first value of the "Tag" enum being "Patina"
DO $$ 
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'Tag') THEN
        CREATE TYPE "Tag" AS ENUM ('Patina');
    END IF;
END $$;

CREATE TABLE IF NOT EXISTS "UserTag" (
    id UUID PRIMARY KEY,
    "createdAt" TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    "userId" UUID NOT NULL,
    tag "Tag" NOT NULL,
    CONSTRAINT "pk_user_tags" UNIQUE ("userId", tag), -- Ensures one tag type per user.
    CONSTRAINT "fk_user" FOREIGN KEY ("userId") REFERENCES "User"(id) ON DELETE CASCADE ON UPDATE CASCADE
);
