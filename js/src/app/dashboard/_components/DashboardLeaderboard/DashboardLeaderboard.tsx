import DashboardLeaderboardSkeleton from "@/app/dashboard/_components/DashboardLeaderboard/DashboardLeaderboardSkeleton";
import MyCurrentPoints from "@/app/dashboard/_components/DashboardLeaderboard/MyCurrentPoints";
import LeaderboardMetadata from "@/app/leaderboard/_components/LeaderboardMetadata/LeaderboardMetadata";
import {
  useCurrentLeaderboardUsersQuery,
  useFixMyPointsPrefetch,
} from "@/lib/api/queries/leaderboard";
import { theme } from "@/lib/theme";
import {
  Button,
  Card,
  Divider,
  Flex,
  Text,
  Title,
  Tooltip,
} from "@mantine/core";
import { IconCircleCheckFilled } from "@tabler/icons-react";
import { FaDiscord } from "react-icons/fa";
import { SiLeetcode } from "react-icons/si";
import { Link } from "react-router-dom";

export default function LeaderboardForDashboard({
  userId,
}: {
  userId: string;
}) {
  // Hack to fix a race condition.
  useFixMyPointsPrefetch({ userId });

  const { data, status } = useCurrentLeaderboardUsersQuery({ pageSize: 5 });

  if (status === "pending") {
    return <DashboardLeaderboardSkeleton />;
  }

  if (status === "error") {
    return (
      <Card withBorder padding={"md"} radius={"md"} miw={"31vw"} mih={"63vh"}>
        <Flex
          direction={"row"}
          justify={"center"}
          align={"center"}
          w={"100%"}
          h={"100%"}
        >
          <Title order={6} ta={"center"}>
            Sorry, something went wrong. Please try again later.
          </Title>
        </Flex>
      </Card>
    );
  }

  if (!data.success) {
    return (
      <Card withBorder padding={"md"} radius={"md"} miw={"31vw"} mih={"63vh"}>
        <Flex
          direction={"row"}
          justify={"center"}
          align={"center"}
          w={"100%"}
          h={"100%"}
        >
          <Title order={6} ta={"center"}>
            {data.message}
          </Title>
        </Flex>
      </Card>
    );
  }

  const leaderboardData = data.data;

  if (leaderboardData.data.length == 0) {
    return (
      <Card withBorder padding={"md"} radius={"md"} miw={"31vw"} mih={"63vh"}>
        <Flex
          direction={"row"}
          justify={"center"}
          align={"center"}
          w={"100%"}
          h={"100%"}
        >
          <Title order={6} ta={"center"}>
            Oops! No users here yet. Crack your first problem and claim this
            space like a champ!
          </Title>
        </Flex>
      </Card>
    );
  }

  const inTop5 = !!leaderboardData.data.find((u) => u.id === userId);

  return (
    <Card withBorder padding={"md"} radius={"md"} miw={"31vw"} mih={"63vh"}>
      <Flex direction={"row"} justify={"space-between"} w={"100%"}>
        <LeaderboardMetadata />
        <Button variant={"light"} component={Link} to={"/leaderboard"}>
          View all
        </Button>
      </Flex>
      {!inTop5 && (
        <>
          <MyCurrentPoints userId={userId} />
          <Divider orientation={"horizontal"} />
        </>
      )}
      <Flex direction={"column"} gap={"md"} m={"xs"}>
        {leaderboardData.data.map((user, idx) => {
          const isMe = user.id === userId;

          const borderColor = (() => {
            if (isMe) {
              return "#283c86";
            }
            if (idx == 0) {
              return "yellow.8";
            }
            if (idx == 1) {
              return "dark.2";
            }
            if (idx == 2) {
              return "orange.9";
            }
            return undefined;
          })();

          return (
            <Flex
              component={Link}
              to={`/user/${user.id}`}
              key={idx}
              direction={"row"}
              justify={"space-between"}
              bg={isMe ? undefined : borderColor}
              style={{
                borderRadius: "4px",
                backgroundImage:
                  isMe ?
                    `linear-gradient(90deg, ${
                      borderColor || "transparent"
                    }, #45a247)`
                  : undefined,
              }}
              w={"100%"}
              p={"xs"}
              className="group transition-all hover:!bg-blue-500 hover:!bg-none"
            >
              <Text className="transition-all group-hover:text-white-500">
                {idx + 1}.
              </Text>
              <Flex direction={"column"}>
                {user.nickname ?
                  <Tooltip
                    label={
                      "This user is a verified member of the Patina Discord server."
                    }
                    color={"dark.4"}
                  >
                    <Text
                      ta="center"
                      className="transition-all group-hover:text-white-500"
                    >
                      <IconCircleCheckFilled
                        style={{
                          display: "inline",
                        }}
                        color={theme.colors.patina[4]}
                        z={5000000}
                        size={20}
                      />{" "}
                      {user.nickname}
                    </Text>
                  </Tooltip>
                : <Text
                    ta="center"
                    className="transition-all group-hover:text-white-500"
                  >
                    <FaDiscord
                      style={{
                        display: "inline",
                        marginLeft: "4px",
                        marginRight: "4px",
                      }}
                    />
                    {user.discordName}
                  </Text>
                }
                <Text
                  ta="center"
                  className="transition-all group-hover:text-white-500"
                >
                  <SiLeetcode
                    style={{
                      display: "inline",
                      marginLeft: "4px",
                      marginRight: "4px",
                    }}
                  />
                  {user.leetcodeUsername}
                </Text>
              </Flex>
              <Text className="transition-all group-hover:text-white-500">
                {user.totalScore}
              </Text>
            </Flex>
          );
        })}
      </Flex>
    </Card>
  );
}
