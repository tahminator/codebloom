DO $$ 
BEGIN
INSERT INTO "Question" 
    (id, "createdAt", "questionSlug", "questionDifficulty", "questionNumber", "questionLink", "userId", "questionTitle", "acceptanceRate", "submittedAt")
VALUES
    ('5a3f1c26-8a34-4c7d-ac15-6691f8a1e5c2', NOW(), 'test-question-slug', 'Easy', 1, 'https://leetcode.com', 'ed3bfe18-e42a-467f-b4fa-07e8da4d2555', 'Test Question', 99.9, NOW());
END $$
