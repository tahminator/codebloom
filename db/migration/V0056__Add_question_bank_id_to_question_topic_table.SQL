ALTER TABLE IF EXISTS "QuestionTopic"
ADD COLUMN "questionBankId" UUID NULL,
DROP CONSTRAINT "pk_question_topic",
ADD CONSTRAINT "pk_question_topic" UNIQUE ("questionId", "questionBankId", "topic"),
ADD CONSTRAINT "fk_question_bank" FOREIGN KEY ("questionBankId") REFERENCES "QuestionBank"(id) ON DELETE CASCADE ON UPDATE CASCADE;

COMMENT ON COLUMN "QuestionTopic"."questionId" IS
    'Exactly one of questionId or questionBankId must be non-null. If questionId is non-null, this topic refers to a question submission.';

COMMENT ON COLUMN "QuestionTopic"."questionBankId" IS
    'Exactly one of questionBankId or questionId must be non-null. If questionBankId is non-null, this topic refers to a question bank question.';