import { ApiResponse } from "@/lib/types/apiResponse";
import { User } from "@/lib/types/db/user";
import { useQuery, useQueryClient } from "@tanstack/react-query";

/**
 * To patch a rendering bug in the interim, prefetching is used to avoid any loading states.
 */
export const useFixMyPointsPrefetch = ({ userId }: { userId: string }) => {
  const queryClient = useQueryClient();
  queryClient.prefetchQuery({
    queryKey: ["leaderboard", "user", "me"],
    queryFn: () => getMyRecentLeaderboardData({ userId }),
  });
  return;
};

export const useMyRecentLeaderboardData = ({ userId }: { userId: string }) => {
  return useQuery({
    queryKey: ["leaderboard", "user", "me"],
    queryFn: () => getMyRecentLeaderboardData({ userId }),
  });
};

export async function getMyRecentLeaderboardData({
  userId,
}: {
  userId: string;
}) {
  const res = await fetch(`/api/leaderboard/current/user/${userId}`);

  const json = (await res.json()) as ApiResponse<User & { totalScore: number }>;

  return json;
}
