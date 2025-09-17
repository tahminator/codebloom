DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM "User"
        WHERE id = '5a953746-f635-4f72-98ed-ffcd5763225f'
        AND "leetcodeUsername" = 'lucas_brown'
    ) THEN
        UPDATE "User"
        SET "leetcodeUsername" = 'lucas_brown_2'
        WHERE id = '5a953746-f635-4f72-98ed-ffcd5763225f';
    END IF;

END $$;
