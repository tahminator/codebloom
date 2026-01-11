import { Box, Skeleton } from "@mantine/core";

export default function IncompleteQuestionListSkeleton() {
  return (
    <Box p={"xs"}>
      {[...Array(8)].map((_, i) => (
        <Skeleton key={i} height={90} mb="sm" radius="md">
          <Box p={"md"} />
        </Skeleton>
      ))}
    </Box>
  );
}
