import { Card, Center, Flex, Skeleton } from "@mantine/core";

export default function ProblemOfTheDaySkeleton() {
  return (
    <Card withBorder padding={"md"} radius={"md"} miw={"31vw"} mih={"63vh"}>
      <Center mb={"xs"}>
        <Skeleton w={"18rem"} h={"2.5rem"} data-testid="potd-skeleton-title" />
      </Center>
      <Center mb={"md"}>
        <Skeleton
          w={"13rem"}
          h={"1.5rem"}
          data-testid="potd-skeleton-reset-time"
        />
      </Center>
      <Flex
        direction={"column"}
        gap={"sm"}
        align={"center"}
        justify={"center"}
        w={"100%"}
        h={"100%"}
      >
        <Skeleton
          w={"60%"}
          h={"2rem"}
          data-testid="potd-skeleton-problem-title"
        />
        <Skeleton
          w={"9rem"}
          h={"1.50rem"}
          radius={"xl"}
          data-testid="potd-skeleton-problem-multiplier"
        />
        <Skeleton
          w={"10rem"}
          h={"2.25rem"}
          radius={"md"}
          data-testid="potd-skeleton-problem-link-button"
        />
      </Flex>
    </Card>
  );
}
