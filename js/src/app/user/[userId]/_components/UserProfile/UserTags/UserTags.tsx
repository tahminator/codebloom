import TagList from "@/components/ui/tags/TagList";
import Toast from "@/components/ui/toast/Toast";
import { useUserProfileQuery } from "@/lib/api/queries/user";
import { ApiUtils } from "@/lib/api/utils";
import { Group, Text } from "@mantine/core";
import { IconAlertTriangle } from "@tabler/icons-react";

import UserTagsSkeleton from "./UserTagsSkeleton";

export default function UserTags({ userId }: { userId?: string }) {
  const { data, status } = useUserProfileQuery({ userId });

  if (status === "pending") {
    return (
      <>
        <UserTagsSkeleton />
      </>
    );
  }

  if (status === "error") {
    return (
      <Toast message="Sorry, something went wrong when trying to fetch the user's tags. Please try again later." />
    );
  }

  if (!data.success) {
    return (
      <>
        {
          <Group gap="xs">
            <IconAlertTriangle color="red"/>
            <Text
              fw={500}
              ff="Inter, sans-serif"
            >
              Error Fetching Tags
            </Text>
          </Group>
        }
      </>
    );
  }

  const tags = data.payload.tags;

  const filteredTags = ApiUtils.filterUnusedTags(tags);

  return (
    <>
      <TagList tags={filteredTags} size={40} gap="xs" />
    </>
  );
}
