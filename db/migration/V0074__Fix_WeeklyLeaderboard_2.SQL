DO $$
BEGIN
    CASE current_database()
        WHEN 'codebloom-prod' THEN
            UPDATE
                "WeeklyMessage"
            SET
                -- previous date 2026-02-09 12:02:01.340315 -05:00
                -- set to sunday, the 8th
                "createdAt" = '2026-02-08 12:02:01.340315 -05:00'
            WHERE
                id = '91c56b9b-92ed-4528-b6cc-d04c3bb0e2ab';
        ELSE
            RAISE NOTICE 'Skipping prod only migration: Current database is %', current_database();
    END CASE;
END $$;
