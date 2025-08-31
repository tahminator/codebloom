import DashboardLeaderboardSkeleton from "@/app/dashboard/_components/DashboardLeaderboard/DashboardLeaderboardSkeleton";
import MyCurrentPoints from "@/app/dashboard/_components/DashboardLeaderboard/MyCurrentPoints";
import LeaderboardMetadata from "@/app/leaderboard/_components/LeaderboardMetadata/LeaderboardMetadata";
import { useAuthQuery } from "@/lib/api/queries/auth";
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
  Overlay,
  Text,
  Title,
  Tooltip,
} from "@mantine/core";
import { IconCircleCheckFilled } from "@tabler/icons-react";
import { FaDiscord } from "react-icons/fa";
import { SiLeetcode } from "react-icons/si";
import { Link } from "react-router-dom";

import FilterControl from "./FilterControl";

export default function LeaderboardForDashboard({
  userId,
}: {
  userId: string;
}) {
  // Hack to fix a race condition.
  useFixMyPointsPrefetch({ userId });

  const leaderboardQuery = useCurrentLeaderboardUsersQuery({
    pageSize: 5,
  });
  const userQuery = useAuthQuery();

  if (leaderboardQuery.status === "pending" || userQuery.status === "pending") {
    return <DashboardLeaderboardSkeleton />;
  }

  if (leaderboardQuery.status === "error" || userQuery.status === "error") {
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

  if (!leaderboardQuery.data.success || !userQuery.data.user) {
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
            {leaderboardQuery.data.success && leaderboardQuery.data.message}
            {!userQuery.data.user}
          </Title>
        </Flex>
      </Card>
    );
  }

  const leaderboardData = leaderboardQuery.data.payload;
  const userTags = userQuery.data.user!.tags;

  if (leaderboardData.items.length == 0) {
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

  const inTop5 = !!leaderboardData.items.find((u) => u.id === userId);

  return (
    <Card withBorder padding={"md"} radius={"md"} miw={"31vw"} mih={"63vh"}>
      <Flex
        direction={{ base: "column", md: "row" }}
        justify={"space-between"}
        w={"100%"}
      >
        <LeaderboardMetadata />

        <Button variant={"light"} component={Link} to={"/leaderboard"}>
          View all
        </Button>
      </Flex>
      <FilterControl
        userTags={userTags}
        flags={{
          patina: leaderboardQuery.patina,
          hunter: leaderboardQuery.hunter,
          nyu: leaderboardQuery.nyu,
          baruch: leaderboardQuery.baruch,
          rpi: leaderboardQuery.rpi,
          gwc: leaderboardQuery.gwc,
        }}
        toggles={{
          patina: leaderboardQuery.togglePatina,
          hunter: leaderboardQuery.toggleHunter,
          nyu: leaderboardQuery.toggleNyu,
          baruch: leaderboardQuery.toggleBaruch,
          rpi: leaderboardQuery.toggleRpi,
          gwc: leaderboardQuery.toggleGwc,
        }}
      />
      {!inTop5 && (
        <>
          <MyCurrentPoints userId={userId} />
          <Divider orientation={"horizontal"} />
        </>
      )}
      <Flex direction={"column"} gap={"md"} m={"xs"}>
        {leaderboardQuery.isPlaceholderData && (
          <Overlay
            zIndex={1000}
            backgroundOpacity={0.55}
            blur={10}
            radius={"md"}
          />
        )}
        {leaderboardData.items.map((user, idx) => {
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
