import {
  Button,
  Center,
  Container,
  Flex,
  Group,
  Stack,
  Title,
} from "@mantine/core";
import { Link } from "react-router-dom";
import { useRef } from "react";
import MiniLeaderboardMobile from "@/app/component/MiniLeaderboardMobile";
import MiniLeaderboard from "@/app/component/MiniLeaderboard";
import OurFeatures from "@/app/component/OurFeatures";

export default function AboutUs() {
  const targetSectionRef = useRef<HTMLDivElement>(null);

  const scrollToSection = () => {
    if (targetSectionRef.current) {
      targetSectionRef.current.scrollIntoView({
        behavior: "smooth",
        block: "start",
      });
    }
  };

  return (
    <>
      <Flex direction={"row"} h={"92vh"} w={"100vw"} visibleFrom="lg">
        <Flex
          align={"center"}
          justify={"center"}
          h={"100%"}
          direction={"column"}
          w={"50%"}
        >
          <Title order={3} style={{ color: "#4cffb0", alignItems: "center" }}>
            Level Up Your Coding with
          </Title>
          <Title order={3}>Patina’s LeetCode Challenge!</Title>
          <div
            style={{
              padding: "1rem",
            }}
          >
            <Center>
              <Group gap="sm">
                <Button variant="white" onClick={scrollToSection}>
                  Learn More
                </Button>
                <Link to="/login">
                  <Button>Get Started</Button>
                </Link>
              </Group>
            </Center>
          </div>
        </Flex>
        <Flex
          align={"center"}
          justify={"center"}
          h={"100%"}
          direction={"column"}
          w={"50%"}
        >
          <MiniLeaderboard />
        </Flex>
      </Flex>
      <Flex h={"80vh"} w={"100vw"} hiddenFrom="lg">
        <Stack align={"center"} justify={"center"} h={"100%"} w={"100vw"}>
          <Title order={5} style={{ color: "#4cffb0", alignItems: "center" }}>
            Level Up Your Coding with
          </Title>
          <Title order={5}>Patina’s LeetCode Challenge!</Title>
          <div
            style={{
              padding: "1rem",
            }}
          >
            <Center>
              <Group gap="sm">
                <Button variant="white" onClick={scrollToSection}>
                  Learn More
                </Button>
                <Link to="/login">
                  <Button>Get Started</Button>
                </Link>
              </Group>
            </Center>
          </div>
        </Stack>
      </Flex>
      <Container hiddenFrom={"lg"}>
        <MiniLeaderboardMobile />
      </Container>
      <div ref={targetSectionRef} style={{ padding: "2rem" }}>
        <OurFeatures />
      </div>
    </>
  );
}
