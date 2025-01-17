import { useFullLeaderboardEntriesQuery } from "@/app/leaderboard/hooks";
import Toast from "@/components/ui/toast/Toast";
import { Card, Loader, Table, Text } from "@mantine/core";
import { FaDiscord } from "react-icons/fa";
import { SiLeetcode } from "react-icons/si";

export default function LeaderboardIndex() {
  const { data, status } = useFullLeaderboardEntriesQuery();

  if (status === "pending") {
    return (
      <div className="flex flex-col items-center justify-center w-screen h-screen">
        <Loader />
      </div>
    );
  }

  if (status === "error") {
    return <Toast message="Sorry, something went wrong." />;
  }

  if (!data.json || data.json.users.length === 0) {
    return <p>Sorry, there are no users to display.</p>;
  }

  const { json } = data;
  const first = json.users[0];
  const second = json.users[1];
  const third = json.users[2];

  return (
    <div style={{ padding: "1rem" }}>
      <h1
        style={{
          fontSize: "1.5rem",
          fontWeight: "bold",
          marginBottom: "1rem",
        }}
        className="text-center"
      >
        {json.name}
      </h1>
      <div
        className="flex items-end justify-center gap-4"
        style={{ marginBottom: "2rem" }}
      >
        {second && (
          <Card
            withBorder
            shadow="sm"
            radius="md"
            className="border-2 border-gray-400 flex flex-col items-center justify-center"
            style={{
              height: "180px",
              width: "200px",
            }}
          >
            <Text ta="center" size="xl">
              Second
            </Text>
            <Text ta="center" fw={700} size="lg">
              <FaDiscord className="inline" /> {second.discordName}
            </Text>
            <Text ta="center">
              <SiLeetcode className="inline" /> {second.leetcodeUsername}
            </Text>
            <Text ta="center" fw={500} size="md">
              {second.totalScore} Points
            </Text>
          </Card>
        )}
        {first && (
          <Card
            withBorder
            shadow="sm"
            radius="md"
            className="border-2 border-yellow-300 flex flex-col items-center justify-center"
            style={{
              height: "220px",
              width: "200px",
            }}
          >
            <Text ta="center" size="xl">
              First
            </Text>
            <Text ta="center" fw={700} size="lg">
              <FaDiscord className="inline" /> {first.discordName}
            </Text>
            <Text ta="center">
              <SiLeetcode className="inline" /> {first.leetcodeUsername}
            </Text>
            <Text ta="center" fw={500} size="md">
              {first.totalScore} Points
            </Text>
          </Card>
        )}
        {third && (
          <Card
            withBorder
            shadow="sm"
            radius="md"
            className="border-2 border-yellow-800 flex flex-col items-center justify-center"
            style={{
              height: "155px",
              width: "200px",
            }}
          >
            <Text ta="center" size="xl">
              Third
            </Text>
            <Text ta="center" fw={700} size="lg">
              <FaDiscord className="inline" /> {third.discordName}
            </Text>
            <Text ta="center">
              <SiLeetcode className="inline" /> {third.leetcodeUsername}
            </Text>
            <Text ta="center" fw={500} size="md">
              {third.totalScore} Points
            </Text>
          </Card>
        )}
      </div>

      <Table>
        <Table.Thead>
          <Table.Tr>
            <Table.Th>Rank</Table.Th>
            <Table.Th>Discord Name</Table.Th>
            <Table.Th>LeetCode Username</Table.Th>
            <Table.Th>Total Points</Table.Th>
          </Table.Tr>
        </Table.Thead>
        <Table.Tbody>
          {json.users.map((entry, index) => {
            if ([0, 1, 2].includes(index)) return null;

            return (
              <Table.Tr key={entry.discordName}>
                <Table.Td>{index + 1}</Table.Td>
                <Table.Td>{entry.discordName}</Table.Td>
                <Table.Td>{entry.leetcodeUsername}</Table.Td>
                <Table.Td>{entry.totalScore}</Table.Td>
              </Table.Tr>
            );
          })}
        </Table.Tbody>
      </Table>
    </div>
  );
}
