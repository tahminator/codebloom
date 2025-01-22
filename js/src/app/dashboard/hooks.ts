import { ApiResponse } from "@/lib/types/apiResponse";
import { useMutation, useQueryClient } from "@tanstack/react-query";

export const useUsersTotalPoints = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: updateTotalPoints,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["auth"] });
    },
  });
};

async function updateTotalPoints() {
  const res = await fetch("/api/leetcode/check", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
  });

  const json = (await res.json()) as ApiResponse<{
    acceptedSubmissions: { title: string; points: number }[];
  }>;

  if (res.status === 429) {
    return { ...json, message: Number(json.message) };
  }

  return json;
}
