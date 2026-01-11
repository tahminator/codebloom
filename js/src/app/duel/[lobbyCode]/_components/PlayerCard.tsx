import { Api } from "@/lib/api/types";
import { Card, Stack, Avatar, Text } from "@mantine/core";

const DEFAULT_AVATAR_URL =
  "https://assets.leetcode.com/users/default_avatar.jpg";

type Player = Api<"UserDto">;

export default function PlayerCard({
  player,
  currentUserId,
}: {
  player: Player | null;
  currentUserId: string;
}) {
  const isCurrentUser = player?.id === currentUserId;

  const displayName = (() => {
    if (!player) {
      return "Waiting for opponent...";
    }

    if (isCurrentUser) {
      return `${player.leetcodeUsername} (You)`;
    }

    return player.leetcodeUsername;
  })();

  const initial = (() => {
    if (!player) {
      return "?";
    }

    if (player.nickname) {
      return player.nickname[0].toUpperCase();
    }

    return player.discordName[0].toUpperCase();
  })();

  const src = (() => {
    if (!player) {
      return undefined;
    }

    if (!player.profileUrl) {
      return undefined;
    }

    if (player.profileUrl === DEFAULT_AVATAR_URL) {
      return undefined;
    }

    return player.profileUrl;
  })();

  return (
    <Card radius="md" withBorder>
      <Stack align="center" gap={6}>
        <Avatar radius="xl" size={48} src={src}>
          {initial}
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
