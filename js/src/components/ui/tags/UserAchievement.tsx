import AchievementCarousel from "@/components/ui/tags/AchievementCarousel";
import { Api } from "@/lib/api/types";
import { AchievementDtoPlace } from "@/lib/api/types/autogen/schema";
import { ApiUtils } from "@/lib/api/utils";
import { Image, Tooltip, Box, Text, Stack, Divider } from "@mantine/core";
import { useMemo } from "react";

type AchievementDto = Api<"AchievementDto">;
interface UserAchievementProps {
  achievements?: AchievementDto[];
  size: number;
  gap?: string | number;
  showHeader?: boolean;
}

const PLACE_CONFIG = {
  [AchievementDtoPlace.ONE]: "🥇",
  [AchievementDtoPlace.TWO]: "🥈",
  [AchievementDtoPlace.THREE]: "🥉",
} as const;

export default function UserAchievement({
  achievements = [],
  size,
  gap = "xs",
  showHeader = true,
}: UserAchievementProps) {
  const achievementItems = useMemo(
    () =>
      achievements
        .filter(
          (achievement) =>
            achievement.active && achievement.place in PLACE_CONFIG,
        )
        .map((achievement) =>
          achievement.leaderboard ?
            <LeaderboardAchievementBadge
              key={achievement.id}
              achievement={achievement}
              size={size}
            />
          : <GlobalTrophyBadge
              key={achievement.id}
              achievement={achievement}
            />,
        ),
    [achievements, size],
  );

  if (!achievementItems.length) return null;

  return (
    <Stack gap="md" align="center">
      <Divider w="70%" />
      {showHeader && (
        <Text size="sm" fw={500} c="dimmed">
          Achievements
        </Text>
      )}
      <AchievementCarousel
        items={achievementItems}
        visibleCount={3}
        gap={gap}
      />
    </Stack>
  );
}

interface AchievementBadgeProps {
  achievement: AchievementDto;
}

function GlobalTrophyBadge({ achievement }: AchievementBadgeProps) {
  const emoji = PLACE_CONFIG[achievement.place];

  return (
    <Tooltip label={achievement.title} withArrow position="top">
      <Box style={{ position: "relative", display: "inline-block" }}>
        <Text fz={34} lh={1}>
          🏆
        </Text>
        <Text
          style={{
            position: "absolute",
            top: -13,
            right: -7,
            fontSize: 22,
            lineHeight: 1,
          }}
        >
          {emoji}
        </Text>
      </Box>
    </Tooltip>
  );
}

interface LeaderboardAchievementBadgeProps {
  achievement: AchievementDto;
  size: number;
}

function LeaderboardAchievementBadge({
  achievement,
  size,
}: LeaderboardAchievementBadgeProps) {
  if (!achievement.leaderboard) return null;

  const emoji = PLACE_CONFIG[achievement.place];

  const metadata = ApiUtils.getTagMetadataFromLeaderboard(
    achievement.leaderboard,
  );
  return (
    <Tooltip label={achievement.title} withArrow position="top">
      <Box style={{ position: "relative", display: "inline-block" }}>
        <Image
          src={metadata.icon}
          alt={metadata.alt}
          style={{ height: size, width: "auto", cursor: "pointer" }}
        />
        <Text
          style={{
            position: "absolute",
            top: -10,
            right: -7,
            fontSize: 22,
            lineHeight: 1,
          }}
        >
          {emoji}
        </Text>
      </Box>
    </Tooltip>
  );
}
