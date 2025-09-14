import { UserTag } from "@/lib/api/types/usertag";
import { ApiUtils } from "@/lib/api/utils";
import { tagFF } from "@/lib/ff";
import { Image, Tooltip, Group } from "@mantine/core";

interface TagListProps {
  tags: UserTag[];
  size: number;
  gap: string | number;
}

export default function TagList({ tags, size = 20, gap = "xs" }: TagListProps) {
  if (!tagFF || !tags || tags.length === 0) {
    return <></>;
  }

  const filteredTags = ApiUtils.filterUnusedTags(tags);

  if (filteredTags.length === 0) {
    return <></>;
  }

  return (
    <Group gap={gap} wrap="nowrap">
      {filteredTags.map((userTag) => {
        const metadata = ApiUtils.getMetadataByTagEnum(userTag.tag);

        return (
          <Tooltip
            key={userTag.id}
            label={metadata.name}
            color="dark.4"
            position="top"
            withArrow
          >
            <Image
              src={metadata.icon}
              alt={metadata.alt}
              style={{
                height: size,
                width: "auto",
                cursor: "pointer",
              }}
            />
          </Tooltip>
        );
      })}
    </Group>
  );
}
