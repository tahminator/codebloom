import { ApiResponse } from "@/lib/types/apiResponse";
import { useQuery } from "@tanstack/react-query";
import { LeaderboardEntry } from "./types";

export const useLeaderboardEntryQuery = () => {
    return useQuery({
      queryKey: ["leaderboard"],
      queryFn: fetchLeaderboard,
    });
  };

  async function fetchLeaderboard() {
    const response = await fetch("/api/test/leaderboard/all", {
      method: "GET",
    });
  
    const json = (await response.json()) as ApiResponse<LeaderboardEntry[]>;

    if (json.success) {
      return { leaderboard: json.data };
    }
  
    return { leaderboard: [] };
  }