import { Card, Flex, Skeleton } from "@mantine/core";

export default function DashboardLeaderboardSkeleton() {
  return (
    <Card withBorder padding={"md"} radius={"md"} miw={"31vw"} mih={"63vh"}>
      <Flex direction={"row"} justify={"space-between"} w={"100%"} mb="md">
        <Skeleton w={"8rem"} h={"1.75rem"} />
        <Skeleton w={"5.55rem"} h={"2.25rem"} />
      </Flex>
      <Flex justify="center" mb="xl" gap="xxs">
        <Skeleton visible width="50%" height="36px" />
        <Skeleton visible width="50%" height="36px" />
      </Flex>
      <Flex direction={"column"} gap={"md"} m={"xs"}>
        {Array(6)
          .fill(0)
          .map((_, index) => {
            return (
              <Skeleton
                key={index}
                w={"100%"}
                h={"4.5rem"}
                bg="rgba(255, 255, 255, 0.02)"
                radius={8}
              />
            );
          })}
      </Flex>
    </Card>
  );
}
