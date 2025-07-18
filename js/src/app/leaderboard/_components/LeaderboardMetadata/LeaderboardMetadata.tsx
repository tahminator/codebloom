import PrettyCounter from "@/components/ui/pretty-counter/PrettyCounter";
import { useCurrentLeaderboardMetadataQuery } from "@/lib/api/queries/leaderboard";
import useCountdown from "@/lib/hooks/useCountdown";
import { Box, Button, Center, Skeleton, Title } from "@mantine/core";
import { useEffect } from "react";
import { Link } from "react-router-dom";

type LeaderboardMetadataOptions = {
  showClock?: boolean;
  showAllLeaderboardButton?: boolean;
};

export default function LeaderboardMetadata(
  props: LeaderboardMetadataOptions = {},
) {
  const { showClock = false, showAllLeaderboardButton = false } = props;

  const { data, status } = useCurrentLeaderboardMetadataQuery();
  const [countdown, reset] = useCountdown(-10);

  useEffect(() => {
    if (status === "success" && data.success && data.payload.shouldExpireBy) {
      const shouldExpireByDate = new Date(data.payload.shouldExpireBy);
      const expireSeconds = (shouldExpireByDate.getTime() - Date.now()) / 1000;
      console.log(expireSeconds);
      reset(expireSeconds);
    }
  }, [status, data, reset]);

  if (status === "pending") {
    return (
      <>
        <Center>
          <Title order={3} mb={"sm"} ta={"center"}>
            <Skeleton visible>Really long tNameV</Skeleton>
          </Title>
        </Center>
        <Center>
          {showClock && (
            <Title order={6} mb={"sm"} ta={"center"}>
              <Skeleton visible>Long tTime value</Skeleton>
            </Title>
          )}
        </Center>
      </>
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

  const leaderboardData = data.payload;

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
      <Box ta={"center"} mb={"md"}>
        {showAllLeaderboardButton && (
          <Button component={Link} to={"/leaderboard/all"}>
            View All Leaderboards
          </Button>
        )}
      </Box>
    </>
  );
}
