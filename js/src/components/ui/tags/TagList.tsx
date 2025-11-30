import AchievementCarousel from "@/components/ui/carousel/ItemCarousel";
import { UserTag } from "@/lib/api/types/usertag";
import { ApiUtils } from "@/lib/api/utils";
import { tagFF } from "@/lib/ff";
import { Image, Tooltip, Stack, Divider, Text } from "@mantine/core";
import { useMemo } from "react";

interface TagListProps {
  tags: UserTag[];
  size?: number;
  gap?: string | number;
  expanded?: boolean;
}

interface TagBadgeProps {
  userTag: UserTag;
  size: number;
}

export default function TagList({
  tags,
  size = 20,
  gap = "xs",
  expanded = false,
}: TagListProps) {
  const filteredTags = ApiUtils.filterUnusedTags(tags);
  const tagItems = useMemo(
    () =>
      filteredTags.map((userTag) => (
        <TagBadge key={userTag.id} userTag={userTag} size={size} />
      )),
    [filteredTags, size],
  );

  if (!tagFF) return null;

  const hasTags = tagItems.length > 0;

  if (!hasTags) return null;

  return (
    <Stack gap="sm" align="center">
      {expanded && <Divider w="70%" />}
      <Stack gap="sm" align="center">
        {expanded && (
          <Text size="sm" fw={500} c="dimmed">
            Leaderboard
          </Text>
        )}
        <AchievementCarousel
          items={tagItems}
          visibleCount={3}
          gap={gap}
          enableCarousel={expanded}
        />
      </Stack>
    </Stack>
  );
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
