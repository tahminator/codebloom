import { ApiURL } from "@/lib/api/common/apiURL";
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
  const { url, method, res } = ApiURL.create("/api/auth/validate", {
    method: "GET",
  });
  const response = await fetch(url, {
    method,
  });

  const json = res(await response.json());

  if (json.success) {
    return {
      session: json.payload.session,
      user: json.payload.user,
      isAdmin: json.payload.user.admin,
    };
  }

  return { session: undefined, user: undefined, isAdmin: false };
}
