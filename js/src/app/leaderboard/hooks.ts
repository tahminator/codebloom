import { useURLState } from "@/lib/hooks/useUrlState";
import { ApiResponse } from "@/lib/types/apiResponse";
import { UserWithScore } from "@/lib/types/db/user";
import { Page } from "@/lib/types/page";
import { keepPreviousData, useQuery } from "@tanstack/react-query";
import { useCallback } from "react";

export const useFullLeaderboardEntriesQuery = ({
  initialPage = 1,
  pageSize = 20,
  tieToUrl = true,
}: {
  initialPage?: number;
  pageSize?: number;
  tieToUrl?: boolean;
}) => {
  const [page, setPage] = useURLState("page", initialPage, tieToUrl);

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

  const query = useQuery({
    queryKey: ["leaderboard", "users", page, pageSize],
    queryFn: () => fetchLeaderboard({ page, pageSize }),
    placeholderData: keepPreviousData,
  });

  return {
    ...query,
    page,
    goBack,
    goForward,
    goTo,
  };
};

async function fetchLeaderboard({
  page,
  pageSize,
}: {
  page: number;
  pageSize: number;
}) {
  const response = await fetch(
    `/api/leaderboard/current/user/all?page=${page}&pageSize=${pageSize}`,
    {
      method: "GET",
    },
  );

  const json = (await response.json()) as ApiResponse<Page<UserWithScore[]>>;

  return json;
}
