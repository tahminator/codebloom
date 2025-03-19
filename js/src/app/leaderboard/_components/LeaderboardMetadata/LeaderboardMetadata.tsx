import { useCurrentLeaderboardMetadataQuery } from "@/lib/api/queries/leaderboard";
import { Center, Skeleton, Title } from "@mantine/core";

export default function LeaderboardMetadata() {
  const { data, status } = useCurrentLeaderboardMetadataQuery();

  if (status === "pending") {
    return (
      <Center>
        <Title
          order={3}
          style={{
            fontSize: "1rem",
            fontWeight: "bold",
            marginBottom: "1rem",
          }}
          className="text-center sm:text-lg"
        >
          <Skeleton visible>Really long tVal name</Skeleton>
        </Title>
      </Center>
    );
  }

  if (status === "error") {
    return (
      <Title
        order={3}
        style={{
          fontSize: "1rem",
          fontWeight: "bold",
          marginBottom: "1rem",
        }}
        className="text-center sm:text-lg"
      >
        Sorry, something went wrong.
      </Title>
    );
  }

  if (!data.success) {
    return (
      <Title
        order={3}
        style={{
          fontSize: "1rem",
          fontWeight: "bold",
          marginBottom: "1rem",
        }}
        className="text-center sm:text-lg"
      >
        {data.message}
      </Title>
    );
  }

  const leaderboardData = data.data;

  return (
    <Title
      order={3}
      style={{
        fontSize: "1rem",
        fontWeight: "bold",
        marginBottom: "1rem",
      }}
      className="text-center sm:text-lg"
    >
      {leaderboardData.name}
    </Title>
  );
}
