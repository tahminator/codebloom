CREATE TABLE user (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid () -- Auto-generate UUID
    discordId VARCHAR(32) UNIQUE NOT NULL -- Unique numeric Discord ID
);