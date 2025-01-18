import { useShallowLeaderboardEntriesQuery } from "@/app/hooks";
import LeaderboardCard from "@/components/ui/LeaderboardCard";
import Toast from "@/components/ui/toast/Toast";
import { Loader, Table, Title } from "@mantine/core";
import { FaDiscord } from "react-icons/fa";
import { SiLeetcode } from "react-icons/si";

export default function MiniLeaderboardDesktop() {
  const { data, status } = useShallowLeaderboardEntriesQuery();

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
    return <p>Sorry, there are no users to display.</p>;
  }

  const [first, second, third] = data.leaderboard;

  return (
    <div style={{ padding: "1rem" }}>
      <Title
        order={3}
        style={{
          fontSize: "1rem",
          fontWeight: "bold",
          marginBottom: "1rem",
        }}
        className="text-center sm:text-lg"
      >
        Leetcode Leaderboard
      </Title>
      <div
        className="flex flex-col sm:flex-row items-center sm:items-end justify-center gap-4"
        style={{ marginBottom: "2rem" }}
      >
        {second && (
          <LeaderboardCard
            placeString={"Second"}
            discordName={second.discordName}
            leetcodeUsername={second.leetcodeUsername}
            totalScore={second.totalScore}
          />
        )}
        {first && (
          <LeaderboardCard
            placeString={"First"}
            discordName={first.discordName}
            leetcodeUsername={first.leetcodeUsername}
            totalScore={first.totalScore}
          />
        )}
        {third && (
          <LeaderboardCard
            placeString={"Third"}
            discordName={third.discordName}
            leetcodeUsername={third.leetcodeUsername}
            totalScore={third.totalScore}
          />
        )}
      </div>
      <Table horizontalSpacing="xl">
        <Table.Thead>
          <Table.Tr>
            <Table.Th></Table.Th>
            <Table.Th>
              <FaDiscord />
            </Table.Th>
            <Table.Th>
              <SiLeetcode />
            </Table.Th>
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
