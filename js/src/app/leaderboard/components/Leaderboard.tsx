import LeaderboardSkeleton from "@/app/leaderboard/components/LeaderboardSkeleton";
import LeaderboardCard from "@/components/ui/LeaderboardCard";
import Toast from "@/components/ui/toast/Toast";
import { Table, Title } from "@mantine/core";
import { FaDiscord } from "react-icons/fa";
import { SiLeetcode } from "react-icons/si";
import { useFullLeaderboardEntriesQuery } from "../hooks";

export default function LeaderboardIndex() {
  const { data, status } = useFullLeaderboardEntriesQuery();

  if (status === "pending") {
    return <LeaderboardSkeleton />;
  }

  if (status === "error") {
    return <Toast message="Sorry, something went wrong." />;
  }

  if (!data.json) {
    return <p>Sorry, there are no users to display.</p>;
  }
  const json = data.json;

  const [first, second, third] = json.users;

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
        {json.name}
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
            width={"300px"}
          />
        )}
        {first && (
          <LeaderboardCard
            placeString={"First"}
            discordName={first.discordName}
            leetcodeUsername={first.leetcodeUsername}
            totalScore={first.totalScore}
            width={"300px"}
          />
        )}
        {third && (
          <LeaderboardCard
            placeString={"Third"}
            discordName={third.discordName}
            leetcodeUsername={third.leetcodeUsername}
            totalScore={third.totalScore}
            width={"300px"}
          />
        )}
      </div>
      <Table>
        <Table.Thead>
          <Table.Tr>
            <Table.Th>#</Table.Th>
            <Table.Th>Name</Table.Th>
            <Table.Th>Pts</Table.Th>
          </Table.Tr>
        </Table.Thead>
        <Table.Tbody>
          {json.users.map((entry, index) => {
            if ([0, 1, 2].includes(index)) return null;
            return (
              <Table.Tr key={index}>
                <Table.Td>{index + 1}</Table.Td>
                <Table.Td>
                  <div style={{ display: "flex", flexDirection: "column" }}>
                    <span style={{ fontSize: "18px", lineHeight: "28px" }}>
                      <FaDiscord style={{ display: "inline" }} />{" "}
                      {entry.discordName}
                    </span>
                    <span>
                      <SiLeetcode style={{ display: "inline" }} />{" "}
                      {entry.leetcodeUsername}
                    </span>
                  </div>
                </Table.Td>
                <Table.Td>{entry.totalScore}</Table.Td>
              </Table.Tr>
            );
          })}
        </Table.Tbody>
      </Table>
    </div>
  );
}
