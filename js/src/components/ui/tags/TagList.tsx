import { Image, Tooltip, Group } from "@mantine/core";

import { UserTag, School_List } from "./UserTags.tsx";

interface TagListProps {
  tags: UserTag[];
  size: number;
  gap: string | number;
}

export default function TagList({ tags, size = 20, gap = "xs" }: TagListProps) {
  if (!tags || tags.length === 0) {
    return null;
  }

  const filteredTags = tags.filter(userTag => 
    userTag.tag !== "Gwc" && userTag.tag !== "Patina" && userTag.tag in School_List
  );

  if (filteredTags.length === 0) {
    return null;
  }

  return (
    <Group gap={gap} wrap="nowrap">
      {filteredTags.map((userTag) => {
        const school = School_List[userTag.tag as keyof typeof School_List];
        
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
                cursor: "pointer"
              }}
            />
          </Tooltip>
        );
      })}
    </Group>
  );
}