import { ApiResponse } from "@/lib/api/common/apiResponse";
import { Session } from "@/lib/api/types/session";
import { User } from "@/lib/api/types/user";
import { useQuery } from "@tanstack/react-query";

/**
 * Fetch the user's authentication state from the server.
 */
export const useAuthQuery = () => {
  return useQuery({
    queryKey: ["auth"],
    queryFn: validateAuthentication,
  });
};

async function validateAuthentication() {
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
