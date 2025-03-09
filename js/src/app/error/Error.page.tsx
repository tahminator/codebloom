import { Button, Container, Flex, Group, Text, Title } from "@mantine/core";
import { Link } from "react-router-dom";

import classes from "./ErrorPage.module.css";
import { Illustration } from "./Illustration";

export default function ErrorPage() {
  return (
    <Container className={classes.root}>
      <div className={classes.inner}>
        <Illustration className={classes.image} />
        <div className={classes.content}>
          <Flex gap={"md"} direction={"column"}>
            <Title className={classes.title}>Womp Womp.</Title>
            <div className="flex flex-col flex-wrap items-center">
              <Text
                c="dimmed"
                size="lg"
                ta="center"
                className={classes.description}
              >
                Unfortunately, this is only a 404 page. You may have mistyped
                the address, or the page has been moved to another URL.
              </Text>
            </div>
            <Group justify="center">
              <Button size="md" component={Link} to={"/"}>
                Go back to the home page
              </Button>
            </Group>
          </Flex>
        </div>
      </div>
    </Container>
  );
}
