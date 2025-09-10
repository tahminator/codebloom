DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_enum e
        JOIN pg_type t ON e.enumtypid = t.oid
        WHERE t.typname = 'ApiKeyAccessEnum'
        AND e.enumlabel = 'TEST_VAL'
    ) THEN
        ALTER TYPE "ApiKeyAccessEnum" ADD VALUE 'TEST_VAL';
    END IF;
END $$;