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
  [AchievementDtoPlace.ONE]: { rank: 1, emoji: "🥇", text: "1st Place" },
  [AchievementDtoPlace.TWO]: { rank: 2, emoji: "🥈", text: "2nd Place" },
  [AchievementDtoPlace.THREE]: { rank: 3, emoji: "🥉", text: "3rd Place" },
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

function processAchievements(achievements: AchievementDto[]) {
  const achievementByLeaderboard = new Map<
    AchievementDtoLeaderboard,
    AchievementDto
  >();
  let bestGlobal: AchievementDto | undefined;

  for (const achievement of achievements) {
    if (!achievement.active || !isTopThreePlace(achievement.place)) {
      continue;
    }

    if (!achievement.leaderboard) {
      if (
        !bestGlobal ||
        PLACE_CONFIG[achievement.place].rank <
          PLACE_CONFIG[bestGlobal.place].rank
      ) {
        bestGlobal = achievement;
      }
      continue;
    }

    const leaderboard = achievement.leaderboard as AchievementDtoLeaderboard;
    const existing = achievementByLeaderboard.get(leaderboard);

    if (
      !existing ||
      PLACE_CONFIG[achievement.place].rank < PLACE_CONFIG[existing.place].rank
    ) {
      achievementByLeaderboard.set(leaderboard, achievement);
    }
  }

  return { bestGlobal, achievementByLeaderboard };
}

interface AchievementBadgeProps {
  achievement: AchievementDto;
}

function GlobalTrophyBadge({ achievement }: AchievementBadgeProps) {
  const config = PLACE_CONFIG[achievement.place];

  return (
    <Tooltip
      label={`${config.text} – ${achievement.title}`}
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

interface TagBadgeProps {
  userTag: UserTag;
  size: number;
  achievement?: AchievementDto;
}

function TagBadge({ userTag, size, achievement }: TagBadgeProps) {
  const metadata = ApiUtils.getMetadataByTagEnum(userTag.tag);

  const tooltipLabel =
    achievement ?
      `${PLACE_CONFIG[achievement.place].text} – ${achievement.leaderboard} Leaderboard`
    : metadata.name;

  return (
    <Tooltip label={tooltipLabel} withArrow position="top">
      <Box style={TROPHY_STYLES.container}>
        <Image
          src={metadata.icon}
          alt={metadata.alt}
          style={{ height: size, width: "auto", cursor: "pointer" }}
        />
        {achievement && (
          <Text style={TAG_MEDAL_STYLE}>
            {PLACE_CONFIG[achievement.place].emoji}
          </Text>
        )}
      </Box>
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

  const { bestGlobal, achievementByLeaderboard } =
    processAchievements(achievements);
  const items = [];
  if (bestGlobal) {
    items.push(
      <GlobalTrophyBadge key="global-trophy" achievement={bestGlobal} />,
    );
  }
  filteredTags.forEach((userTag) => {
    const leaderboardKey = userTag.tag as unknown as AchievementDtoLeaderboard;
    const achievement = achievementByLeaderboard.get(leaderboardKey);

    items.push(
      <TagBadge
        key={userTag.id}
        userTag={userTag}
        size={size}
        achievement={achievement}
      />,
    );
  });

  return (
    <AchievementCarousel visibleCount={3} gap={gap}>
      {items}
    </AchievementCarousel>
  );
}
