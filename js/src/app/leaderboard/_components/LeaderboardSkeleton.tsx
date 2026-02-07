import { Center, Flex, Group, Paper, Skeleton } from "@mantine/core";

/**
 * @todo - Could possibly scan the URL for page number and define different skeletons based off of that.
 */
export default function LeaderboardSkeleton() {
  return (
    <>
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
              if (index === 1) return "210px";
              if (index === 0) return "185px";
              if (index === 2) return "170px";
            })();

            return (
              <Skeleton visible key={index} width={"300px"} height={height} />
            );
          })}
      </Flex>
      <Flex justify="flex-end" mb="md" pt="2rem">
        <Skeleton
          visible
          width={"100px"}
          height={"36px"}
          data-testid="leaderboard-skeleton-name"
        />
      </Flex>
      <Center mb="md">
        <Skeleton visible width="100%" height="36px" />
      </Center>
      {Array(17)
        .fill(0)
        .map((_, index) => (
          <Paper
            key={index}
            mb="md"
            p="md"
            bg="rgba(255, 255, 255, 0.02)"
            radius={8}
          >
            <Group justify="space-between" align="center">
              <Flex align="center" gap="md">
                <Skeleton visible width="2rem" height="2rem" />
                <Skeleton visible w="60%" maw={320} miw={120} height="3rem" />
              </Flex>
              <Skeleton visible w="4rem" height="2rem" />
            </Group>
          </Paper>
        ))}
      <Flex p="xl" justify="center">
        {[...Array(8)].map((_, i) => (
          <Skeleton key={i} height={30} width={30} mr={8} />
        ))}
      </Flex>
    </>
  );
}
