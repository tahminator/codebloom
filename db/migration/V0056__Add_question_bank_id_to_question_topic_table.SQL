ALTER TABLE IF EXISTS "QuestionTopic"
ALTER COLUMN "questionId" DROP NOT NULL,
ADD COLUMN "questionBankId" UUID NULL,
DROP CONSTRAINT "pk_question_topic",
ADD CONSTRAINT "pk_question_topic" UNIQUE ("questionId", "questionBankId", "topic"),
ADD CONSTRAINT "fk_question_bank" FOREIGN KEY ("questionBankId") REFERENCES "QuestionBank"(id) ON DELETE CASCADE ON UPDATE CASCADE,
ADD CONSTRAINT "ck_question_topic_one_nonnull" CHECK (
    (("questionId" IS NOT NULL AND "questionBankId" IS NULL)
     OR
     ("questionId" IS NULL AND "questionBankId" IS NOT NULL))
);

COMMENT ON COLUMN "QuestionTopic"."questionId" IS
    'Exactly one of questionId or questionBankId must be non-null. If questionId is non-null, this topic refers to a question submission.';

COMMENT ON COLUMN "QuestionTopic"."questionBankId" IS
    'Exactly one of questionBankId or questionId must be non-null. If questionBankId is non-null, this topic refers to a question bank question.';