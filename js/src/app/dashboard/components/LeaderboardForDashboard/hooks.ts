import { ApiResponse } from "@/lib/types/apiResponse";
import { User } from "@/lib/types/db/user";
import { useQuery } from "@tanstack/react-query";

export const useMyRecentLeaderboardData = ({ userId }: { userId: string }) => {
  return useQuery({
    queryKey: ["leaderboard", "user", "me"],
    queryFn: () => getMyRecentLeaderboardData({ userId }),
  });
};

async function getMyRecentLeaderboardData({ userId }: { userId: string }) {
  const res = await fetch(`/api/leaderboard/current/user/${userId}`);

  const json = (await res.json()) as ApiResponse<User & { totalScore: number }>;

  if (json.success) {
    return { user: json.data };
  }

  return { user: undefined };
}
