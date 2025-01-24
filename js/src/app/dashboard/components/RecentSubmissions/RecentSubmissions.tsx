import RecentSubmissionsSkeleton from "@/app/dashboard/components/RecentSubmissions/RecentSubmissionsSkeleton";
import { useUserSubmissionsQuery } from "@/app/submission/u/[userId]/hooks";
import { Badge, Box, Button, Card, Flex, Text, Title } from "@mantine/core";
import { FaExternalLinkAlt } from "react-icons/fa";
import { Link } from "react-router-dom";

export default function RecentSubmissions({ userId }: { userId: string }) {
  const { data, status } = useUserSubmissionsQuery({ userId });

  if (status === "pending") {
    return <RecentSubmissionsSkeleton />;
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
            Sorry, something went wrong. Please try again.
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

  const questions = data.data;

  if (!questions.length) {
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
            Oops! No problems solved yet—tackle one to start racking up points!
          </Title>
        </Flex>
      </Card>
    );
  }

  return (
    <Card withBorder padding={"md"} radius={"md"} miw={"31vw"} mih={"63vh"}>
      <Flex direction={"row"} justify={"space-between"}>
        <Title order={4}>Submissions</Title>

        <Button
          variant={"light"}
          component={Link}
          to={`/submission/u/${userId}`}
        >
          View all
        </Button>
      </Flex>

      {questions.map((q, idx) => {
        const badgeColor = (() => {
          if (q.questionDifficulty === "Easy") {
            return undefined;
          }
          if (q.questionDifficulty === "Medium") {
            return "yellow";
          }
          if (q.questionDifficulty === "Hard") {
            return "red";
          }
          return undefined;
        })();

        return (
          <Flex
            key={idx}
            direction={"row"}
            justify={"space-between"}
            style={{
              borderRadius: "4px",
            }}
            p={"0.4rem"}
          >
            <Text>{idx + 1}.</Text>
            <Flex direction={"column"}>
              <Text ta="center">{q.questionTitle}</Text>

              <Box ta={"center"}>
                <Badge ta="center" color={badgeColor}>
                  {q.questionDifficulty}
                </Badge>
              </Box>
              <Text ta={"center"}>{q.pointsAwarded} points</Text>
            </Flex>

            <Button
              component={Link}
              to={`/submission/s/${q.id}`}
              variant={"subtle"}
            >
              <FaExternalLinkAlt />
            </Button>
          </Flex>
        );
      })}
    </Card>
  );
}
