import { useCallback } from "react";

import { useURLState } from "./useUrlState";

type PaginatedReturn = {
  page: number;
  goBack: () => void;
  goForward: () => void;
  goTo: (n: number) => void;
};

type PaginatedProps = {
  initialPage?: number;
  tieToUrl?: boolean;
};

export function usePagination({
  initialPage = 1,
  tieToUrl = true,
}: PaginatedProps): PaginatedReturn {
  const [page, setPage] = useURLState("page", initialPage, tieToUrl);

  const goBack = useCallback(() => {
    setPage((old: number) => Math.max(old - 1, 1));
  }, [setPage]);

  const goForward = useCallback(() => {
    setPage((old: number) => old + 1);
  }, [setPage]);

  const goTo = useCallback(
    (pageNumber: number) => {
      setPage(() => Math.max(pageNumber, 1));
    },
    [setPage],
  );
  return {
    page,
    goBack,
    goForward,
    goTo,
  };
}
