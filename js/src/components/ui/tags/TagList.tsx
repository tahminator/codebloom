import AchievementCarousel from "@/components/ui/tags/AchievementCarousel";
import {
  components,
  AchievementDtoPlace,
  AchievementDtoLeaderboard,
  UserTagTag,
} from "@/lib/api/types/autogen/schema";
import { UserTag } from "@/lib/api/types/usertag";
import { ApiUtils } from "@/lib/api/utils";
import { tagFF } from "@/lib/ff";
import { Image, Tooltip, Box, Text, Stack, Divider } from "@mantine/core";

type AchievementDto = components["schemas"]["AchievementDto"];

interface TagListProps {
  tags: UserTag[];
  achievements?: AchievementDto[];
  size?: number;
  gap?: string | number;
}

const PLACE_CONFIG = {
  [AchievementDtoPlace.ONE]: { emoji: "🥇" },
  [AchievementDtoPlace.TWO]: { emoji: "🥈" },
  [AchievementDtoPlace.THREE]: { emoji: "🥉" },
} as const;

const TROPHY_STYLES = {
  container: { position: "relative", display: "inline-block" } as const,
  trophy: { fontSize: 34, lineHeight: 1 } as const,
  medal: {
    position: "absolute",
    top: -13,
    right: -7,
    fontSize: 22,
    lineHeight: 1,
  } as const,
};

const TAG_MEDAL_STYLE = {
  position: "absolute",
  top: -10,
  right: -7,
  fontSize: 22,
  lineHeight: 1,
} as const;

const LEADERBOARD_TO_TAG: Record<AchievementDtoLeaderboard, UserTagTag> = {
  [AchievementDtoLeaderboard.Hunter]: UserTagTag.Hunter,
  [AchievementDtoLeaderboard.Patina]: UserTagTag.Patina,
  [AchievementDtoLeaderboard.Nyu]: UserTagTag.Nyu,
  [AchievementDtoLeaderboard.Baruch]: UserTagTag.Baruch,
  [AchievementDtoLeaderboard.Rpi]: UserTagTag.Rpi,
  [AchievementDtoLeaderboard.Sbu]: UserTagTag.Sbu,
  [AchievementDtoLeaderboard.Columbia]: UserTagTag.Columbia,
  [AchievementDtoLeaderboard.Ccny]: UserTagTag.Ccny,
  [AchievementDtoLeaderboard.Cornell]: UserTagTag.Cornell,
  [AchievementDtoLeaderboard.Bmcc]: UserTagTag.Bmcc,
  [AchievementDtoLeaderboard.Gwc]: UserTagTag.Gwc,
};

function isTopThreePlace(place: AchievementDtoPlace): boolean {
  return Boolean(PLACE_CONFIG[place]);
}

function getMetadataFromLeaderboard(leaderboard: AchievementDtoLeaderboard) {
  const tagEnum = LEADERBOARD_TO_TAG[leaderboard];
  return ApiUtils.getMetadataByTagEnum(tagEnum);
}

interface AchievementBadgeProps {
  achievement: AchievementDto;
}

function GlobalTrophyBadge({ achievement }: AchievementBadgeProps) {
  const config = PLACE_CONFIG[achievement.place];

  return (
    <Tooltip label={`${achievement.title}`} withArrow position="top">
      <Box style={TROPHY_STYLES.container}>
        <Text style={TROPHY_STYLES.trophy}>🏆</Text>
        <Text style={TROPHY_STYLES.medal}>{config.emoji}</Text>
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
  if (!achievement.leaderboard) {
    return null;
  }

  const config = PLACE_CONFIG[achievement.place];
  const metadata = getMetadataFromLeaderboard(achievement.leaderboard);

  return (
    <Tooltip label={`${achievement.title}`} withArrow position="top">
      <Box style={TROPHY_STYLES.container}>
        <Image
          src={metadata.icon}
          alt={metadata.alt}
          style={{ height: size, width: "auto", cursor: "pointer" }}
        />
        <Text style={TAG_MEDAL_STYLE}>{config.emoji}</Text>
      </Box>
    </Tooltip>
  );
}

interface TagBadgeProps {
  userTag: UserTag;
  size: number;
}

function TagBadge({ userTag, size }: TagBadgeProps) {
  const metadata = ApiUtils.getMetadataByTagEnum(userTag.tag);

  return (
    <Tooltip label={metadata.name} withArrow position="top">
      <Image
        src={metadata.icon}
        alt={metadata.alt}
        style={{ height: size, width: "auto", cursor: "pointer" }}
      />
    </Tooltip>
  );
}

export default function TagList({
  tags,
  achievements = [],
  size = 20,
  gap = "xs",
}: TagListProps) {
  if (!tagFF) return null;

  const filteredTags = ApiUtils.filterUnusedTags(tags ?? []);

  if (!filteredTags.length && !achievements?.length) {
    return null;
  }

  const achievementItems: JSX.Element[] = [];
  const tagItems: JSX.Element[] = [];

  achievements.forEach((achievement) => {
    if (achievement.active && isTopThreePlace(achievement.place)) {
      if (!achievement.leaderboard) {
        achievementItems.push(
          <GlobalTrophyBadge key={achievement.id} achievement={achievement} />,
        );
      } else {
        achievementItems.push(
          <LeaderboardAchievementBadge
            key={achievement.id}
            achievement={achievement}
            size={size}
          />,
        );
      }
    }
  });

  filteredTags.forEach((userTag) => {
    tagItems.push(<TagBadge key={userTag.id} userTag={userTag} size={size} />);
  });

  return (
    <Stack gap="md" align="center">
      {tagItems.length > 0 && (
        <Stack gap="xs" align="center">
          <AchievementCarousel visibleCount={3} gap={gap}>
            {tagItems}
          </AchievementCarousel>
        </Stack>
      )}
      {achievementItems.length > 0 && tagItems.length > 0 && (
        <Divider w="70%" />
      )}
      {achievementItems.length > 0 && (
        <Stack gap="sm" align="center">
          <Text size="sm" fw={500} c="dimmed">
            Achievements
          </Text>
          <AchievementCarousel visibleCount={3} gap={gap}>
            {achievementItems}
          </AchievementCarousel>
        </Stack>
      )}
    </Stack>
  );
}
