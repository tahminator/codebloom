DO $$
BEGIN
INSERT INTO "User" 
    (id, "discordId", "discordName", "leetcodeUsername", "nickname", admin)
VALUES
    ('a1b2c3d4-e5f6-7890-abcd-ef1234567810', '987654321091234567', 'drols123', 'drol12345', 'Drol', false),
    ('b2c3d4e5-f6a7-8901-bcde-f23456789039', '876543210987313131', 'bax123', 'bax12345', 'Bax', false);

INSERT INTO "UserTag"
    (id, "createdAt", "userId", tag)
VALUES
    ('baaaaaaa-b2b2-c3c3-d4d4-e5e5e5e5e5e5', NOW() - INTERVAL '5 minutes', 'a1b2c3d4-e5f6-7890-abcd-ef1234567810', 'Ccny'),
    ('abbbbbbb-c3c3-d4d4-e5e5-f6f6f6f6f6f6', NOW() - INTERVAL '5 minutes', 'b2c3d4e5-f6a7-8901-bcde-f23456789039', 'Columbia');
INSERT INTO "Metadata"
    (id, "userId", "leaderboardId", "createdAt", "totalScore")
VALUES
    ('b12b12bb-d2d2-e3e3-f4f4-a5a5a5a5a5a5', 'a1b2c3d4-e5f6-7890-abcd-ef1234567810', '39bc2def-669f-4383-8ea3-7202efd613f2', NOW() - INTERVAL '6 minutes', 2500),
    ('12345678-e3e3-f4f4-a5a5-b6b6b6b6b6b6', 'b2c3d4e5-f6a7-8901-bcde-f23456789039', '39bc2def-669f-4383-8ea3-7202efd613f2', NOW() - INTERVAL '6 minutes', 3200);

END $$