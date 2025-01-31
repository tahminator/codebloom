import { useMyRecentLeaderboardData } from "@/app/dashboard/components/DashboardLeaderboard/hooks";
import { Flex, Loader, Text } from "@mantine/core";
import { FaDiscord } from "react-icons/fa";
import { SiLeetcode } from "react-icons/si";
import { Link } from "react-router-dom";

export default function MyCurrentPoints({ userId }: { userId: string }) {
  const { data, status } = useMyRecentLeaderboardData({ userId });

  if (status === "pending") {
    <Loader />;
  }

  if (status === "error") {
    return (
      <Text>Sorry, failed to fetch user data from current leaderboard.</Text>
    );
  }

  return (
    <Flex
      component={Link}
      direction={"column"}
      gap={"md"}
      m={"xs"}
      to={`/submission/u/${userId}`}
      className="group transition-all"
    >
      <Flex
        direction={"row"}
        justify={"space-between"}
        bg={"dark.4"}
        p={"xs"}
        style={{
          borderRadius: "4px",
        }}
        className="transition-all group-hover:!bg-blue-500"
      >
        <Text>Me</Text>
        <Flex direction={"column"}>
          <Text ta="center">
            <FaDiscord
              style={{
                display: "inline",
                marginLeft: "4px",
                marginRight: "4px",
              }}
            />
            {data?.user?.discordName}
          </Text>
          <Text ta="center">
            <SiLeetcode
              style={{
                display: "inline",
                marginLeft: "4px",
                marginRight: "4px",
              }}
            />
            {data?.user?.leetcodeUsername}
          </Text>
        </Flex>
        <Text>{data?.user?.totalScore}</Text>
      </Flex>
    </Flex>
  );
}
