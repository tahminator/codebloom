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
} from "@mantine/core";
import { useMemo } from "react";

interface DuelPlayer {
  id: string;
  leetcodeUsername: string;
  ready: boolean;
}

type LobbyDto = Api<"LobbyDto">;

interface DuelWaitingScreenProps {
  lobby: LobbyDto;
  players?: DuelPlayer[];
  currentUserId: string;
  isHost: boolean;
  onLeave: () => void;
  onStart: () => void;
  onToggleReady: () => void;
}

export function WaitingDuelPage({
  lobby,
  players = [],
  currentUserId,
  isHost,
  onLeave,
  onStart,
  onToggleReady,
}: DuelWaitingScreenProps) {
  const sortedPlayers = useMemo(() => {
    if (players.length <= 2) return players;
    return [...players].sort((a, b) => a.id.localeCompare(b.id));
  }, [players]);

  const player1 = sortedPlayers[0] ?? null;
  const player2 = sortedPlayers[1] ?? null;
  const bothReady = !!player1 && !!player2 && player1.ready && player2.ready;

  return (
    <div
      style={{
        minHeight: "85vh",
        display: "flex",
        justifyContent: "center",
        alignItems: "center",
        backgroundColor: "#0f1115",
      }}
    >
      <Stack maw={1100} w="80%" gap="xl">
        <Stack gap="xs">
          <Text size="md" c="dimmed" fw={600}>
            DUEL LOBBY
          </Text>
          <Text size="2rem" fw={700} c="white" lh={1.3}>
            Waiting for your LeetCode duel to start...
          </Text>
          <Text size="md" c="dimmed">
            Share your lobby code with a friend. Once both players join and are
            ready, the host can start the duel and you'll both receive the same
            LeetCode problem.
          </Text>
        </Stack>
        <Card
          radius="lg"
          withBorder
          padding="2rem"
          style={{
            background: "#0f1115",
            borderColor: "#2E7D32",
            boxShadow: "0 0 15px rgba(0, 255, 120, 0.15)",
          }}
        >
          <Stack gap="xl">
            <Group justify="space-between">
              <Group>
                <Text size="md" c="dimmed" fw={600}>
                  LOBBY CODE
                </Text>
                <CopyButton value={lobby.joinCode}>
                  {({ copied, copy }) => (
                    <Button
                      size="md"
                      variant="subtle"
                      aria-label="Copy lobby code to clipboard"
                      onClick={copy}
                      styles={{
                        root: {
                          backgroundColor: "#0b0d12",
                          borderRadius: "15px",
                          border: "1px solid #2f343f",
                          fontFamily: "monospace",
                        },
                      }}
                    >
                      {lobby.joinCode} {copied ? "✓ Copied" : "Copy"}
                    </Button>
                  )}
                </CopyButton>
              </Group>
              <Badge
                size="lg"
                variant={bothReady ? "filled" : "outline"}
                color={bothReady ? "green.7" : "gray"}
              >
                {bothReady ? "Both players ready!" : "Waiting for opponent..."}
              </Badge>
            </Group>
            <Group justify="space-between" align="center">
              <PlayerCard
                player={player1}
                position="Player 1"
                currentUserId={currentUserId}
                onToggleReady={onToggleReady}
              />
              <Text c="dimmed" fw={600} size="xl">
                VS
              </Text>
              <PlayerCard
                player={player2}
                position="Player 2"
                currentUserId={currentUserId}
                onToggleReady={onToggleReady}
              />
            </Group>
            <Group justify="flex-end" gap="md">
              <Button
                variant="default"
                color="gray"
                size="md"
                onClick={onLeave}
                styles={{
                  root: {
                    backgroundColor: "#151820",
                    borderColor: "#2f343f",
                    fontSize: "1rem",
                    padding: "0.75rem 1.5rem",
                  },
                }}
              >
                Leave Lobby
              </Button>
              {isHost && (
                <Button
                  size="md"
                  onClick={onStart}
                  disabled={!bothReady}
                  styles={{
                    root: {
                      backgroundColor: bothReady ? "#00C851" : "#1a3a1d",
                      color: "#000",
                      fontSize: "1rem",
                      padding: "0.75rem 1.5rem",
                    },
                  }}
                >
                  Start Duel
                </Button>
              )}
            </Group>
          </Stack>
        </Card>
      </Stack>
    </div>
  );
}

function PlayerCard({
  player,
  position,
  currentUserId,
  onToggleReady,
}: {
  player: DuelPlayer | null;
  position: string;
  currentUserId: string;
  onToggleReady: () => void;
}) {
  const isCurrentUser = player?.id === currentUserId;
  const displayName =
    player ?
      isCurrentUser ? `${player.leetcodeUsername} (You)`
      : player.leetcodeUsername
    : `Waiting for opponent...`;

  const status =
    !player ? "Not Connected"
    : player.ready ? "Ready"
    : "Not Ready";
  const statusColor = player?.ready ? "green" : "gray";

  return (
    <Card
      radius="md"
      withBorder
      padding="xl"
      style={{
        flex: 1,
        background: "#101218",
        borderColor: "#2f343f",
      }}
    >
      <Group align="center" gap="md">
        <Avatar
          radius="xl"
          size={64}
          color={player ? "green" : "gray"}
          style={{ border: "2px solid rgba(255,255,255,0.04)" }}
        >
          {player ? player.leetcodeUsername.charAt(0).toUpperCase() : "?"}
        </Avatar>
        <Stack gap={4}>
          <Text size="sm" c="dimmed" fw={500}>
            {position}
          </Text>
          <Text size="lg" fw={600} c="white">
            {displayName}
          </Text>
          <Group align="center" gap="sm" style={{ marginTop: 4 }}>
            <Badge color={statusColor} size="lg">
              {status}
            </Badge>
            {isCurrentUser && (
              <Button
                size="xs"
                variant="light"
                color={player?.ready ? "gray" : "green"}
                onClick={onToggleReady}
              >
                {player?.ready ? "Unready" : "Ready Up"}
              </Button>
            )}
          </Group>
        </Stack>
      </Group>
    </Card>
  );
}
