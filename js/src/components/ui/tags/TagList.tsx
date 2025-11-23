import AchievementCarousel from "@/components/ui/tags/AchievementCarousel";
import {
  components,
  AchievementDtoPlace,
  AchievementDtoLeaderboard,
} from "@/lib/api/types/autogen/schema";
import { UserTag } from "@/lib/api/types/usertag";
import { ApiUtils } from "@/lib/api/utils";
import { tagFF } from "@/lib/ff";
import { Image, Tooltip, Box, Text } from "@mantine/core";

type AchievementDto = components["schemas"]["AchievementDto"];

interface TagListProps {
  tags: UserTag[];
  achievements?: AchievementDto[];
  size?: number;
  gap?: string | number;
}

const PLACE_CONFIG = {
  [AchievementDtoPlace.ONE]: { rank: 1, emoji: "🥇" },
  [AchievementDtoPlace.TWO]: { rank: 2, emoji: "🥈" },
  [AchievementDtoPlace.THREE]: { rank: 3, emoji: "🥉" },
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

function isTopThreePlace(place: AchievementDtoPlace): boolean {
  return place in PLACE_CONFIG;
}

interface AchievementBadgeProps {
  achievement: AchievementDto;
}

function GlobalTrophyBadge({ achievement }: AchievementBadgeProps) {
  const config = PLACE_CONFIG[achievement.place];

  return (
    <Tooltip
      label={`${achievement.title}`}
      withArrow
      position="top"
    >
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
  const config = PLACE_CONFIG[achievement.place];
  const leaderboardAsTag = achievement.leaderboard as unknown as UserTag["tag"];
  const metadata = ApiUtils.getMetadataByTagEnum(leaderboardAsTag);
  return (
    <Tooltip
      label={`${achievement.leaderboard} – ${achievement.title}`}
      withArrow
      position="top"
    >
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
  if (!tagFF || !tags?.length) {
    return null;
  }

  const filteredTags = ApiUtils.filterUnusedTags(tags);

  if (!filteredTags.length && !achievements.length) {
    return null;
  }

  const items: JSX.Element[] = [];
  const leaderboardsWithAchievements = new Set<AchievementDtoLeaderboard>();

  achievements.forEach((achievement) => {
    if (achievement.active && isTopThreePlace(achievement.place)) {
      if (!achievement.leaderboard) {
        items.push(
          <GlobalTrophyBadge key={achievement.id} achievement={achievement} />,
        );
      } else {
        leaderboardsWithAchievements.add(achievement.leaderboard);
        items.push(
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
    const tagAsLeaderboard =
      userTag.tag as unknown as AchievementDtoLeaderboard;
    if (!leaderboardsWithAchievements.has(tagAsLeaderboard)) {
      items.push(<TagBadge key={userTag.id} userTag={userTag} size={size} />);
    }
  });

  return (
    <AchievementCarousel visibleCount={3} gap={gap}>
      {items}
    </AchievementCarousel>
  );
}
