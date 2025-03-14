import MiniLeaderboardSkeleton from "@/app/_component/skeletons/MiniLeaderboardSkeleton";
import { useShallowLeaderboardEntriesQuery } from "@/app/hooks";
import LeaderboardCard from "@/components/ui/LeaderboardCard";
import Toast from "@/components/ui/toast/Toast";
import { theme } from "@/lib/theme";
import { Table, Text, Title, Tooltip } from "@mantine/core";
import { IconCircleCheckFilled } from "@tabler/icons-react";
import { FaDiscord } from "react-icons/fa";
import { SiLeetcode } from "react-icons/si";

export default function MiniLeaderboardDesktop() {
  const { data, status } = useShallowLeaderboardEntriesQuery();

  if (status === "pending") {
    return <MiniLeaderboardSkeleton />;
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
            nickname={second.nickname}
            width={"200px"}
            userId={second.id}
          />
        )}
        {first && (
          <LeaderboardCard
            placeString={"First"}
            discordName={first.discordName}
            leetcodeUsername={first.leetcodeUsername}
            totalScore={first.totalScore}
            nickname={first.nickname}
            width={"200px"}
            userId={first.id}
          />
        )}
        {third && (
          <LeaderboardCard
            placeString={"Third"}
            discordName={third.discordName}
            leetcodeUsername={third.leetcodeUsername}
            totalScore={third.totalScore}
            nickname={third.nickname}
            width={"200px"}
            userId={third.id}
          />
        )}
      </div>
      {json.users.length > 3 && (
        <Table horizontalSpacing="xl">
          <Table.Thead>
            <Table.Tr>
              <Table.Th></Table.Th>
              <Table.Th>Name </Table.Th>
              <Table.Th>Total Points</Table.Th>
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
                      {entry.nickname ?
                        <span style={{ fontSize: "18px", lineHeight: "28px" }}>
                          <Tooltip
                            label={
                              "This user is a member of the Patina Discord server."
                            }
                            color={"dark.4"}
                          >
                            <Text>
                              <IconCircleCheckFilled
                                className="inline"
                                color={theme.colors.patina[4]}
                                z={5000000}
                                size={20}
                              />{" "}
                              {entry.nickname}
                            </Text>
                          </Tooltip>
                        </span>
                      : <span style={{ fontSize: "18px", lineHeight: "28px" }}>
                          <FaDiscord style={{ display: "inline" }} />{" "}
                          {entry.discordName}
                        </span>
                      }
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
    </div>
  );
}
