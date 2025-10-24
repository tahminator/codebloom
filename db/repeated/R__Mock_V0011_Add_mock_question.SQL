DO $$ 
BEGIN
INSERT INTO "Question" 
    (id, "createdAt", "questionSlug", "questionDifficulty", "questionNumber", "questionLink", "userId", "questionTitle", "acceptanceRate", "submittedAt")
VALUES
    ('c9857a8a-9d0b-4d2e-b73c-3af2425bdca6', NOW(), 'test-question-slug', 'Easy', 1, 'https://leetcode.com', 'ed3bfe18-e42a-467f-b4fa-07e8da4d2555', 'Test Question', 99.9, NOW());
END $$
