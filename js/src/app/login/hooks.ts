import { ApiResponse } from "@/lib/types/apiResponse";
import { Session } from "@/lib/types/db/session";
import { User } from "@/lib/types/db/user";
import { useQuery } from "@tanstack/react-query";
import { useSearchParams } from "react-router-dom";

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

export function useAuthCallbackInfo() {
  const [searchParams] = useSearchParams();

  const success = (() => {
    const val = searchParams.get("success");
    console.log(val);

    if (val === "true") {
      return true;
    }

    if (val === "false") {
      return false;
    }

    return null;
  })();

  const message = searchParams.get("message");

  return {
    success: success,
    message: message,
  };
}
