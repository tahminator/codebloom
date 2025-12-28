import { useFetchPotdEmbedQuery } from "@/lib/api/queries/embed";
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

export default function PotdEmbedView() {
  const { data, status } = useFetchPotdEmbedQuery();

  if (status === "pending") {
    return (
      <Card withBorder padding={"md"} radius={"md"} miw={"31vw"} mih={"40vh"}>
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
      <Card withBorder padding={"md"} radius={"md"} miw={"31vw"} mih={"40vh"}>
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
      <Card withBorder padding={"md"} radius={"md"} miw={"31vw"} mih={"40vh"}>
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

  const json = data.payload;

  return (
    <Card withBorder padding={"md"} radius={"md"} miw={"31vw"} mih={"40vh"}>
      <Center>
        <Title style={{ textAlign: "center" }} order={3}>
          Problem of the day
        </Title>
      </Center>
      <Center>
        <Title order={6}> POTD resets at 8:00 EDT everyday</Title>
      </Center>
      <Center>
        <Flex
          direction={"column"}
          gap={"sm"}
          align={"center"}
          justify={"center"}
          w={"50%"}
          h={"100%"}
        >
          <Title style={{ textAlign: "center" }} order={4}>
            {json.title}
          </Title>
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
      </Center>
    </Card>
  );
}
