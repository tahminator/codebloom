import { useURLState } from "@/lib/hooks/useUrlState";
import { ApiResponse } from "@/lib/types/apiResponse";
import { Question } from "@/lib/types/db/question";
import { Page } from "@/lib/types/page";
import { keepPreviousData, useQuery } from "@tanstack/react-query";
import { useCallback, useEffect } from "react";

export const useUserSubmissionsQuery = ({
  userId,
  initialPage = 1,
  tieToUrl = false,
  pageSize = 20,
}: {
  userId?: string;
  initialPage?: number;
  tieToUrl?: boolean;
  pageSize?: number;
}) => {
  const [page, setPage] = useURLState("page", initialPage, tieToUrl);
  const [searchQuery, setSearchQuery, debouncedQuery] = useURLState(
    "query",
    "",
    tieToUrl,
    true,
    500,
  );

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
    queryKey: ["submission", "user", userId, page, debouncedQuery],
    queryFn: () =>
      fetchUserSubmissions({ page, userId, query: debouncedQuery, pageSize }),
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
    debouncedQuery,
  };
};

async function fetchUserSubmissions({
  page,
  userId,
  query,
  pageSize,
}: {
  page: number;
  userId?: string;
  query?: string;
  pageSize: number;
}) {
  const response = await fetch(
    `/api/user/${userId ?? ""}/submissions?page=${page}&query=${query}&pageSize=${pageSize}`,
  );

  const json = (await response.json()) as ApiResponse<Page<Question[]>>;

  return json;
}
