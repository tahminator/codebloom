import { Box, Card, Flex, Paper, Skeleton } from "@mantine/core";

export default function MiniLeaderboardSkeleton() {
  return (
    <Box pos={"relative"} p={"xs"}>
      <Flex justify="center" mb="xl" gap="xxs">
        <Skeleton visible width="50%" height="36px" />
        <Skeleton visible width="50%" height="36px" />
      </Flex>
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
              <Skeleton visible key={index}>
                <Card
                  withBorder
                  shadow="sm"
                  radius="md"
                  className={`border-2 flex flex-col items-center justify-center`}
                  h={height}
                  w="200px"
                />
              </Skeleton>
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
                <Skeleton visible width="2rem" height="1.75rem" />
                <Skeleton visible width="10rem" height="3rem" />
              </Flex>
              <Skeleton visible width="4rem" height="1.5rem" />
            </Flex>
          </Paper>
        ))}
      <Skeleton visible width="100%" height="30px" mt="md" radius="md" />
    </Box>
  );
}
