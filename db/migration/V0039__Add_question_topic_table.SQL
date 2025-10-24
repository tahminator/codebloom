CREATE TABLE IF NOT EXISTS "QuestionTopic" (
    id UUID PRIMARY KEY,
    "createdAt" TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    "questionId" UUID NOT NULL,
    "topicSlug" TEXT NOT NULL, -- raw leetcode slug, in case Leetcode adds a new topic that we don't support yet.
    "topic" "LeetcodeTopicEnum" NOT NULL, -- our topic column, stored as enum
    CONSTRAINT "pk_question_topic" UNIQUE ("questionId", "topic"),
    CONSTRAINT "fk_question" FOREIGN KEY ("questionId") REFERENCES "Question"(id) ON DELETE CASCADE ON UPDATE CASCADE
)
