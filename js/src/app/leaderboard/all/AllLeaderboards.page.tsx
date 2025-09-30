import DashboardListView from "@/app/leaderboard/all/_components/DashboardListView";
import { Footer } from "@/components/ui/footer/Footer";
import Header from "@/components/ui/header/Header";
import DocumentDescription from "@/components/ui/title/DocumentDescription";
import DocumentTitle from "@/components/ui/title/DocumentTitle";
import { Box } from "@mantine/core";

export default function AllLeaderboardsPage() {
  return (
    <>
      <DocumentTitle title={`CodeBloom - All Leaderboards`} />
      <DocumentDescription
        description={`CodeBloom - View all leaderboards`}
      />
      <Header />
      <Box p={"lg"}>
        <DashboardListView />
      </Box>
      <Footer />
    </>
  );
}
