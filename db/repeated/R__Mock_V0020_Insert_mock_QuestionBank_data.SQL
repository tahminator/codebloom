DO $$
BEGIN
INSERT INTO "QuestionBank" 
    (id, "questionSlug", "questionDifficulty", "questionLink", "questionNumber", "questionTitle", description, "acceptanceRate", "createdAt")
VALUES
    ('165dd000-c310-11f0-8d3a-461b1b1abee8', 'very-hard-question', 'Hard', 'https://leetcode.com', 31337, 'VERY HARD QUESTION', 'Solve P=NP', 0.001, NOW());
END $$