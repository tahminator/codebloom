import { Box, Flex, Paper, Skeleton } from "@mantine/core";

export default function MiniLeaderboardMobileSkeleton() {
  return (
    <Box pos={"relative"} p={"xs"}>
      <Flex justify="center" mb="xl" gap="xxs">
        <Skeleton visible maw="180px" w="50%" height="36px" />
        <Skeleton visible maw="180px" w="50%" height="36px" />
      </Flex>
      <div></div>
      <Flex
        direction={{ base: "column", xs: "row" }}
        align={{ base: "center", xs: "flex-end" }}
        justify="center"
        gap="md"
        mb="xl"
      >
        {Array(3)
          .fill(0)
          .map((_, index) => {
            const height = (() => {
              if (index === 0) return "210px";
              if (index === 1) return "185px";
              if (index === 2) return "170px";
            })();

            return (
              <Skeleton visible key={index} width={"300px"} height={height} />
            );
          })}
      </Flex>
      {Array(2)
        .fill(0)
        .map((_, index) => (
          <Paper
            key={index}
            mb="md"
            p="md"
            bg="rgba(255, 255, 255, 0.02)"
            radius={8}
          >
            <Flex justify="space-between" align="center">
              <Flex align="center" gap="md">
                <Skeleton visible width="2rem" height="1.5rem" />
                <Skeleton visible width="120px" height="3rem" />
              </Flex>
              <Skeleton visible width="2rem" height="1.5rem" />
            </Flex>
          </Paper>
        ))}
      <Skeleton visible width="100%" height="40px" mt="md" radius="md" />
    </Box>
  );
}
