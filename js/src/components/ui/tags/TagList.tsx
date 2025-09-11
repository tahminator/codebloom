import { UserTag } from "@/lib/api/types/user";
import { tagFF } from "@/lib/ff";
import { Image, Tooltip, Group } from "@mantine/core";

import { TAG_ICONS_LIST } from "./UserTags.tsx";

interface TagListProps {
  tags: UserTag[];
  size: number;
  gap: string | number;
}

export default function TagList({ tags, size = 20, gap = "xs" }: TagListProps) {
  if (!tagFF || !tags || tags.length === 0) {
    return <></>;
  }

  const filteredTags = tags.filter((userTag) => userTag.tag !== "Gwc");

  if (filteredTags.length === 0) {
    return <></>;
  }

  return (
    <Group gap={gap} wrap="nowrap">
      {filteredTags.map((userTag) => {
        const school =
          TAG_ICONS_LIST[userTag.tag as keyof typeof TAG_ICONS_LIST];

        if (!school) {
          return null;
        }

        return (
          <Tooltip
            key={userTag.id}
            label={school.name}
            color="dark.4"
            position="top"
            withArrow
          >
            <Image
              src={school.icon}
              alt={school.alt}
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
