import { useFullLeaderboardEntriesQuery } from "@/app/leaderboard/hooks";
import Toast from "@/components/ui/toast/Toast";
import { Card, Grid, Loader, Table, Text } from "@mantine/core";
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

  if (!data?.leaderboard || data.leaderboard.length === 0) {
    return <p>Sorry, there is no data available.</p>;
  }

  // TODO - Handle case where there are less than 3 entries.

  const [first, second, third] = data.leaderboard;

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
        Leetcode Leaderboard
      </h1>
      <Grid className="p-4">
        <Grid.Col span={4}>
          <Card
            withBorder
            shadow="sm"
            radius="md"
            className="border-2 border-gray-400"
          >
            <div
              style={{
                display: "flex",
                alignItems: "center",
                gap: "1rem",
                justifyContent: "center",
              }}
            >
              <div>
                <Text ta="center" size="xl">
                  Second
                </Text>
                <Text ta="center" fw={700} size="xl">
                  <FaDiscord className="inline" /> {second.discordName}
                </Text>
                <Text ta="center">
                  <SiLeetcode className="inline" /> {second.leetcodeUsername}
                </Text>
                <Text ta="center" fw={500} size="lg">
                  {second.totalScore} Points
                </Text>
              </div>
            </div>
          </Card>
        </Grid.Col>
        <Grid.Col span={4}>
          <Card
            withBorder
            shadow="sm"
            radius="md"
            className="border-2 border-yellow-300"
          >
            <div
              style={{
                display: "flex",
                alignItems: "center",
                justifyContent: "center",
                gap: "1rem",
              }}
            >
              <div>
                <Text ta="center" size="xl">
                  First
                </Text>
                <Text ta="center" fw={700} size="xl">
                  <FaDiscord className="inline" /> {first.discordName}
                </Text>
                <Text ta="center">
                  <SiLeetcode className="inline" /> {first.leetcodeUsername}
                </Text>
                <Text ta="center" fw={500} size="lg">
                  {first.totalScore} Points
                </Text>
              </div>
            </div>
          </Card>
        </Grid.Col>
        <Grid.Col span={4}>
          <Card
            withBorder
            shadow="sm"
            radius="md"
            className="border-2 border-yellow-800"
          >
            <div
              style={{
                display: "flex",
                alignItems: "center",
                justifyContent: "center",
                gap: "1rem",
              }}
            >
              <div>
                <Text ta="center" size="xl">
                  Third
                </Text>
                <Text ta="center" fw={700} size="xl">
                  <FaDiscord className="inline" /> {third.discordName}
                </Text>
                <Text ta="center">
                  <SiLeetcode className="inline" /> {third.leetcodeUsername}
                </Text>
                <Text ta="center" fw={500} size="lg">
                  {third.totalScore} Points
                </Text>
              </div>
            </div>
          </Card>
        </Grid.Col>
      </Grid>

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
          {data.leaderboard.map((entry, index) => {
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
