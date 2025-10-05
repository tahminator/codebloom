import { useCallback } from "react";

import { useURLState } from "./useUrlState";

type Paginator = {
  page: number;
  goBack: () => void;
  goForward: () => void;
  goTo: (n: number) => void;
};

type PaginatedProps = {
  initialPage?: number;
  tieToUrl?: boolean;
};

const defaultProps = {
  initialPage: 1,
  tieToUrl: true,
};

/**
 * A custom React hook for managing pagination of lists
 *
 * @param {PaginatedProps} props - Configuration options for the pagination hook.
 * @param {number} props.initialPage - The starting page number.
 * @param {boolean} props.tieToUrl - Whether to sync the page state with the URL.
 *
 * @returns {Paginator} A paginator object containing:
 * - `page` (number): The current page number.
 * - `goBack` (function): Decrements the page number, but never below 1.
 * - `goForward` (function): Increments the page number, but never past the amount of elements.
 * - `goTo` (function): Sets the page number to a specific value (minimum 1).
 */
export function usePagination({
  initialPage = defaultProps.initialPage,
  tieToUrl = defaultProps.tieToUrl,
}: PaginatedProps = defaultProps): Paginator {
  const [page, setPage] = useURLState("page", initialPage, {
    enabled: tieToUrl,
  });

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
