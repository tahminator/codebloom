import { ApiResponse } from "@/lib/types/apiResponse";
import { useMutation, useQueryClient } from "@tanstack/react-query";

export const useSetLeetcodeUsername = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: updateLeetcodeUsername,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["auth"] });
    },
  });
};

async function updateLeetcodeUsername({
  leetcodeUsername,
}: {
  leetcodeUsername: string;
}) {
  const res = await fetch("/api/leetcode/set", {
    method: "POST",
    body: JSON.stringify({
      leetcodeUsername,
    }),
    headers: {
      "Content-Type": "application/json",
    },
  });

  const json = (await res.json()) as ApiResponse<undefined>;

  return { success: json.success, message: json.message };
}
