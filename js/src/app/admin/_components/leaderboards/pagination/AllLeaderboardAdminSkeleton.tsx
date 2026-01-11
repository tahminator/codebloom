import { Box, Flex, Skeleton } from "@mantine/core";

export default function AllLeaderboardAdminSkeleton() {
  return (
    <Box p={"xs"}>
      <Flex justify="center" mb="xl" gap="xs">
        <Skeleton visible width="75%" height="36px" />
        <Skeleton visible width="25%" height="36px" />
      </Flex>
      <Skeleton height={50} mb="sm" radius="md" />
      {[...Array(5)].map((_, i) => (
        <Skeleton key={i} height={60} mb="sm" radius="md">
          <Box p={"md"} />
        </Skeleton>
      ))}
      <Flex p="xl" justify="center">
        {[...Array(8)].map((_, i) => (
          <Skeleton key={i} height={30} width={30} mr={8} />
        ))}
      </Flex>
    </Box>
  );
}
