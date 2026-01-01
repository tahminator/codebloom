import DashboardListView from "@/app/leaderboard/all/_components/DashboardListView";
import DocumentDescription from "@/components/ui/title/DocumentDescription";
import DocumentTitle from "@/components/ui/title/DocumentTitle";
import { Box } from "@mantine/core";

export default function AllLeaderboardsPage() {
  return (
    <>
      <DocumentTitle title={`CodeBloom - All Leaderboards`} />
      <DocumentDescription description={`CodeBloom - View all leaderboards`} />
      <Box p={"lg"}>
        <DashboardListView />
      </Box>
    </>
  );
}
