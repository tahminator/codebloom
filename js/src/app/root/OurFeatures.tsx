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
          <Title order={4} align="center">
            Code Submission
          </Title>
          <Text>
            Submit your LeetCode solutions and earn points based on your
            corectness, which is determined by question difficulty and topic.
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
          <Title order={4} align="center">
            LeetCode Leaderboard
          </Title>
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
