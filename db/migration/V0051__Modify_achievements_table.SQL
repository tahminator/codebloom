ALTER TABLE "Achievement"
DROP COLUMN "iconUrl";

CREATE TYPE "AchievementPlaceEnum" AS ENUM (
    'ONE',
    'TWO',
    'THREE'
);

ALTER TABLE "Achievement"
ADD COLUMN place "AchievementPlaceEnum" NOT NULL,
ADD COLUMN leaderboard "Tag" NULL; -- null indicates global leaderboard.

