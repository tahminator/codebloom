DO $$
BEGIN

DELETE FROM
    "User"
WHERE
    id = 'feafee74-a6dc-11f0-9187-461b1b1abee9';

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
    ('0fc839aa-a6dd-11f0-b76d-461b1b1abee9', 'feafee74-a6dc-11f0-9187-461b1b1abee9', '8fc363b2-b5be-4f1a-9e0e-ea4844fc919c', NOW() - INTERVAL '7 minutes', 2500);

END $$
