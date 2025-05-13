import { UnknownApiResponse } from "@/lib/api/common/apiResponse";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";

/**
 * This mutation will set the user's leetcode username, refetching on success
 */
export const useSetLeetcodeUsername = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: updateLeetcodeUsername,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["auth"] });
    },
  });
};

/**
 * Fetch the user's private key used for Leetcode authentication.
 */
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

  const json = (await res.json()) as UnknownApiResponse<undefined>;

  return { success: json.success, message: json.message };
}

async function getLeetcodeQueryKey() {
  const res = await fetch("/api/leetcode/key");

  const json = (await res.json()) as UnknownApiResponse<string>;

  if (!json.success) {
    return { success: json.success, message: json.message, payload: null };
  }

  return {
    success: json.success,
    message: json.message,
    payload: json.payload,
  };
}
