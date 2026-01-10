import ToastWithRedirect from "@/components/ui/toast/ToastWithRedirect";
import { useGetCurrentDuelOrPartyQuery } from "@/lib/api/queries/duels";
import { Flex, Divider, Loader, Card, Stack } from "@mantine/core";

import PartyCreate from "./PartyCreate";
import PartyJoin from "./PartyJoin";

export default function PartyEntry() {
  const { data, status } = useGetCurrentDuelOrPartyQuery();

  if (status === "pending") {
    return (
      <Flex
        direction={"column"}
        align={"center"}
        justify={"center"}
        w={"98vw"}
        h={"90vh"}
      >
        <Loader />
      </Flex>
    );
  }

  if (status === "error") {
    return (
      <ToastWithRedirect to={-1} message={"Sorry, something went wrong."} />
    );
  }

  if (data?.payload) {
    return (
      <ToastWithRedirect
        to={`/duel/${data.payload?.code}`}
        message={"You are already in a party/duel."}
      />
    );
  }

  return (
    <Flex w="100%" h="81vh" justify="center" align="center">
      <Card w="100%" maw={400} mx="auto">
        <Stack gap="xs">
          <PartyJoin />
          <Divider color="#656b70" w="100%" size="3px" />
          <PartyCreate />
        </Stack>
      </Card>
    </Flex>
  );
}
