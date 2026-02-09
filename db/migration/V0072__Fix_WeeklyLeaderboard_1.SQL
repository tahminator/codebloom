DO $$
BEGIN
    CASE current_database()
        WHEN 'codebloom-prod' THEN
            UPDATE
                "WeeklyMessage"
            SET
                -- previous date 2026-02-02 17:13:49.873433 -05:00
                -- set to sunday
                "createdAt" = '2026-02-01 17:13:49.873433 -05:00'
            WHERE
                id = 'e960ad8a-021c-4120-b601-221fb7624dc0';
        ELSE
            RAISE NOTICE 'Skipping prod only migration: Current database is %', current_database();
    END CASE;
END $$;
