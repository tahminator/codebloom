import { Box, Skeleton } from "@mantine/core";

export default function NewLeaderboardFormSkeleton() {
  return (
    <Box p={"xs"}>
      <Skeleton height={40} width="100%" mb="xl">
        <Box p={"xs"} />
      </Skeleton>
    </Box>
  );
}
