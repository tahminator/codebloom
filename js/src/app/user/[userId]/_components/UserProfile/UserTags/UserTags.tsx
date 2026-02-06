import UserTagsSkeleton from "@/app/user/[userId]/_components/UserProfile/UserTags/UserTagsSkeleton";
import TagList from "@/components/ui/tags/TagList";
import UserAchievement from "@/components/ui/tags/UserAchievement";
import Toast from "@/components/ui/toast/Toast";
import { useUserProfileQuery } from "@/lib/api/queries/user";
import { Group, Text, Stack } from "@mantine/core";
import { IconAlertTriangle } from "@tabler/icons-react";

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

  const { tags, achievements } = data.payload;
  // const { tags } = data.payload;
  // const achievements = [
  //   {
  //     id: "141sfdshyk0-563",
  //     active: true,
  //     createdAt: "2026-02-07T00:00:00",
  //     deletedAt: null,
  //     description: null,
  //     leaderboard: null,
  //     place: "ONE",
  //     title: "title",
  //     userId: userId,
  //   },
  // ]


  return (
    <Stack gap="md" w="100%">
      <TagList tags={tags} size={40} gap="xs" expanded={true} />
      <UserAchievement achievements={achievements} size={40} gap="xs" />
    </Stack>
  );
}
