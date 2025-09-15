import MiniLeaderboard from "@/app/_component/MiniLeaderboard";
import MiniLeaderboardMobile from "@/app/_component/MiniLeaderboardMobile";
import OurFeatures from "@/app/_component/OurFeatures";
import LeaderboardMetadata from "@/app/leaderboard/_components/LeaderboardMetadata/LeaderboardMetadata";
import {
  Badge,
  Box,
  Button,
  Center,
  Container,
  Flex,
  Group,
  Stack,
  Title,
} from "@mantine/core";
import { useRef } from "react";
import { Link } from "react-router-dom";

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
      <Flex direction={"row"} h={"92vh"} w={"98vw"} visibleFrom="lg">
        <Flex
          align={"center"}
          justify={"center"}
          h={"100%"}
          direction={"column"}
          w={"50%"}
        >
          <Badge
            component={Link}
            to={"/settings"}
            variant={"gradient"}
            gradient={{ from: "green", to: "cyan", deg: 90 }}
            size={"xl"}
            m={"md"}
            style={{
              cursor: "pointer",
            }}
          >
            Join your university leaderboard! Register here
          </Badge>
          <Title order={3} style={{ color: "#4cffb0", alignItems: "center" }}>
            Staging test, wow so cool!
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
          <Box pos={"relative"} p={"xs"}>
            <LeaderboardMetadata showClock />
            <MiniLeaderboard />
          </Box>
        </Flex>
      </Flex>
      <Flex h={"80vh"} w={"100vw"} hiddenFrom="lg">
        <Stack align={"center"} justify={"center"} h={"100%"} w={"100vw"}>
          <Badge
            component={Link}
            to={"/settings"}
            variant={"gradient"}
            gradient={{ from: "green", to: "cyan", deg: 90 }}
            size={"sm"}
            m={"md"}
            style={{
              cursor: "pointer",
            }}
          >
            Join your university leaderboard! Register here
          </Badge>
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
        <LeaderboardMetadata showClock />
        <MiniLeaderboardMobile />
      </Container>
      <div ref={targetSectionRef} style={{ padding: "2rem" }}>
        <OurFeatures />
      </div>
    </>
  );
}
