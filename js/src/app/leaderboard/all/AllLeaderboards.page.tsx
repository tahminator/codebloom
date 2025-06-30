import DashboardListView from "@/app/leaderboard/all/_components/DashboardListView";
import { Footer } from "@/components/ui/footer/Footer";
import Header from "@/components/ui/header/Header";
import { Box } from "@mantine/core";

export default function AllLeaderboardsPage() {
  return (
    <>
      <Header />
      <Box p={"lg"}>
        <DashboardListView />
      </Box>
      <Footer />
    </>
  );
}
