DO $$ BEGIN
INSERT INTO "Announcement" 
    (id, "createdAt", "expiresAt", "showTimer", message)
VALUES
    ('a2b2D3d4-e3f6-7820-abcd-ef5432176890', NOW() - INTERVAL '5 minutes', NOW() + INTERVAL '43800 minutes',true,'testing');



END $$