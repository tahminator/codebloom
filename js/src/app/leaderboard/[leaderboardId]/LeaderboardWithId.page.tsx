import Leaderboard from "@/app/leaderboard/_components/Leaderboard";
import LeaderboardMetadata from "@/app/leaderboard/_components/LeaderboardMetadata/LeaderboardMetadata";
import { Box } from "@mantine/core";
import { useParams } from "react-router";

export default function LeaderboardWithIdPage() {
  const { leaderboardId } = useParams();

  if (!leaderboardId) {
    return <></>;
  }

  return (
    <Box>
      <LeaderboardMetadata leaderboardId={leaderboardId} />
      <Leaderboard leaderboardId={leaderboardId} isHistorical />
    </Box>
  );
}
