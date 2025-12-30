import DashboardLeaderboardSkeleton from "@/app/dashboard/_components/DashboardLeaderboard/DashboardLeaderboardSkeleton";
import FilterTagsControl from "@/app/dashboard/_components/DashboardLeaderboard/FilterTagControls";
import MyCurrentPoints from "@/app/dashboard/_components/DashboardLeaderboard/MyCurrentPoints";
import LeaderboardMetadata from "@/app/leaderboard/_components/LeaderboardMetadata/LeaderboardMetadata";
import {
  useCurrentLeaderboardUsersQuery,
  useFixMyPointsPrefetch,
} from "@/lib/api/queries/leaderboard";
import { UserTag } from "@/lib/api/types/usertag";
import { ApiUtils } from "@/lib/api/utils";
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
import { useState } from "react";
import { FaDiscord } from "react-icons/fa";
import { SiLeetcode } from "react-icons/si";
import { Link } from "react-router-dom";

export default function LeaderboardForDashboard({
  userId,
  userTags,
}: {
  userId: string;
  userTags: UserTag[];
}) {
  // Hack to fix a race condition.
  useFixMyPointsPrefetch({ userId });

  const { data, status, filters, toggleFilter, isPlaceholderData } =
    useCurrentLeaderboardUsersQuery({
      pageSize: 5,
      tieToUrl: false,
    });
  const [selectedFilterKey, setSelectedFilterKey] = useState<
    string | undefined
  >();

  const onFilterSelected = (value: string | undefined) => {
    setSelectedFilterKey(value);
  };

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

  const leaderboardData = data.payload;

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
  const filteredTags = ApiUtils.filterUnusedTags(userTags);

  return (
    <Card withBorder padding={"md"} radius={"md"} miw={"31vw"} mih={"63vh"}>
      <Flex
        direction={{ base: "column", md: "row" }}
        justify={"space-between"}
        w={"100%"}
      >
        <LeaderboardMetadata />
        <Button
          variant={"light"}
          component={Link}
          to={
            selectedFilterKey ?
              `/leaderboard?${selectedFilterKey}=true`
            : "/leaderboard"
          }
        >
          View all
        </Button>
      </Flex>
      <FilterTagsControl
        tags={filteredTags}
        filters={filters}
        toggleFilter={toggleFilter}
        onFilterSelected={onFilterSelected}
      />
      {!inTop5 && (
        <>
          <MyCurrentPoints userId={userId} />
          <Divider orientation={"horizontal"} />
        </>
      )}
      <Flex direction={"column"} gap={"md"} m={"xs"}>
        {isPlaceholderData && (
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
              <Text>{idx + 1}.</Text>
              <Flex direction={"column"}>
                {user.nickname && (
                  <Tooltip
                    label={
                      "This user is a verified member of the Patina Discord server."
                    }
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
                      {user.nickname}
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
                  {user.discordName}
                </Text>
                {user.leetcodeUsername && (
                  <Text ta="center">
                    <SiLeetcode
                      style={{
                        display: "inline",
                        marginLeft: "4px",
                        marginRight: "4px",
                      }}
                    />
                    {user.leetcodeUsername}
                  </Text>
                )}
              </Flex>
              <Text>{user.totalScore}</Text>
            </Flex>
          );
        })}
      </Flex>
    </Card>
  );
}
