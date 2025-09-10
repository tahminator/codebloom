DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM "ApiKey"
        WHERE id = '48fa0072-2d3c-4c94-9003-0234efcf0209'
    ) THEN
        INSERT INTO "ApiKey"
            (id, "apiKeyHash", "expiresAt", "updatedBy")
        VALUES
            ('48fa0072-2d3c-4c94-9003-0234efcf0209', 'testVal', NOW() + INTERVAL '30 days', 'ed3bfe18-e42a-467f-b4fa-07e8da4d2555');
    END IF;
END $$;