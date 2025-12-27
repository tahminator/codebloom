DO $$
BEGIN
    INSERT INTO "Achievement" 
        (id, "userId", title, description, "isActive", place, leaderboard, "createdAt")
    VALUES
        (
            '265dd000-c310-11f0-8d3a-461b1b1abee9',
            'aa7bfc3d-4849-45a0-83d9-d9c7494fb6df',
            'October Overflow',
            'Achieved first place on the Patina Network leaderboard',
            true,
            'ONE',
            'Patina',
            '2025-11-11 00:00:00+00'
        );

    INSERT INTO "Achievement" 
        (id, "userId", title, description, "isActive", place, leaderboard, "createdAt")
    VALUES
        (
            '365dd000-c310-11f0-8d3a-461b1b1abeea',
            'aa7bfc3d-4849-45a0-83d9-d9c7494fb6df',
            'auto november = nullptr;',
            'Secured first place on the global leaderboard',
            true,
            'ONE',
            NULL,
            '2025-12-06 00:00:00+00'
        );

END $$;