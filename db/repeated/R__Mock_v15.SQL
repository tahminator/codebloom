DO $$
BEGIN
INSERT INTO "User" 
    (id, "discordId", "discordName", "leetcodeUsername", "nickname", admin)
VALUES
    ('9e7c4f91-3b1a-4dc7-9022-ccf2b5c8b0ad', '135792468101214168', 'mrbmccperson', 'bmccleetcoder', NULL, false),
    ('d5a88b64-19e2-47db-a46f-6a42c39b2f13', '735920481657392814', 'msbmccperson', NULL, 'nickedbmcc', false);
INSERT INTO "UserTag"
    (id, "createdAt", "userId", tag)
VALUES
    ('dadd02e4-a645-11f0-9605-461b1b1abee9', NOW() - INTERVAL '7 minutes', '9e7c4f91-3b1a-4dc7-9022-ccf2b5c8b0ad', 'Bmcc'),
    ('e020f5d0-a645-11f0-a34d-461b1b1abee9', NOW() - INTERVAL '7 minutes', 'd5a88b64-19e2-47db-a46f-6a42c39b2f13', 'Bmcc');
INSERT INTO "Metadata"
    (id, "userId", "leaderboardId", "createdAt", "totalScore")
VALUES
    ('f1630fe0-a645-11f0-a00b-461b1b1abee9', '9e7c4f91-3b1a-4dc7-9022-ccf2b5c8b0ad', '39bc2def-669f-4383-8ea3-7202efd613f2', NOW() - INTERVAL '7 minutes', 2500),
    ('0e9345b2-a646-11f0-8343-461b1b1abee9', 'd5a88b64-19e2-47db-a46f-6a42c39b2f13', '39bc2def-669f-4383-8ea3-7202efd613f2', NOW() - INTERVAL '7 minutes', 3200);
END $$