import { Button, Center, Flex, Group, Title } from "@mantine/core";
import { Link } from "react-router-dom";
import { useRef } from "react";
import OurFeatures from "./OurFeatures";
import MiniLeaderboardIndex from "./MiniLeaderboardIndex";

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
      <div
        style={{
          display: "flex",
          flexDirection: "row",
          height: "92vh",
          width: "100vw",
        }}
      >
        <Flex
          align={"center"}
          justify={"center"}
          h={"100%"}
          direction={"column"}
          w={"50%"}
          visibleFrom="sm"
        >
          <Title order={2} style={{ color: "#4cffb0", alignItems: "center" }}>
            Level Up Your Coding with
          </Title>
          <Title order={2}>Patina’s LeetCode Challenge!</Title>
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
          visibleFrom="sm"
        >
          <MiniLeaderboardIndex />
        </Flex>
      </div>
      <div ref={targetSectionRef}>
        <OurFeatures />
      </div>
    </>
  );
}
