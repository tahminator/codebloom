import { Flex, Skeleton, Divider, Text } from "@mantine/core";

export default function UserTagsSkeleton() {
  return (
    <>
      <Flex direction={"column"} align={"center"} gap={"sm"} w={"100%"}>
        <Divider w={"70%"} />
        <Text size={"sm"}>
          <Skeleton>Leaderboard</Skeleton>
        </Text>
        <Flex wrap="wrap" gap="sm">
          <Skeleton height={40} width={40} />
          <Skeleton height={40} width={40} />
        </Flex>
        <Divider w={"70%"} />
        <Text size={"sm"}>
          <Skeleton>Achievements</Skeleton>
        </Text>
        <Flex wrap="wrap" gap="sm">
          <Skeleton height={40} width={40} />
          <Skeleton height={40} width={40} />
          <Skeleton height={40} width={40} />
        </Flex>
      </Flex>
    </>
  );
}
