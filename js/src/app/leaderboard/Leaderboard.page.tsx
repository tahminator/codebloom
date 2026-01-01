import Leaderboard from "@/app/leaderboard/_components/Leaderboard";
import LeaderboardMetadata from "@/app/leaderboard/_components/LeaderboardMetadata/LeaderboardMetadata";
import { Box } from "@mantine/core";

export default function LeaderboardPage() {
  return (
    <div className="flex flex-col min-h-screen">
      <div className="grow">
        <Box p={"lg"}>
          <LeaderboardMetadata
            showClock
            showAllLeaderboardButton
            syntaxStripSize={"md"}
          />
          <Leaderboard />
        </Box>
      </div>
    </div>
  );
}
