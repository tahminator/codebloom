DO $$
BEGIN
INSERT INTO "User" 
    (id, "discordId", "discordName", "leetcodeUsername", "nickname", admin)
VALUES
    ('8010914e-05ab-4405-a2c7-bdbdca1e7b6c', '987654321091234567', 'drols123', 'drol12345', 'Drol', false),
    ('d90eef02-e176-4b29-b897-4dfcd017ae8e', '876543210987313131', 'bax123', 'bax12345', 'Bax', false);

INSERT INTO "UserTag"
    (id, "createdAt", "userId", tag)
VALUES
    ('a0f895b4-afe9-4535-911d-b2ea4869fe3e', NOW() - INTERVAL '5 minutes', '8010914e-05ab-4405-a2c7-bdbdca1e7b6c', 'Ccny'),
    ('a4f2f3f4-1085-44d6-89f8-182ee84d953b', NOW() - INTERVAL '5 minutes', 'd90eef02-e176-4b29-b897-4dfcd017ae8e', 'Columbia');
INSERT INTO "Metadata"
    (id, "userId", "leaderboardId", "createdAt", "totalScore")
VALUES
    ('f5278e90-841f-4d6d-ab91-9bbd8b9dec0e', '8010914e-05ab-4405-a2c7-bdbdca1e7b6c', '39bc2def-669f-4383-8ea3-7202efd613f2', NOW() - INTERVAL '6 minutes', 2500),
    ('2ae5bc9d-094c-4ca7-b9bd-8f00c9d04940', 'd90eef02-e176-4b29-b897-4dfcd017ae8e', '39bc2def-669f-4383-8ea3-7202efd613f2', NOW() - INTERVAL '6 minutes', 3200);

END $$