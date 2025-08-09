CREATE TABLE IF NOT EXISTS "Achievement" (
    id UUID NOT NULL PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES "User"(id),
    icon_url TEXT NOT NULL,
    title TEXT NOT NULL,
    description TEXT,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    deleted_at TIMESTAMPTZ
);