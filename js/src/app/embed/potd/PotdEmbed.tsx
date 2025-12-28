import { Center, Flex } from "@mantine/core";

import PotdEmbedView from "./_components/PotdEmbedView";

export default function PotdEmbed() {
  return (
    <>
      <Center>
          <Flex direction={"column"} flex={1} gap={"md"}>
            <PotdEmbedView />
          </Flex>
      </Center>
    </>
  );
}
