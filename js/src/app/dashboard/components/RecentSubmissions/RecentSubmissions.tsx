import { useRecentSubmissionsQuery } from "@/app/dashboard/components/RecentSubmissions/hooks";
import {
  Badge,
  Box,
  Button,
  Card,
  Center,
  Flex,
  Text,
  Title,
} from "@mantine/core";
import { FaExternalLinkAlt } from "react-icons/fa";
import { Link } from "react-router-dom";

export default function RecentSubmissions() {
  const { data, status } = useRecentSubmissionsQuery({ start: 0, end: 5 });

  if (status === "pending") {
    return (
      <Card withBorder padding={"md"} radius={"md"}>
        <Center>
          <Title order={4}>Loading...</Title>
        </Center>
      </Card>
    );
  }

  if (status === "error") {
    return (
      <Card withBorder padding={"md"} radius={"md"}>
        <Center>
          <Title order={4}>Error</Title>
        </Center>
        <Center>
          <Text>Sorry, something went wrong.</Text>
        </Center>
      </Card>
    );
  }

  if (!data.success) {
    return (
      <Card withBorder padding={"md"} radius={"md"}>
        <Center>
          <Title order={4}>Error</Title>
        </Center>
        <Center>
          <Text>{data.message}</Text>
        </Center>
      </Card>
    );
  }

  const questions = data.data;

  return (
    <Card withBorder padding={"md"} radius={"md"}>
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
