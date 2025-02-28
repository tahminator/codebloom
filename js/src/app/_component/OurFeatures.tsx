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
            correctly. We use a formula to calculate your score that takes
            difficulty, acceptance rate, and more into consideration.
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
            Answer the daily LeetCode challenge question that resets at 7:00 PM
            EST to gain a varying bonus multiplier!
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
