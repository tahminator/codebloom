import PlayerCard from "@/app/duel/[lobbyCode]/_components/PlayerCard";
import {
  useLeavePartyMutation,
  useStartDuelMutation,
} from "@/lib/api/queries/duels";
import { Api } from "@/lib/api/types";
import {
  Stack,
  Card,
  Text,
  Center,
  CopyButton,
  Button,
  Group,
  Badge,
  Flex,
} from "@mantine/core";
import { notifications } from "@mantine/notifications";

type DuelData = Api<"DuelData">;
type User = Api<"UserDto">;

export default function PartyWaitingBody({
  duelData,
  currentUser,
  playable,
}: {
  duelData: DuelData;
  currentUser: User;
  playable: boolean;
}) {
  const { lobby, players } = duelData;
  const { mutate: leavePartyMutate, isPending: isLeavePartyPending } =
    useLeavePartyMutation();
  const { mutate: startDuelMutate, isPending: isStartDuelPending } =
    useStartDuelMutation();

  const playerOne = players[0] ?? null;
  const playerTwo = players[1] ?? null;
  const bothPlayersPresent = !!playerOne && !!playerTwo;
  const canDuelStart = bothPlayersPresent || currentUser.admin;

  const onLeave = () => {
    leavePartyMutate(undefined, {
      onError: () => {
        notifications.show({
          message: "Hmm, something went wrong.",
        });
      },
      onSuccess: (data) => {
        notifications.show({
          message: data.message,
          color: data.success ? undefined : "red",
        });
      },
    });
  };

  const onStart = () => {
    startDuelMutate(undefined, {
      onError: () => {
        notifications.show({
          message: "Hmm, something went wrong.",
        });
      },
      onSuccess: (data) => {
        notifications.show({
          message: data.message,
          color: data.success ? undefined : "red",
        });
      },
    });
  };

  return (
    <Flex mih={"85vh"} justify={"center"} align={"center"}>
      <Stack maw={480} w="100%" gap="xl">
        <Card padding="lg" radius="lg" withBorder>
          <Center mb="lg">
            <CopyButton value={lobby.joinCode}>
              {({ copied, copy }) => (
                <Button
                  size="md"
                  variant="filled"
                  onClick={copy}
                  color={"dark.7"}
                  bdrs={"md"}
                >
                  <Group>
                    <Text fw={700} ff="monospace" c="white" size="lg">
                      {lobby.joinCode}
                    </Text>
                    <Badge
                      color={copied ? "teal" : undefined}
                      variant="filled"
                      size="md"
                    >
                      {copied ? "✓ Copied" : "Copy"}
                    </Badge>
                  </Group>
                </Button>
              )}
            </CopyButton>
          </Center>
          <Stack gap="lg">
            <PlayerCard player={playerOne} currentUserId={currentUser.id} />
            <Center>
              <Text c="dimmed" fw={700} size="3.5rem" fs={"italic"}>
                VS
              </Text>
            </Center>
            <PlayerCard player={playerTwo} currentUserId={currentUser.id} />
            <Stack mt="md">
              {playable && (
                <Group justify="center">
                  <Button
                    color="red"
                    size="md"
                    onClick={onLeave}
                    loading={isLeavePartyPending}
                  >
                    Leave Party
                  </Button>
                  <Button
                    size="md"
                    onClick={onStart}
                    disabled={!canDuelStart}
                    loading={isStartDuelPending}
                  >
                    Start Duel
                  </Button>
                </Group>
              )}
            </Stack>
          </Stack>
        </Card>
      </Stack>
    </Flex>
  );
}
