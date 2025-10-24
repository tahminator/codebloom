DO $$
BEGIN
INSERT INTO "User" 
    (id, "discordId", "discordName", "leetcodeUsername", "nickname", admin)
VALUES
    ('bbbbbbbb-e5f6-7890-abcd-ef1234567890', '987654321098765000', 'kazuha123', 'Kazuha', 'Kazuha', false);
INSERT INTO "UserTag"
    (id, "createdAt", "userId", tag)
VALUES
    ('c7c7c7c7-b2b2-c3c3-d4d4-e5e5e5e5e5e5', NOW() - INTERVAL '5 minutes', 'bbbbbbbb-e5f6-7890-abcd-ef1234567890', 'Sbu');
INSERT INTO "Metadata"
    (id, "userId", "leaderboardId", "createdAt", "totalScore")
VALUES
    ('c6c6c6c6-d2d2-e3e3-f4f4-a5a5a5a5a5a5', 'bbbbbbbb-e5f6-7890-abcd-ef1234567890', '39bc2def-669f-4383-8ea3-7202efd613f2', NOW() - INTERVAL '6 minutes', 2200);
END $$