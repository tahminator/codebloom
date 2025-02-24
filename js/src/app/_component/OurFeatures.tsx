import { Card, Title, Text, Center } from "@mantine/core";

export default function OurFeatures() {
  return (
    <>
      <Center>
        <Title order={3} style={{ marginBottom: "2rem" }}>
          Explore Our Features
        </Title>
      </Center>
      <div
        style={{
          display: "flex",
          flexWrap: "wrap",
          gap: "1rem",
          justifyContent: "center",
        }}
      >
        <Card
          shadow="sm"
          padding="lg"
          style={{
            maxWidth: "300px",
            border: "1px solid #ddd",
            borderRadius: "8px",
          }}
        >
          <Center>
            <Title order={4}>Submission Points</Title>
          </Center>
          <Text>
            Earn points based on the specific LeetCode question you answer
            correctly. An easy question earns 100 points. A medium question
            earns 300 points. A hard question earns 600 points.
          </Text>
        </Card>
        <Card
          shadow="sm"
          padding="lg"
          style={{
            maxWidth: "300px",
            border: "1px solid #ddd",
            borderRadius: "8px",
          }}
        >
          <Center>
            <Title order={4}>Daily Challenge</Title>
          </Center>
          <Text>
            Answer the daily LeetCode challenge question that resets at 12:00 AM
            to gain 1.8x points.
          </Text>
        </Card>
        <Card
          shadow="sm"
          padding="lg"
          style={{
            maxWidth: "300px",
            border: "1px solid #ddd",
            borderRadius: "8px",
          }}
        >
          <Center>
            <Title order={4}>Leaderboard System</Title>
          </Center>
          <Text>
            Track your LeetCode progress and improvement by viewing yourself on
            a leaderboard amongst fellow Patina Network members. The top five
            users are displayed above!
          </Text>
        </Card>
      </div>
    </>
  );
}
