-- The slug should be unique, but only per user.
ALTER TABLE IF EXISTS "Question"
ADD CONSTRAINT "Question_userId_questionSlug_unique" UNIQUE ("userId", "questionSlug");