import { Box } from "@mantine/core";

import OrgEmbedView from "./_components/OrgEmbedView";

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
