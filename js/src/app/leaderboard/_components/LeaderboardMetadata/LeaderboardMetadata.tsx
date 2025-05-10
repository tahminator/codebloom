import PrettyCounter from "@/components/ui/pretty-counter/PrettyCounter";
import { useCurrentLeaderboardMetadataQuery } from "@/lib/api/queries/leaderboard";
import useCountdown from "@/lib/hooks/useCountdown";
import { Center, Skeleton, Title } from "@mantine/core";
import { useEffect } from "react";

type LeaderboardMetadataOptions = {
  showClock?: boolean;
};

export default function LeaderboardMetadata(
  props: LeaderboardMetadataOptions = {},
) {
  const { showClock = false } = props;

  const { data, status } = useCurrentLeaderboardMetadataQuery();
  const [countdown, reset] = useCountdown(-10);

  useEffect(() => {
    if (status === "success" && data.success && data.data.shouldExpireBy) {
      const shouldExpireByDate = new Date(data.data.shouldExpireBy);
      const expireSeconds = (shouldExpireByDate.getTime() - Date.now()) / 1000;
      console.log(expireSeconds);
      reset(expireSeconds);
    }
  }, [status, data, reset]);

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
        {showClock && (
          <Title
            order={6}
            style={{
              fontSize: "1rem",
              fontWeight: "bold",
              marginBottom: "1rem",
            }}
            className="text-center sm:text-lg"
          >
            <Skeleton visible>Really long tVal name</Skeleton>
          </Title>
        )}
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
    <>
      <Title order={3} ta={"center"} mb={"xs"}>
        {leaderboardData.name}
      </Title>
      <Title order={6} ta={"center"} mb={"xs"}>
        {showClock && leaderboardData.shouldExpireBy && countdown > 0 && (
          <PrettyCounter size={"lg"} time={countdown} />
        )}
      </Title>
    </>
  );
}
