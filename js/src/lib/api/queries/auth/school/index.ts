import { ApiURL } from "@/lib/api/common/apiURL";
import { useMutation, useQueryClient } from "@tanstack/react-query";

export async function verifySchool(email: { email: string }) {
  const { url, method, req, res } = ApiURL.create("/api/auth/school/enroll", {
    method: "POST",
  });
  const response = await fetch(url, {
    method,
    body: req(email),
  });

  const json = res(await response.json());

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
