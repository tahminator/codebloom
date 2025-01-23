import { useRecentSubmissionsQuery } from "@/app/dashboard/components/RecentSubmissions/hooks";
import {
  Badge,
  Box,
  Button,
  Card,
  Flex,
  Loader,
  Text,
  Title,
} from "@mantine/core";
import { FaExternalLinkAlt } from "react-icons/fa";
import { Link } from "react-router-dom";

export default function RecentSubmissions() {
  const { data, status } = useRecentSubmissionsQuery({ start: 0, end: 5 });

  if (status === "pending") {
    return (
      <Card withBorder padding={"md"} radius={"md"} miw={"31vw"} mih={"60vh"}>
        <Flex
          direction={"row"}
          justify={"center"}
          align={"center"}
          w={"100%"}
          h={"100%"}
        >
          <Loader />
        </Flex>
      </Card>
    );
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
            Sorry, something went wrong. Please try again.
          </Title>
        </Flex>
      </Card>
    );
  }

  if (!data.success) {
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
            {data.message}
          </Title>
        </Flex>
      </Card>
    );
  }

  const questions = data.data;

  if (!questions.length) {
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
            Oops! No problems solved yet—tackle one to start racking up points!
          </Title>
        </Flex>
      </Card>
    );
  }

  return (
    <Card withBorder padding={"md"} radius={"md"} miw={"31vw"} mih={"60vh"}>
      <Flex direction={"row"} justify={"space-between"}>
        <Title order={4}>Submissions</Title>
        <Button variant={"light"} component={Link} to={"/submissions"}>
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
            p={"xs"}
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
              to={q.questionLink}
              reloadDocument
              variant={"subtle"}
              target="_blank"
              rel="noopener noreferrer"
            >
              <FaExternalLinkAlt />
            </Button>
          </Flex>
        );
      })}
    </Card>
  );
}
