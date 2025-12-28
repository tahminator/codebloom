import { Box, Center, Flex } from "@mantine/core";

import PotdEmbedView from "./_components/PotdEmbedView";

export default function PotdEmbed() {
  return (
    <>
      <Center>
        <Box pl={"lg"} pr={"lg"}>
          <Flex direction={"column"} flex={1} gap={"md"}>
            <PotdEmbedView />
          </Flex>
        </Box>
      </Center>
    </>
  );
}
