import { ApiResponse } from "@/lib/types/apiResponse";
import { Leaderboard } from "@/lib/types/db/leaderboard";
import { useQuery } from "@tanstack/react-query";

export const useCurrentLeaderboardMetadataQuery = () => {
  return useQuery({
    queryKey: ["leaderboard", "metadata"],
    queryFn: useCurrentLeaderboardMetadata,
  });
};

async function useCurrentLeaderboardMetadata() {
  const response = await fetch(`/api/leaderboard/current/metadata`, {
    method: "GET",
  });

  const json = (await response.json()) as ApiResponse<Leaderboard>;

  return json;
}
