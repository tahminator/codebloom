import PartyWaitingBody from "@/app/duel/[lobbyCode]/_components/party/PartyWaitingBody";
import ToastWithRedirect from "@/components/ui/toast/ToastWithRedirect";
import {
  useDuelOrPartyData,
  useMyDuelOrPartyData,
} from "@/lib/api/queries/duels/sse";
import { Api } from "@/lib/api/types";
import { LobbyStatus } from "@/lib/api/types/autogen/schema";
import { Flex, Loader } from "@mantine/core";

type User = Api<"UserDto">;

export function CurrentDuelBody({
  currentUser,
}: {
  currentUser: Api<"UserDto">;
}) {
  const query = useMyDuelOrPartyData();

  return <DuelBody query={query} currentUser={currentUser} playable />;
}

export function DuelWithIdBody({
  currentUser,
  code,
}: {
  currentUser: User;
  code: string;
}) {
  const query = useDuelOrPartyData(code);

  return <DuelBody query={query} currentUser={currentUser} />;
}

function DuelBody({
  currentUser,
  query,
  playable = false,
}: {
  currentUser: User;
  query: ReturnType<typeof useDuelOrPartyData>;
  playable?: boolean;
}) {
  const { data, status, error } = query;

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

  if (status === "error" || error) {
    return (
      <ToastWithRedirect
        to={"/duel"}
        message={"Hmm, something went wrong."}
      />
    );
  }

  if (!data || !data.success) {
    return (
      <ToastWithRedirect
        to={"/duel"}
        message={"Please join or create a duel first."}
      />
    );
  }

  const duelData = data.payload;

  switch (duelData.lobby.status) {
    case LobbyStatus.CLOSED:
      return <></>;
    case LobbyStatus.AVAILABLE:
      return (
        <PartyWaitingBody
          duelData={duelData}
          currentUser={currentUser}
          playable={playable}
        />
      );
    case LobbyStatus.ACTIVE:
      return <></>;
    case LobbyStatus.COMPLETED:
      return <></>;
  }
}
