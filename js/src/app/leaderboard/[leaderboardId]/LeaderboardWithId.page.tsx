import LeaderboardMetadataWithId from "@/app/leaderboard/[leaderboardId]/_components/LeaderboardMetadataWithId";
import LeaderboardWithId from "@/app/leaderboard/[leaderboardId]/_components/LeaderboardWithId";
import { Box } from "@mantine/core";
import { useParams } from "react-router";

export default function LeaderboardWithIdPage() {
  const { leaderboardId } = useParams();

  // This shouldn't happen, since no ID means `/leaderboard`,
  // which just links to the current leaderboard.
  if (!leaderboardId) {
    return <></>;
  }

  return (
    <>
      <Box p={"lg"}>
        <LeaderboardMetadataWithId leaderboardId={leaderboardId} />
        <LeaderboardWithId leaderboardId={leaderboardId} />
      </Box>
    </>
  );
}
