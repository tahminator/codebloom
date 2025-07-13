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
  return useMutation<UnknownApiResponse<string>, Error, { email: string }>({
    mutationFn: verifySchool,
    onSuccess: async (data) => {
      if (data) {
        await queryClient.invalidateQueries();
      }
    },
  });
};
