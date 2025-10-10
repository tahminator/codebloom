import { useMyRecentLeaderboardData } from "@/lib/api/queries/leaderboard";
import { theme } from "@/lib/theme";
import { Flex, Skeleton, Text, Tooltip } from "@mantine/core";
import { IconCircleCheckFilled } from "@tabler/icons-react";
import { FaDiscord } from "react-icons/fa";
import { SiLeetcode } from "react-icons/si";
import { Link } from "react-router-dom";

export default function MyCurrentPoints({ userId }: { userId: string }) {
  const { data, status } = useMyRecentLeaderboardData({ userId });

  if (status === "pending") {
    return (
      <Flex direction={"column"} gap={"md"} m={"xs"}>
        <Skeleton>
          <Flex
            direction={"row"}
            justify={"space-between"}
            style={{
              borderRadius: "4px",
              padding: "var(--mantine-spacing-xs)",
            }}
            p={"xs"}
          >
            <Text>Me.</Text>
            <Flex direction={"column"}>
              <Text ta="center">
                <FaDiscord
                  style={{
                    display: "inline",
                    marginLeft: "4px",
                    marginRight: "4px",
                  }}
                />
                tVal name
              </Text>
              <Text ta="center">
                <SiLeetcode
                  style={{
                    display: "inline",
                    marginLeft: "4px",
                    marginRight: "4px",
                  }}
                />
                tVal name
              </Text>
            </Flex>
            <Text>tVal score</Text>
          </Flex>
        </Skeleton>
      </Flex>
    );
  }

  if (status === "error") {
    return (
      <Text>Sorry, failed to fetch user data from current leaderboard.</Text>
    );
  }

  if (!data.success) {
    return <Text>{data.message}</Text>;
  }

  const userData = data.payload;

  return (
    <Flex
      component={Link}
      direction={"column"}
      gap={"md"}
      m={"xs"}
      to={`/user/${userId}`}
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
        className="transition-all hover:!bg-blue-500"
      >
        <Text>Me</Text>
        <Flex direction={"column"}>
          {userData.nickname && (
            <Tooltip
              label={
                "This user is a verified member of the Patina Discord server."
              }
              color={"dark.4"}
            >
              <Text ta="center">
                <IconCircleCheckFilled
                  style={{
                    display: "inline",
                  }}
                  color={theme.colors.patina[4]}
                  z={5000000}
                  size={20}
                />{" "}
                {userData.nickname}
              </Text>
            </Tooltip>
          )}
          <Text ta="center">
            <FaDiscord
              style={{
                display: "inline",
                marginLeft: "4px",
                marginRight: "4px",
              }}
            />
            {userData.discordName}
          </Text>
          {userData.leetcodeUsername && (
            <Text ta="center">
              <SiLeetcode
                style={{
                  display: "inline",
                  marginLeft: "4px",
                  marginRight: "4px",
                }}
              />
              {userData.leetcodeUsername}
            </Text>
          )}
        </Flex>
        <Text>{userData.totalScore}</Text>
      </Flex>
    </Flex>
  );
}
