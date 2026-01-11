import { Flex, Skeleton } from "@mantine/core";

export default function UserTagsSkeleton() {
  return (
    <>
      <Flex wrap="wrap" gap="sm">
        <Skeleton height={40} width={40} />
        <Skeleton height={40} width={40} />
      </Flex>
      <Flex wrap="wrap" gap="sm">
        <Skeleton height={40} width={40} />
        <Skeleton height={40} width={40} />
        <Skeleton height={40} width={40} />
      </Flex>
    </>
  );
}
