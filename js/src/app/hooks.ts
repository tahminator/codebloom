import { ApiResponse } from "@/lib/types/apiResponse";
import { useQuery } from "@tanstack/react-query";
import { LeaderboardEntry } from "@/app/leaderboard/LeaderboardEntry";

export const useShallowLeaderboardEntriesQuery = () => {
    return useQuery({
      queryKey: ["dashboard", "shallow"],
      queryFn: fetchLeaderboard,
    });
  };

  async function fetchLeaderboard() {
    const response = await fetch("/api/test/leaderboard/shallow", {
      method: "GET",
    });
  
    const json = (await response.json()) as ApiResponse<LeaderboardEntry>;

    if (json.success) {
      return { json: json.data };
    }
  
    return { json: undefined };
  }