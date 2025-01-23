import { useFetchPotdQuery } from "@/app/dashboard/components/ProblemOfTheDay/hooks";
import {
  Badge,
  Button,
  Card,
  Center,
  Flex,
  Loader,
  Title,
} from "@mantine/core";
import { Link } from "react-router-dom";

export default function ProblemOfTheDay() {
  const { data, status } = useFetchPotdQuery();

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
            Sorry, something went wrong. Please try again later.
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

  const json = data.data;

  return (
    <Card withBorder padding={"md"} radius={"md"} miw={"31vw"} mih={"60vh"}>
      <Center>
        <Title order={3}>Problem of the day</Title>
      </Center>
      <Flex
        direction={"column"}
        gap={"sm"}
        align={"center"}
        justify={"center"}
        w={"100%"}
        h={"100%"}
      >
        <Title order={4}>{json.title}</Title>
        <Badge
          variant={"gradient"}
          gradient={{ from: "blue.8", to: "green.8" }}
        >
          {json.multiplier}x multiplier
        </Badge>
        <Button
          component={Link}
          to={`https://leetcode.com/problems/${json.slug}`}
          variant={"outline"}
          reloadDocument
          target="_blank"
          rel="noopener noreferrer"
        >
          Go to question
        </Button>
      </Flex>
    </Card>
  );
}
