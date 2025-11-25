import AchievementCarousel from "@/components/ui/tags/AchievementCarousel";
import UserAchievement from "@/components/ui/tags/UserAchievement";
import { components } from "@/lib/api/types/autogen/schema";
import { UserTag } from "@/lib/api/types/usertag";
import { ApiUtils } from "@/lib/api/utils";
import { tagFF } from "@/lib/ff";
import { Image, Tooltip, Stack, Divider, Text } from "@mantine/core";
import { useMemo } from "react";

type AchievementDto = components["schemas"]["AchievementDto"];

interface TagListProps {
  tags: UserTag[];
  achievements?: AchievementDto[];
  size?: number;
  gap?: string | number;
  showLeaderboardTitle?: boolean;
  showDivider?: boolean;
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
  showLeaderboardTitle = true,
  showDivider = true,
}: TagListProps) {

  const filteredTags = ApiUtils.filterUnusedTags(tags ?? []);

  const tagItems = useMemo(
    () =>
      filteredTags.map((userTag) => (
        <TagBadge key={userTag.id} userTag={userTag} size={size} />
      )),
    [filteredTags, size],
  );

  if (!tagFF) return null;

  const hasTags = tagItems.length > 0;
  const hasAchievements = (achievements?.length ?? 0) > 0;

  if (!hasTags && !hasAchievements ) return null;

  return (
  <Stack gap="md" align="center">
    {showDivider && <Divider w="70%" />}
    {hasTags && (
      <Stack gap="xs" align="center">
        {showLeaderboardTitle && (
          <Text size="sm" fw={500} c="dimmed">
            Leaderboard
          </Text>
        )}
        <AchievementCarousel visibleCount={3} gap={gap}>
          {tagItems}
        </AchievementCarousel>
      </Stack>
    )}
    {showLeaderboardTitle && hasTags && hasAchievements && (
      <Divider w="70%" />
    )}
    {hasAchievements && (
      <UserAchievement
        achievements={achievements}
        size={size}
        gap={gap}
        showHeader={showLeaderboardTitle}
      />
    )}
  </Stack>
  );
}
