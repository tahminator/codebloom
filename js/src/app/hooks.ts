import { ApiResponse } from "@/lib/types/apiResponse";
import { LeaderboardEntry } from "@/lib/types/db/leaderboard";
import { useQuery } from "@tanstack/react-query";

export const useShallowLeaderboardEntriesQuery = () => {
  return useQuery({
    queryKey: ["leaderboard", "all", "shallow"],
    queryFn: fetchLeaderboard,
  });
};

async function fetchLeaderboard() {
  const response = await fetch(
    import.meta.env.DEV ?
      "/api/test/leaderboard/shallow"
    : "/api/leaderboard/current/shallow",
    {
      method: "GET",
    },
  );

  const json = (await response.json()) as ApiResponse<LeaderboardEntry>;

  return json;
}
