import PotdEmbedView from "@/app/embed/potd/_components/PotdEmbedView";
import { Center, Flex } from "@mantine/core";

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
