import { useURLState } from "@/lib/hooks/useUrlState";
import { ApiResponse } from "@/lib/types/apiResponse";
import { LeaderboardEntry } from "@/lib/types/db/leaderboard";
import { Page } from "@/lib/types/page";
import { keepPreviousData, useQuery } from "@tanstack/react-query";
import { useCallback } from "react";

export const useFullLeaderboardEntriesQuery = ({
  initialPage = 1,
}: {
  initialPage?: number;
}) => {
  const [page, setPage] = useURLState("page", initialPage);

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
    queryKey: ["leaderboard", "all", "full", page],
    queryFn: () => fetchLeaderboard({ page }),
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

async function fetchLeaderboard({ page }: { page: number }) {
  const response = await fetch(`/api/leaderboard/current?page=${page}`, {
    method: "GET",
  });

  const json = (await response.json()) as ApiResponse<Page<LeaderboardEntry>>;

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
