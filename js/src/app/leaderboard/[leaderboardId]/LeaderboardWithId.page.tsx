import LeaderboardMetadataWithId from "@/app/leaderboard/[leaderboardId]/_components/LeaderboardMetadataWithId";
import LeaderboardWithId from "@/app/leaderboard/[leaderboardId]/_components/LeaderboardWithId";
import { Footer } from "@/components/ui/footer/Footer";
import Header from "@/components/ui/header/Header";
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
      <Header />
      <Box p={"lg"}>
        <LeaderboardMetadataWithId leaderboardId={leaderboardId} />
        <LeaderboardWithId leaderboardId={leaderboardId} />
      </Box>
      <Footer />
    </>
  );
}
