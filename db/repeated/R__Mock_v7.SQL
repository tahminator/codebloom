DO $$
BEGIN
INSERT INTO "User" 
    (id, "discordId", "discordName", "leetcodeUsername", "nickname", admin)
VALUES
    ('a1b2c3d4-e5f6-7890-abcd-ef1234567890', '987654321098765433', 'techstudent1', 'coding_wizard_21', 'Alex', false),
    ('b2c3d4e5-f6a7-8901-bcde-f23456789012', '876543210987654321', 'codingpro99', 'algorithm_master', 'Jordan', false);

INSERT INTO "UserTag"
    (id, "createdAt", "userId", tag)
VALUES
    ('a1a1a1a1-b2b2-c3c3-d4d4-e5e5e5e5e5e5', NOW() - INTERVAL '5 minutes', 'a1b2c3d4-e5f6-7890-abcd-ef1234567890', 'Rpi'),
    ('b2b2b2b2-c3c3-d4d4-e5e5-f6f6f6f6f6f6', NOW() - INTERVAL '5 minutes', 'b2c3d4e5-f6a7-8901-bcde-f23456789012', 'Baruch');
INSERT INTO "Metadata"
    (id, "userId", "leaderboardId", "createdAt", "totalScore")
VALUES
    ('c1c1c1c1-d2d2-e3e3-f4f4-a5a5a5a5a5a5', 'a1b2c3d4-e5f6-7890-abcd-ef1234567890', '39bc2def-669f-4383-8ea3-7202efd613f2', NOW() - INTERVAL '6 minutes', 8500),
    ('d2d2d2d2-e3e3-f4f4-a5a5-b6b6b6b6b6b6', 'b2c3d4e5-f6a7-8901-bcde-f23456789012', '39bc2def-669f-4383-8ea3-7202efd613f2', NOW() - INTERVAL '6 minutes', 7200);

END $$
