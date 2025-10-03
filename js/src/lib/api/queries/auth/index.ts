import { UnknownApiResponse } from "@/lib/api/common/apiResponse";
import { Api } from "@/lib/api/types";
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

  const json = (await response.json()) as UnknownApiResponse<{
    user: Api<"PrivateUserDto">;
    session: Api<"SessionDto">;
  }>;
  if (json.success) {
    return {
      session: json.payload.session,
      user: json.payload.user,
      isAdmin: json.payload.user.admin,
    };
  }

  return { session: undefined, user: undefined, isAdmin: false };
}
