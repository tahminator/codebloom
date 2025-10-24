DO $$
BEGIN
INSERT INTO "User" 
    (id, "discordId", "discordName", "leetcodeUsername", "nickname", admin)
VALUES
    ('feafee74-a6dc-11f0-9187-461b1b1abee9', '135468101241858291', 'expiredbmcc', 'leetexpiredbmcc', 'nickexpiredbmcc', false);
INSERT INTO "UserTag"
    (id, "createdAt", "userId", tag)
VALUES
    ('1f11789a-a6dd-11f0-b5de-461b1b1abee9', NOW() - INTERVAL '7 minutes', 'feafee74-a6dc-11f0-9187-461b1b1abee9', 'Bmcc');
INSERT INTO "Metadata"
    (id, "userId", "leaderboardId", "createdAt", "totalScore")
VALUES
    ('0fc839aa-a6dd-11f0-b76d-461b1b1abee9', 'feafee74-a6dc-11f0-9187-461b1b1abee9', '39bc2def-669f-4383-8ea3-7202efd613f2', NOW() - INTERVAL '7 minutes', 2500);
END $$