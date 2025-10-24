DO $$
BEGIN
        INSERT INTO "Session"
            (id, "expiresAt", "userId")
        VALUES 
            ('d99e10a2-6285-46f0-8150-ba4727b520f4', '2099-01-01 23:59:50', 'ed3bfe18-e42a-467f-b4fa-07e8da4d2555');
END $$
