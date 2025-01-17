import { Button, Center, Group, Title } from "@mantine/core";
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
          padding: "1rem",
          fontSize: "2rem",
          fontWeight: "bold",
          marginBottom: "1rem",
        }}
      >
        <Title order={2} align="center" style={{ color: "#4cffb0" }}>
          Level Up Your Coding with
        </Title>
        <Title order={2} align="center">
          Patina’s LeetCode Challenge!
        </Title>
      </div>
      <div
        style={{
          padding: "1rem",
          fontSize: "1.5rem",
          fontWeight: "bold",
        }}
      >
        <Title order={3} align="center">
          We’re on a mission to motivate and inspire Patina Network
        </Title>
        <Title order={3} align="center">
          members to achieve their Software Engineering goals. Wherever
        </Title>
        <Title order={3} align="center">
          you are on your LeetCode journey, we’re here to help your code bloom!
        </Title>
      </div>
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
      <MiniLeaderboardIndex />
      <div
        ref={targetSectionRef}
        style={{
          padding: "2rem",
          marginTop: "2rem",
        }}
      >
        <OurFeatures />
      </div>
    </>
  );
}
