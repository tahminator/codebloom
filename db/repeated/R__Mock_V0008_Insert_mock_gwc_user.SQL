DO $$
BEGIN
INSERT INTO "User" 
    (id, "discordId", "discordName", "leetcodeUsername", "nickname", admin)
VALUES
    ('a1b2c3d4-e5f6-7890-abcd-ef1234576890', '987654321098765434', 'techstudent5', 'coding_wizard_30', 'Chaewon', false);
INSERT INTO "UserTag"
    (id, "createdAt", "userId", tag)
VALUES
    ('a1b2c3d4-e5f6-7890-abcd-ef1234576890',NOW() - INTERVAL '5 minutes','a1b2c3d4-e5f6-7890-abcd-ef1234576890', 'Gwc');
INSERT INTO "Metadata"
    (id, "userId", "leaderboardId", "createdAt", "totalScore")
VALUES
    ('a1a1a1a1-a2d2-e3e3-f4f4-a5a5a5a5a5a5', 'a1b2c3d4-e5f6-7890-abcd-ef1234576890', '39bc2def-669f-4383-8ea3-7202efd613f2', NOW() - INTERVAL '6 minutes', 8500);

END $$