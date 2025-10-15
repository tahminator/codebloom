import { ApiURL } from "@/lib/api/common/apiURL";
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
  const { url, method, req, res } = ApiURL.create("/api/leetcode/set", {
    method: "POST",
  });
  const response = await fetch(url, {
    method,
    body: req({
      leetcodeUsername,
    }),
  });

  return res(await response.json());
}

async function getLeetcodeQueryKey() {
  const { url, method, res } = ApiURL.create("/api/leetcode/key", {
    method: "GET",
  });
  const response = await fetch(url, {
    method,
  });

  return res(await response.json());
}
