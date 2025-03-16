import { ApiResponse } from "@/lib/types/apiResponse";
import { User } from "@/lib/types/db/user";
import { keepPreviousData, useQuery } from "@tanstack/react-query";

export const useUserProfileQuery = ({ userId }: { userId?: string }) => {
  return useQuery({
    queryKey: ["user", "profile", userId],
    queryFn: () => fetchUserProfile({ userId }),
    placeholderData: keepPreviousData,
  });
};

async function fetchUserProfile({ userId }: { userId?: string }) {
  const response = await fetch(`/api/user/${userId ?? ""}/profile`);

  const json = (await response.json()) as ApiResponse<User>;

  return json;
}
