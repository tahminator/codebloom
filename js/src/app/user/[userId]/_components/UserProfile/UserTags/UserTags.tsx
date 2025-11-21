import TagList from "@/components/ui/tags/TagList";
import Toast from "@/components/ui/toast/Toast";
import { useUserProfileQuery } from "@/lib/api/queries/user";
import { components } from "@/lib/api/types/autogen/schema";
import { Group, Text, Box } from "@mantine/core";
import { IconAlertTriangle } from "@tabler/icons-react";

import UserTagsSkeleton from "./UserTagsSkeleton";

type AchievementDto = components["schemas"]["AchievementDto"];

export default function UserTags({ userId }: { userId: string }) {
  const { data, status } = useUserProfileQuery({ userId });

  if (status === "pending") {
    return <UserTagsSkeleton />;
  }

  if (status === "error") {
    return (
      <Toast message="Sorry, something went wrong when trying to fetch the user's tags. Please try again later." />
    );
  }

  if (!data.success) {
    return (
      <Group gap="xs">
        <IconAlertTriangle color="red" />
        <Text fw={500} ff="Inter, sans-serif">
          Error Fetching Tags
        </Text>
      </Group>
    );
  }

  const tags = data.payload.tags;
  const achievements: AchievementDto[] = data.payload.achievements || [];

  return (
    <Box w="100%">
      <TagList tags={tags} achievements={achievements} size={40} gap="xs" />
    </Box>
  );
}
