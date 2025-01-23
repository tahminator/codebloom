import DashboardLeaderboardSkeleton from "@/app/dashboard/components/DashboardLeaderboard/DashboardLeaderboardSkeleton";
import MyCurrentPoints from "@/app/dashboard/components/DashboardLeaderboard/MyCurrentPoints";
import { useShallowLeaderboardEntriesQuery } from "@/app/hooks";
import { Button, Card, Divider, Flex, Text, Title } from "@mantine/core";
import { FaDiscord } from "react-icons/fa";
import { SiLeetcode } from "react-icons/si";
import { Link } from "react-router-dom";

export default function LeaderboardForDashboard({
  userId,
}: {
  userId: string;
}) {
  const { data, status } = useShallowLeaderboardEntriesQuery();

  if (status === "pending") {
    return <DashboardLeaderboardSkeleton />;
  }

  if (status === "error") {
    return (
      <Card withBorder padding={"md"} radius={"md"} miw={"31vw"} mih={"60vh"}>
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

  if (!data.json || !data.json.users.length) {
    return (
      <Card withBorder padding={"md"} radius={"md"} miw={"31vw"} mih={"60vh"}>
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

  const json = data.json;

  const inTop5 = !!json.users.find((u) => u.id === userId);

  return (
    <Card withBorder padding={"md"} radius={"md"} miw={"31vw"} mih={"60vh"}>
      <Flex direction={"row"} justify={"space-between"} w={"100%"}>
        <Title order={4}>{json.name}</Title>
        <Button variant={"light"} component={Link} to={"/leaderboard"}>
          View all
        </Button>
      </Flex>
      <Flex direction={"column"} gap={"md"} m={"xs"}>
        {json.users.map((user, idx) => {
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
              key={idx}
              direction={"row"}
              justify={"space-between"}
              bg={isMe ? undefined : borderColor}
              style={{
                borderRadius: "4px",
                padding: "var(--mantine-spacing-xs)", // Using Mantine's spacing variable
                background: isMe
                  ? `linear-gradient(90deg, ${
                      borderColor || "transparent"
                    }, #45a247)`
                  : undefined,
              }}
              p={"xs"}
            >
              <Text>{idx + 1}.</Text>
              <Flex direction={"column"}>
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
              </Flex>
              <Text>{user.totalScore}</Text>
            </Flex>
          );
        })}
      </Flex>
      {!inTop5 && (
        <>
          <Divider orientation={"horizontal"} />
          <MyCurrentPoints userId={userId} />
        </>
      )}
    </Card>
  );
}
