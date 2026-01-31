import { CurrentLeaderboard } from "@/app/leaderboard/_components/Leaderboard";
import { Box } from "@mantine/core";

export default function LeaderboardEmbed() {
  return (
    <>
      <Box className="grow">
        <Box pl={"lg"} pr={"lg"}>
          <CurrentLeaderboard embedded />
        </Box>
      </Box>
    </>
  );
}
