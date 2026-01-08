import ToastWithRedirect from "@/components/ui/toast/ToastWithRedirect";
import { useGetCurrentDuelOrPartyQuery } from "@/lib/api/queries/duels";
import {
  Container,
  Stack,
  Title,
  Paper,
  Flex,
  Divider,
  Text,
  Loader,
} from "@mantine/core";

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
    <Container
      style={{
        width: "50%",
      }}
    >
      <Stack align="center" mb="5vh">
        <Title
          order={2}
          style={{
            color: "white",
            fontSize: "clamp(1.4rem, 3vw, 2.4rem)",
            textAlign: "center",
          }}
        >
          Create or Join a Lobby
        </Title>
        <Text
          c="dimmed"
          ta="center"
          style={{
            fontSize: "clamp(0.8rem, 2.2vw, 1rem)",
            width: "90%",
          }}
        >
          Generate a lobby code to host, or enter one to join a friend.
        </Text>
      </Stack>
      <Paper
        radius="lg"
        style={{
          width: "100%",
          padding: "5%",
          border: "0.2vw solid rgba(0,255,125,0.5)",
        }}
      >
        <Flex direction="row" wrap="wrap" justify="space-between" gap="xs">
          <PartyJoin />
          <Divider color="white" w="100%" />
          <PartyCreate />
        </Flex>
      </Paper>
    </Container>
  );
}
