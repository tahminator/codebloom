import MyCurrentPoints from "@/app/dashboard/components/LeaderboardForDashboard/MyCurrentPoints";
import { useShallowLeaderboardEntriesQuery } from "@/app/hooks";
import Toast from "@/components/ui/toast/Toast";
import {
  Button,
  Card,
  Divider,
  Flex,
  Loader,
  Text,
  Title,
} from "@mantine/core";
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
    return (
      <div className="flex flex-col items-center justify-center w-screen h-screen">
        <Loader />
      </div>
    );
  }

  if (status === "error") {
    return <Toast message="Sorry, something went wrong." />;
  }

  if (!data.json) {
    return <p>Sorry, there are no users to display.</p>;
  }

  const json = data.json;

  const inTop5 = !!json.users.find((u) => u.id === userId);

  return (
    <Card withBorder padding={"md"} radius={"md"}>
      <Flex direction={"row"} justify={"space-between"}>
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
