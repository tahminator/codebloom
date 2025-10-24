DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM "User" LIMIT 1) THEN
        UPDATE "User"
        SET admin = true
        WHERE id = 'ed3bfe18-e42a-467f-b4fa-07e8da4d2555';
    END IF;
END $$

