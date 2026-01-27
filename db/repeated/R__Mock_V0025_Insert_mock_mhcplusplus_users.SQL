DO $$
BEGIN
INSERT INTO "User" 
    (id, "discordId", "discordName", "leetcodeUsername", "nickname", admin)
VALUES
    ('8e7380e1-31f9-4334-8cc5-5799ccf5b62f', '135792487321214299', 'mrmhcplusplus', 'mhcppleetcoder', NULL, false),
    ('1be7f6b5-3642-415d-81fc-cd1b9d4147b7', '095925441677898818', 'msmhcplusplus', NULL, 'nickname123', false);
INSERT INTO "UserTag"
    (id, "createdAt", "userId", tag)
VALUES
    ('63a78906-e1c2-4d51-9e8c-00737a3a8d09', NOW() - INTERVAL '7 minutes', '8e7380e1-31f9-4334-8cc5-5799ccf5b62f', 'MHCPlusPlus'),
    ('2c6aabde-604a-4905-bef6-3830854dab57', NOW() - INTERVAL '7 minutes', '1be7f6b5-3642-415d-81fc-cd1b9d4147b7', 'MHCPlusPlus');
INSERT INTO "Metadata"
    (id, "userId", "leaderboardId", "createdAt", "totalScore")
VALUES
    ('9887accb-37e7-4fab-b50b-c88f9ee206a0', '8e7380e1-31f9-4334-8cc5-5799ccf5b62f', '39bc2def-669f-4383-8ea3-7202efd613f2', NOW() - INTERVAL '7 minutes', 2500),
    ('bb1c904b-4f14-4dc0-85b4-bec9cf907962', '1be7f6b5-3642-415d-81fc-cd1b9d4147b7', '39bc2def-669f-4383-8ea3-7202efd613f2', NOW() - INTERVAL '7 minutes', 3200);
END $$
