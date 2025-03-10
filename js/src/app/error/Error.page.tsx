import { Illustration } from "@/app/error/IIllustration";
import { Box, Button, Flex, Group, Text, Title } from "@mantine/core";
import { Link } from "react-router-dom";

export default function ErrorPage() {
  return (
    <Flex p={"160px"} justify={"center"} h={"100vh"}>
      <Flex pos={"relative"} align={"center"}>
        <Box
          pos={"absolute"}
          inset={0}
          opacity={0.75}
          c={"dark"}
          component={Illustration}
        />
        <Flex
          style={{ textAlign: "center" }}
          pos={"relative"}
          gap={"md"}
          direction={"column"}
        >
          <Title ta={"center"}>Womp Womp.</Title>
          <Flex direction={"column"} wrap={"wrap"} align={"center"}>
            <Text c="dimmed" size="lg" ta="center">
              Unfortunately, this is only a 404 page. You may have mistyped the
              address, or the page has been moved to another URL.
            </Text>
          </Flex>
          <Group justify="center">
            <Button size="md" component={Link} to={"/"}>
              Go back to the home page
            </Button>
          </Group>
        </Flex>
      </Flex>
    </Flex>
  );
}
