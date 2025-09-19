import { UnknownApiResponse } from "@/lib/api/common/apiResponse";
import { useMutation, useQueryClient } from "@tanstack/react-query";

export async function verifySchool(email: { email: string }) {
  const response = await fetch("/api/auth/school/enroll", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(email),
  });

  const json = (await response.json()) as UnknownApiResponse<string>;

  return json;
}

export const useVerifySchoolMutation = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: verifySchool,
    onSuccess: async (data) => {
      if (data.success) {
        /**
         * https://github.com/tahminator/codebloom/pull/256#issue-3286903003
         */
        setTimeout(async () => {
          queryClient.invalidateQueries();
        }, 100);
      }
    },
  });
};
