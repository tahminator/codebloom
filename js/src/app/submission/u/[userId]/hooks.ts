import { useURLState } from "@/lib/hooks/useUrlState";
import { ApiResponse } from "@/lib/types/apiResponse";
import { Question } from "@/lib/types/db/question";
import { User } from "@/lib/types/db/user";
import { Page } from "@/lib/types/page";
import { keepPreviousData, useQuery } from "@tanstack/react-query";
import { useCallback, useEffect } from "react";

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
  const [searchQuery, setSearchQuery] = useURLState("query", "", tieToUrl);

  const goBack = useCallback(() => {
    setPage((old) => Math.max(old - 1, 0));
  }, [setPage]);

  const goForward = useCallback(() => {
    setPage((old) => old + 1);
  }, [setPage]);

  const goTo = useCallback(
    (pageNumber: number) => {
      setPage(() => Math.max(pageNumber, 0));
    },
    [setPage],
  );

  useEffect(() => {
    goTo(1);
  }, [searchQuery, goTo]);

  const query = useQuery({
    queryKey: ["submission", "user", userId, page, searchQuery],
    queryFn: () => fetchUserSubmissions({ page, userId, query: searchQuery }),
    placeholderData: keepPreviousData,
  });

  return {
    ...query,
    page,
    goBack,
    goForward,
    goTo,
    searchQuery,
    setSearchQuery,
  };
};

async function fetchUserSubmissions({
  page,
  userId,
  query,
}: {
  page: number;
  userId?: string;
  query?: string;
}) {
  const response = await fetch(
    `/api/leetcode/submission/u/${userId ?? ""}?page=${page}&query=${query}`,
  );

  const json = (await response.json()) as ApiResponse<
    Page<User & { questions: Question[] }>
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
