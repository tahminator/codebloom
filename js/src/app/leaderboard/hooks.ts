import { LeaderboardEntry } from "@/app/leaderboard/types";
import { ApiResponse } from "@/lib/types/apiResponse";
import { useQuery } from "@tanstack/react-query";

export const useFullLeaderboardEntriesQuery = () => {
  return useQuery({
    queryKey: ["leaderboard", "all", "full"],
    queryFn: fetchLeaderboard,
  });
};

async function fetchLeaderboard() {
  const response = await fetch(
    import.meta.env.DEV
      ? "/api/test/leaderboard/all"
      : "/api/leaderboard/current",
    {
      method: "GET",
    }
  );

  const json = (await response.json()) as ApiResponse<LeaderboardEntry>;

  if (json.success) {
    return { json: json.data };
  }

  return { json: undefined };
}
