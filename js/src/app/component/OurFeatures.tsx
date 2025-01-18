import { Card, Title, Text } from "@mantine/core";

export default function OurFeatures() {
  return (
    <>
      <Title order={3} align="center" style={{ marginBottom: "2rem" }}>
        Explore Our Features
      </Title>
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
            <Title order={4}>Code Submission</Title>
          </Center>
          <Text>
            Earn points based on the specific LeetCode question you answer
            correctly. An easy question earns 60 points. A medium question earns
            80 points. A hard questions earns 110 points.
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
            to gain 1.3x points.
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
            <Title order={4}>LeetCode Leaderboard</Title>
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
