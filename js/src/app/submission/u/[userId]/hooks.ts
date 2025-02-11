import { useURLState } from "@/lib/hooks/useUrlState";
import { ApiResponse } from "@/lib/types/apiResponse";
import { Question } from "@/lib/types/db/question";
import { User } from "@/lib/types/db/user";
import { Page } from "@/lib/types/page";
import { keepPreviousData, useQuery } from "@tanstack/react-query";

export const useUserSubmissionsQuery = ({
  userId,
  initialPage = 1,
  tieToUrl = false,
}: {
  userId?: string;
  initialPage?: number;
  tieToUrl?: boolean;
}) => {
  const [page, setPage] = useURLState("page", initialPage, tieToUrl);

  const goBack = () => {
    setPage((old) => Math.max(old - 1, 0));
  };

  const goForward = () => {
    setPage((old) => old + 1);
  };

  const goTo = (pageNumber: number) => {
    setPage(() => Math.max(pageNumber, 0));
  };

  const query = useQuery({
    queryKey: ["submission", "user", userId, page],
    queryFn: () => fetchUserSubmissions({ page, userId }),
    placeholderData: keepPreviousData,
  });

  return { ...query, page, goBack, goForward, goTo };
};

async function fetchUserSubmissions({
  page,
  userId,
}: {
  page: number;
  userId?: string;
}) {
  const response = await fetch(
    `/api/leetcode/submission/u/${userId ?? ""}?page=${page}`,
  );

  const json = (await response.json()) as ApiResponse<
    Page<(Question & Pick<User, "discordName" | "leetcodeUsername">)[]>
  >;

  if (json.success) {
    return { ...json.data, success: json.success, message: json.message };
  }

  return {
    hasNextPage: false,
    data: null,
    pages: 0,
    message: json.message,
    success: json.success,
  };
}
