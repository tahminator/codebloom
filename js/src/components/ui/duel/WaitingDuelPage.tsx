import { Api } from "@/lib/api/types";
import {
  Button,
  Card,
  Group,
  Stack,
  Text,
  Badge,
  CopyButton,
  Avatar,
  Box,
  Center,
} from "@mantine/core";

interface DuelPlayer {
  id: string;
  leetcodeUsername: string;
  ready: boolean;
}

interface DuelWaitingScreenProps {
  lobby: Api<"LobbyDto">;
  players?: DuelPlayer[];
  currentUserId: string;
  isHost: boolean;
  onLeave: () => void;
  onStart: () => void;
}

export function WaitingDuelPage({
  lobby,
  players = [],
  currentUserId,
  isHost,
  onLeave,
  onStart,
}: DuelWaitingScreenProps) {
  const player1 = players[0] ?? null;
  const player2 = players[1] ?? null;
  const bothPlayersPresent = !!player1 && !!player2;

  return (
    <Box
      style={{
        minHeight: "85vh",
        display: "flex",
        justifyContent: "center",
        alignItems: "center",
      }}
    >
      <Stack maw={480} w="100%" gap="xl">
        <Card
          padding="1.5rem"
          radius="lg"
          style={{
            backgroundColor: "#1a1c23",
            borderColor: "#3f3f46",
            borderWidth: "2px",
          }}
        >
          <Center mb="lg">
            <CopyButton value={lobby.joinCode}>
              {({ copied, copy }) => (
                <Button
                  size="md"
                  variant="filled"
                  onClick={copy}
                  styles={{
                    root: {
                      backgroundColor: "#1a3a52",
                      border: "1px solid #2a4a62",
                      borderRadius: "12px",
                    },
                  }}
                >
                  <Group>
                    <Text fw={700} ff="monospace" c="white" size="lg">
                      {lobby.joinCode}
                    </Text>
                    <Badge
                      color={copied ? "teal" : "green"}
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
            <PlayerCard player={player1} currentUserId={currentUserId} />
            <Center>
              <Text
                c="dimmed"
                fw={700}
                size="3.5rem"
                style={{ fontStyle: "italic" }}
              >
                VS
              </Text>
            </Center>
            <PlayerCard player={player2} currentUserId={currentUserId} />
            <Stack mt="md">
              <Group justify="center">
                <Button variant="light" color="red" size="md" onClick={onLeave}>
                  Leave Party
                </Button>
                {isHost && (
                  <Button
                    size="md"
                    color="green"
                    onClick={onStart}
                    disabled={!bothPlayersPresent}
                  >
                    Start Duel
                  </Button>
                )}
              </Group>
            </Stack>
          </Stack>
        </Card>
      </Stack>
    </Box>
  );
}

function PlayerCard({
  player,
  currentUserId,
}: {
  player: DuelPlayer | null;
  currentUserId: string;
}) {
  const isCurrentUser = player?.id === currentUserId;
  const displayName =
    player ?
      isCurrentUser ? `${player.leetcodeUsername} (You)`
      : player.leetcodeUsername
    : "Waiting for opponent...";

  return (
    <Card
      radius="md"
      style={{
        backgroundColor: "#24262e",
        borderColor: "#3f3f46",
        borderWidth: "2px",
      }}
    >
      <Stack align="center" gap={6}>
        <Avatar
          radius="xl"
          size={48}
          color={player ? "gray" : "dark"}
          styles={{
            root: {
              border: "2px solid #3f3f46",
              backgroundColor: player ? "#374151" : "#1f2937",
            },
          }}
        >
          <Text size="lg" fw={700} c={player ? "white" : "dimmed"}>
            {player ? player.leetcodeUsername.charAt(0).toUpperCase() : "?"}
          </Text>
        </Avatar>
        <Text
          fw={600}
          c={player ? "white" : "dimmed"}
          style={!player ? { fontStyle: "italic" } : undefined}
        >
          {displayName}
        </Text>
      </Stack>
    </Card>
  );
}
