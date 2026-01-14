import OrgEmbedView from "@/app/embed/leaderboard/_components/OrgEmbedView";
import { Box } from "@mantine/core";

export default function LeaderboardEmbed() {
  return (
    <>
      <Box className="grow">
        <Box pl={"lg"} pr={"lg"}>
          <OrgEmbedView />
        </Box>
      </Box>
    </>
  );
}
