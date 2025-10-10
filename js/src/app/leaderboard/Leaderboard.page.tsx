import Leaderboard from "@/app/leaderboard/_components/Leaderboard";
import LeaderboardMetadata from "@/app/leaderboard/_components/LeaderboardMetadata/LeaderboardMetadata";
import { Footer } from "@/components/ui/footer/Footer";
import Header from "@/components/ui/header/Header";
import { Box } from "@mantine/core";

export default function LeaderboardPage() {
  return (
    <div className="flex flex-col min-h-screen">
      <Header />
      <div className="grow">
        <Box p={"lg"}>
          <LeaderboardMetadata showClock showAllLeaderboardButton />
          <Leaderboard />
        </Box>
      </div>
      <Footer />
    </div>
  );
}
