ALTER TABLE IF EXISTS "Question"
-- description could be really long, so we will accept anything instead of running into errors.
ADD COLUMN description TEXT NULL;