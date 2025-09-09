import { Image, Tooltip, Group } from "@mantine/core";

import { UserTag, TAG_CONFIG } from "./userTag.tsx";

interface TagListProps {
  tags: UserTag[];
  size?: number;
  gap?: string | number;
}

export default function TagList({ tags, size = 20, gap = "xs" }: TagListProps) {
  if (!tags || tags.length === 0) {
    return null;
  }

  const filteredTags = tags.filter(userTag => 
    userTag.tag !== "Gwc" && userTag.tag !== "Patina" && userTag.tag in TAG_CONFIG
  );

  if (filteredTags.length === 0) {
    return null;
  }

  return (
    <Group gap={gap} wrap="nowrap">
      {filteredTags.map((userTag) => {
        const config = TAG_CONFIG[userTag.tag as keyof typeof TAG_CONFIG];
        
        if (!config) {
          return null;
        }

        return (
          <Tooltip
            key={userTag.id}
            label={config.name}
            color="dark.4"
            position="top"
            withArrow
          >
            <Image
              src={config.icon}
              alt={config.alt}
              style={{ 
                height: size, 
                width: "auto",
                cursor: "pointer"
              }}
            />
          </Tooltip>
        );
      })}
    </Group>
  );
}
