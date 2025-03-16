import RecentSubmissionsSkeleton from "@/app/dashboard/_components/RecentSubmissions/RecentSubmissionsSkeleton";
import { useUserSubmissionsQuery } from "@/app/user/[userId]/_components/UserSubmissions/hooks";
import { Badge, Box, Button, Card, Flex, Text, Title } from "@mantine/core";
import { Link } from "react-router-dom";

export default function RecentSubmissions({ userId }: { userId: string }) {
  const { data, status } = useUserSubmissionsQuery({ userId, pageSize: 5 });

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

  const questions = data.data.data;

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
            component={Link}
            to={`/submission/${q.id}`}
            key={idx}
            direction={"row"}
            justify={"space-between"}
            style={{
              borderRadius: "4px",
            }}
            p={"lg"}
            className="group"
          >
            <Text className="transition-all group-hover:text-blue-500">
              {idx + 1}.
            </Text>
            <Flex direction={"column"}>
              <Text
                ta="center"
                className="transition-all group-hover:text-blue-500"
              >
                {q.questionTitle}
              </Text>

              <Box ta={"center"}>
                <Badge
                  ta="center"
                  color={badgeColor}
                  className="transition-all group-hover:bg-blue-500"
                >
                  {q.questionDifficulty}
                </Badge>
              </Box>
            </Flex>

            <Text
              ta={"center"}
              className="transition-all group-hover:text-blue-500"
            >
              +{q.pointsAwarded} points
            </Text>
          </Flex>
        );
      })}
    </Card>
  );
}
