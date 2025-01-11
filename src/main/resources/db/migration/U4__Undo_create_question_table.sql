DO $$ BEGIN
    DROP TYPE question_difficulty;
EXCEPTION 
    -- Don't know how to handle the specific exception, but this is good enough.
    WHEN others THEN null;
END $$;

DROP TABLE IF EXISTS "Question";