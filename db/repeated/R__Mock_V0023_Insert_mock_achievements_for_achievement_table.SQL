DO $$
BEGIN
    INSERT INTO "Achievement" 
        (id, "userId", title, description, "isActive", place, leaderboard, "createdAt")
    VALUES
        (
            '265dd000-c310-11f0-8d3a-461b1b1abee9',
            '1717a2dc-ce77-4853-a620-82177799ad26',
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
            '1717a2dc-ce77-4853-a620-82177799ad26',
            'auto november = nullptr;',
            'Secured first place on the global leaderboard',
            true,
            'ONE',
            NULL,
            '2025-12-06 00:00:00+00'
        );

END $$;