import { ApiResponse } from "@/lib/types/apiResponse";
import { useQuery } from "@tanstack/react-query";
import { LeaderboardEntry } from "./LeaderboardEntry";

export const useFullLeaderboardEntriesQuery = () => {
    return useQuery({
      queryKey: ["dashboard, full"],
      queryFn: fetchLeaderboard,
    });
  };

  async function fetchLeaderboard() {
    const response = await fetch("/api/test/leaderboard/all", {
      method: "GET",
    });
  
    const json = (await response.json()) as ApiResponse<LeaderboardEntry>;

    if (json.success) {
      return { json: json.data };
    }
  
    return { json: undefined };
  }