import { Box, Skeleton } from "@mantine/core";

export default function IncompleteQuestionListSkeleton() {
  return (
    <Box p={"xs"}>
      <Skeleton height={40} width="100%" mb="xl" />
      <Box p={"md"}>
        <Skeleton height={36} width="100%" />
      </Box>
      {[...Array(5)].map((_, i) => (
        <Skeleton key={i} height={60} mb="sm" radius="md">
          <Box p={"md"} />
        </Skeleton>
      ))}
      <Box p="xl" display="flex">
        {[...Array(6)].map((_, i) => (
          <Skeleton key={i} height={36} width={36} mr={8} />
        ))}
      </Box>
    </Box>
  );
}
