import MiniLeaderboardMobileSkeleton from "@/app/_component/skeletons/MiniLeaderboardMobileSkeleton";
import LeaderboardCard from "@/components/ui/LeaderboardCard";
import Toast from "@/components/ui/toast/Toast";
import { useCurrentLeaderboardUsersQuery } from "@/lib/api/queries/leaderboard";
import { UserTagTag } from "@/lib/api/types/autogen/schema";
import getOrdinal from "@/lib/helper/ordinal";
import { theme } from "@/lib/theme";
import {
  Button,
  Overlay,
  SegmentedControl,
  Table,
  Text,
  Tooltip,
} from "@mantine/core";
import { IconCircleCheckFilled } from "@tabler/icons-react";
import { FaDiscord } from "react-icons/fa";
import { SiLeetcode } from "react-icons/si";
import { Link } from "react-router-dom";

export default function MiniLeaderboardMobile() {
  const { data, status, filters, toggleFilter, isPlaceholderData } =
    useCurrentLeaderboardUsersQuery({ pageSize: 5, tieToUrl: false });

  if (status === "pending") {
    return <MiniLeaderboardMobileSkeleton />;
  }

  if (status === "error") {
    return <Toast message="Sorry, something went wrong." />;
  }

  if (!data.success) {
    return <Toast message={data.message} />;
  }

  const leaderboardData = data.payload;

  if (leaderboardData.items.length == 0) {
    return <p>Sorry, there are no users to display.</p>;
  }

  const [first, second, third] = leaderboardData.items;

  return (
    <>
      <SegmentedControl
        value={filters.Patina ? "patina" : "all"}
        w={"100%"}
        variant={"light"}
        data={[
          { label: "All", value: "all" },
          { label: "Patina", value: "patina" },
        ]}
        onChange={() => toggleFilter(UserTagTag.Patina)}
      />
      <div style={{ position: "relative" }}>
        {isPlaceholderData && (
          <Overlay
            zIndex={1000}
            backgroundOpacity={0.55}
            blur={10}
            radius={"md"}
          />
        )}
        <div
          className="flex flex-col sm:flex-row items-center sm:items-end justify-center gap-4"
          style={{ marginBottom: "2rem", marginTop: "1rem" }}
        >
          {first && (
            <LeaderboardCard
              placeString={getOrdinal(first.index)}
              sizeOrder={1}
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
              placeString={getOrdinal(second.index)}
              sizeOrder={2}
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
              placeString={getOrdinal(third.index)}
              sizeOrder={3}
              discordName={third.discordName}
              leetcodeUsername={third.leetcodeUsername}
              nickname={third.nickname}
              totalScore={third.totalScore}
              width={"300px"}
              userId={third.id}
            />
          )}
        </div>
        {leaderboardData.items.length > 3 && (
          <Table>
            <Table.Thead>
              <Table.Tr>
                <Table.Th></Table.Th>
                <Table.Th>Name</Table.Th>
                <Table.Th>Total Points</Table.Th>
              </Table.Tr>
            </Table.Thead>
            <Table.Tbody>
              {leaderboardData.items.map((entry, index) => {
                if ([0, 1, 2].includes(index)) return null;
                return (
                  <Table.Tr key={entry.discordName}>
                    <Table.Td>{index + 1}</Table.Td>
                    <Table.Td>
                      <div style={{ display: "flex", flexDirection: "column" }}>
                        {entry.nickname && (
                          <span
                            style={{ fontSize: "18px", lineHeight: "28px" }}
                          >
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
                        )}
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
      </div>
      <Button
        variant={"light"}
        w={"100%"}
        component={Link}
        to={`/leaderboard?patina=${filters.Patina}`}
      >
        View all
      </Button>
    </>
  );
}
