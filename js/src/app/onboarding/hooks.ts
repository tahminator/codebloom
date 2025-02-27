import { ApiResponse } from "@/lib/types/apiResponse";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";

export const useSetLeetcodeUsername = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: updateLeetcodeUsername,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["auth"] });
    },
  });
};

export const useAuthKeyQuery = () => {
  return useQuery({
    queryKey: ["auth", "key"],
    queryFn: getLeetcodeQueryKey,
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

async function getLeetcodeQueryKey() {
  const res = await fetch("/api/leetcode/key");

  const json = (await res.json()) as ApiResponse<string>;

  if (!json.success) {
    return { success: json.success, message: json.message, data: null };
  }

  return { success: json.success, message: json.message, data: json.data };
}
