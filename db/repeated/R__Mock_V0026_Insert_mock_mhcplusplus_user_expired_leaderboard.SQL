DO $$
BEGIN
INSERT INTO "User" 
    (id, "discordId", "discordName", "leetcodeUsername", "nickname", admin)
VALUES
    ('772f60b0-cf65-4c87-9ae1-7c9838e73b18', '123459012349990002', 'expiredmhcpp', 'leetexpiredmhcpp', 'nickexpiredmhcpp', false);
INSERT INTO "UserTag"
    (id, "createdAt", "userId", tag)
VALUES
    ('0989da10-b3a9-4455-a32a-acb0681a6ff3', NOW() - INTERVAL '7 minutes', '772f60b0-cf65-4c87-9ae1-7c9838e73b18', 'MHCPlusPlus');
INSERT INTO "Metadata"
    (id, "userId", "leaderboardId", "createdAt", "totalScore")
VALUES
    ('69dda5e6-1aba-4b25-901b-cfc591da02f5', '772f60b0-cf65-4c87-9ae1-7c9838e73b18', '8fc363b2-b5be-4f1a-9e0e-ea4844fc919c', NOW() - INTERVAL '7 minutes', 2500);
END $$
