import MiniLeaderboardMobileSkeleton from "@/app/_component/skeletons/MiniLeaderboardMobileSkeleton";
import LeaderboardMetadata from "@/app/leaderboard/_components/LeaderboardMetadata/LeaderboardMetadata";
import LeaderboardCard from "@/components/ui/LeaderboardCard";
import Toast from "@/components/ui/toast/Toast";
import { useCurrentLeaderboardUsersQuery } from "@/lib/api/queries/leaderboard";
import { Button, SegmentedControl, Table } from "@mantine/core";
import { FaDiscord } from "react-icons/fa";
import { SiLeetcode } from "react-icons/si";
import { Link } from "react-router-dom";

export default function MiniLeaderboardMobile() {
  const { data, status, patina, togglePatina } =
    useCurrentLeaderboardUsersQuery({ pageSize: 5 });

  if (status === "pending") {
    return <MiniLeaderboardMobileSkeleton />;
  }

  if (status === "error") {
    return <Toast message="Sorry, something went wrong." />;
  }

  if (!data.success) {
    return <Toast message={data.message} />;
  }

  const leaderboardData = data.data;

  if (leaderboardData.data.length == 0) {
    return <p>Sorry, there are no users to display.</p>;
  }

  const [first, second, third] = leaderboardData.data;

  return (
    <>
      <LeaderboardMetadata />
      <SegmentedControl
        value={patina ? "patina" : "all"}
        w={"100%"}
        variant={"light"}
        data={[
          { label: "All", value: "all" },
          { label: "Patina", value: "patina" },
        ]}
        onChange={togglePatina}
      />
      <div
        className="flex flex-col sm:flex-row items-center sm:items-end justify-center gap-4"
        style={{ marginBottom: "2rem", marginTop: "1rem" }}
      >
        {first && (
          <LeaderboardCard
            placeString={"First"}
            discordName={first.discordName}
            leetcodeUsername={first.leetcodeUsername}
            nickname={first.nickname}
            totalScore={first.totalScore}
            width={"300px"}
            userId={first.id}
          />
        )}
        {second && (
          <LeaderboardCard
            placeString={"Second"}
            discordName={second.discordName}
            leetcodeUsername={second.leetcodeUsername}
            nickname={second.nickname}
            totalScore={second.totalScore}
            width={"300px"}
            userId={second.id}
          />
        )}
        {third && (
          <LeaderboardCard
            placeString={"Third"}
            discordName={third.discordName}
            leetcodeUsername={third.leetcodeUsername}
            nickname={third.nickname}
            totalScore={third.totalScore}
            width={"300px"}
            userId={third.id}
          />
        )}
      </div>
      {leaderboardData.data.length > 3 && (
        <Table>
          <Table.Thead>
            <Table.Tr>
              <Table.Th></Table.Th>
              <Table.Th>Name</Table.Th>
              <Table.Th>Total Points</Table.Th>
            </Table.Tr>
          </Table.Thead>
          <Table.Tbody>
            {leaderboardData.data.map((entry, index) => {
              if ([0, 1, 2].includes(index)) return null;
              return (
                <Table.Tr key={entry.discordName}>
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
      )}
      <Button variant={"light"} w={"100%"} component={Link} to={"/leaderboard"}>
        View all
      </Button>
    </>
  );
}
