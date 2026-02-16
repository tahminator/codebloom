import { Card, Center, Flex, Skeleton } from "@mantine/core";

export default function ProblemOfTheDaySkeleton() {
  return (
    <Card withBorder padding={"md"} radius={"md"} miw={"31vw"} mih={"63vh"}>
      <Center mb={"xs"}>
        <Skeleton w={"18rem"} h={"2.5rem"} />
      </Center>
      <Center mb={"md"}>
        <Skeleton w={"13rem"} h={"1.5rem"} />
      </Center>
      <Flex
        direction={"column"}
        gap={"sm"}
        align={"center"}
        justify={"center"}
        w={"100%"}
        h={"100%"}
      >
        <Skeleton w={"60%"} h={"2rem"} />
        <Skeleton w={"9rem"} h={"1.50rem"} radius={"xl"} />
        <Skeleton w={"10rem"} h={"2.25rem"} radius={"md"} />
      </Flex>
    </Card>
  );
}
