import { ApiResponse } from "@/lib/types/apiResponse";
import { useQuery } from "@tanstack/react-query";
import { LeaderboardEntry } from "@/app/leaderboard/types";

export const useShallowLeaderboardEntriesQuery = () => {
  return useQuery({
    queryKey: ["leaderboard", "all", "shallow"],
    queryFn: fetchLeaderboard,
  });
};

async function fetchLeaderboard() {
  const response = await fetch(
    import.meta.env.DEV
      ? "/api/test/leaderboard/shallow"
      : "/api/leaderboard/current/shallow",
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
