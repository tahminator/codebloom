import { ApiResponse } from "@/lib/types/apiResponse";
import { Session } from "@/lib/types/db/session";
import { User } from "@/lib/types/db/user";
import { useQuery } from "@tanstack/react-query";

export const useAuthQuery = () => {
  return useQuery({
    queryKey: ["auth"],
    queryFn: validateAuthentication,
  });
};

async function validateAuthentication() {
  await new Promise((r) => setTimeout(() => r(0), 1000));
  const response = await fetch("/api/auth/validate", {
    method: "GET",
  });

  const json = (await response.json()) as ApiResponse<{
    user: User;
    session: Session;
  }>;

  if (json.success) {
    return { session: json.data?.session, user: json.data?.user };
  }

  return { session: undefined, user: undefined };
}
