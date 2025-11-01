INSERT INTO "User" 
    (id, "discordId", "discordName", "leetcodeUsername", "nickname", admin)
VALUES
    ('71bbe3f2-2f09-4726-8f79-04554ce561e0', '987654321098765064', 'mockUserForLeaderboardRepoTest125', 'ExampleLC1', 'mockUserForLeaderboardRepoTestNick1', false),
    ('6b8579ca-2d67-42ab-89e9-e75656493f3e', '987654321098765070', 'mockUserForLeaderboardRepoTest126', 'ExampleLC2', 'mockUserForLeaderboardRepoTestNick2', false),
    ('1f0b6ce8-e820-414b-8085-c7d12f449b1c', '987654321098765085', 'mockUserForLeaderboardRepoTest127', 'ExampleLC3', 'mockUserForLeaderboardRepoTestNick3', false),
    ('1717a2dc-ce77-4853-a620-82177799ad26', '987654321098765090', 'mockUserForLeaderboardRepoTest128', NULL, 'mockUserForLeaderboardRepoTestNick4', false);

INSERT INTO "Metadata"
    (id, "userId", "leaderboardId", "createdAt", "totalScore")
VALUES
    ('c719e99f-7a4d-4b38-a652-af2b4d3ab774', '71bbe3f2-2f09-4726-8f79-04554ce561e0', '8fc363b2-b5be-4f1a-9e0e-ea4844fc919c', NOW() - INTERVAL '7 minutes', 3000),
    ('8269b952-516f-4f64-a4d1-17bbf13d859c', '6b8579ca-2d67-42ab-89e9-e75656493f3e', '8fc363b2-b5be-4f1a-9e0e-ea4844fc919c', NOW(), 3000),
    ('15299c76-1178-43f9-bdc5-9fecadb40f76', '1f0b6ce8-e820-414b-8085-c7d12f449b1c', '39bc2def-669f-4383-8ea3-7202efd613f2', NOW(), 0),
    ('db197554-149e-4a3f-89df-d8f3b18c6dac', '1717a2dc-ce77-4853-a620-82177799ad26', '39bc2def-669f-4383-8ea3-7202efd613f2', NOW(), 0);

INSERT INTO "UserTag" (id, "userId", tag, "createdAt")
VALUES
    ('28133d91-4483-4edd-b200-292996fc6448', '71bbe3f2-2f09-4726-8f79-04554ce561e0', 'Patina', NOW()),
    ('9c9e11be-4c59-4ab7-a850-ab82cf0b41ec', '6b8579ca-2d67-42ab-89e9-e75656493f3e', 'Baruch', NOW() - INTERVAL '1 day'),
    ('a2795254-c4cb-4f0f-8b3f-a1e960eb7b50', '6b8579ca-2d67-42ab-89e9-e75656493f3e', 'Patina', NOW() - INTERVAL '1 day'),
    ('e70c540b-763a-4c55-b8ba-3496965452e6', '1f0b6ce8-e820-414b-8085-c7d12f449b1c', 'Hunter', NOW() - INTERVAL '1 day'),
    ('3cf7532d-89ce-4fb1-9356-4a855d3ef41a', '1717a2dc-ce77-4853-a620-82177799ad26', 'Hunter', NOW() - INTERVAL '1 day');

