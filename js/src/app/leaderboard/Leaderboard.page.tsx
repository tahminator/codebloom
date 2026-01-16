import { CurrentLeaderboard } from "@/app/leaderboard/_components/Leaderboard";
import { CurrentLeaderboardMetadata } from "@/app/leaderboard/_components/LeaderboardMetadata/LeaderboardMetadata";
import { Box } from "@mantine/core";

export default function LeaderboardPage() {
  return (
    <div className="flex flex-col min-h-screen">
      <div className="grow">
        <Box>
          <CurrentLeaderboardMetadata
            showClock
            showAllLeaderboardButton
            syntaxStripSize={"md"}
          />
          <CurrentLeaderboard />
        </Box>
      </div>
    </div>
  );
}
