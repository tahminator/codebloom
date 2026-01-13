import { useLobbyNavigation } from "@/app/duel/_hooks/useDuelNavigation";
import { Flex, Divider, Card, Stack } from "@mantine/core";

import PartyCreate from "./PartyCreate";
import PartyJoin from "./PartyJoin";

export default function PartyEntry() {
  useLobbyNavigation();

  return (
    <Flex w="100%" h="81vh" justify="center" align="center">
      <Card w="100%" maw={400} mx="auto">
        <Stack gap="xs">
          <PartyJoin />
          <Divider w="100%" />
          <PartyCreate />
        </Stack>
      </Card>
    </Flex>
  );
}
