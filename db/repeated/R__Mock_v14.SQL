DO $$
BEGIN
INSERT INTO "User" 
    (id, "discordId", "discordName", "leetcodeUsername", "nickname", admin)
VALUES
    ('4466f9ff-84a5-44ff-9e7f-6be540bb2363', '987654321091234000', 'cornellperson1234', 'hahahahahcornellontop', NULL, false),
    ('d38584f4-ad0c-4f29-8c5f-afa12e651532', '876543210987313423', 'cornellperson1235', NULL, 'cornellgang', false);
INSERT INTO "UserTag"
    (id, "createdAt", "userId", tag)
VALUES
    ('c653c84a-62aa-46c6-a5e1-81ead7d39145', NOW() - INTERVAL '5 minutes', '4466f9ff-84a5-44ff-9e7f-6be540bb2363', 'Cornell'),
    ('32425808-df84-4d04-83dc-16f400ebf356', NOW() - INTERVAL '5 minutes', 'd38584f4-ad0c-4f29-8c5f-afa12e651532', 'Cornell');
INSERT INTO "Metadata"
    (id, "userId", "leaderboardId", "createdAt", "totalScore")
VALUES
    ('c20aba3e-506f-4fb1-a5e9-fc0b69f39d9a', '4466f9ff-84a5-44ff-9e7f-6be540bb2363', '8fc363b2-b5be-4f1a-9e0e-ea4844fc919c', NOW() - INTERVAL '5 minutes', 2500),
    ('c20d2def-13fb-456b-909b-4a465c1908cc', 'd38584f4-ad0c-4f29-8c5f-afa12e651532', '39bc2def-669f-4383-8ea3-7202efd613f2', NOW() - INTERVAL '5 minutes', 3200);
END $$
