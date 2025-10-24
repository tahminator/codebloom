ALTER TABLE IF EXISTS "User"
ADD COLUMN "verifyKey" TEXT NULL DEFAULT encode(gen_random_bytes(16), 'hex');
