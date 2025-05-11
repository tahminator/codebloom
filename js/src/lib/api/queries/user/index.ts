import { ApiResponse } from "@/lib/api/common/apiResponse";
import { Page } from "@/lib/api/common/page";
import { Question } from "@/lib/api/types/question";
import { User } from "@/lib/api/types/user";
import { useURLState } from "@/lib/hooks/useUrlState";
import { keepPreviousData, useQuery } from "@tanstack/react-query";
import { useCallback, useEffect } from "react";

/**
 * Fetch the metadata of the given user, such as Leetcode username, Discord name, and more.
 */
export const useUserProfileQuery = ({ userId }: { userId?: string }) => {
  return useQuery({
    queryKey: ["user", "profile", userId],
    queryFn: () => fetchUserProfile({ userId }),
    placeholderData: keepPreviousData,
  });
};

/**
 * Fetch the user's submissions. This is a super query and as such,
 * also exports pagination and search capabilities.
 */
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
  const [searchQuery, setSearchQuery, debouncedQuery] = useURLState(
    "query",
    "",
    tieToUrl,
    true,
    500,
  );
  const pageSize = 20;

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
    pageSize,
  };
};

export const useGetAllUsersQuery = ({
  tieToUrl = false,
}: {
  tieToUrl?: boolean;
}) => {
  const initialPage = 1;
  const pageSize = 5;

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
    queryKey: ["submission", "user", "all", page, debouncedQuery],
    queryFn: () =>
      fetchAllUsers({
        page,
        pageSize,
        query: debouncedQuery,
      }),
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
    pageSize,
  };
};

async function fetchUserProfile({ userId }: { userId?: string }) {
  const response = await fetch(`/api/user/${userId ?? ""}/profile`);

  const json = (await response.json()) as ApiResponse<User>;

  return json;
}

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

async function fetchAllUsers({
  page,
  query,
  pageSize,
}: {
  page: number;
  query?: string;
  pageSize: number;
}) {
  const response = await fetch(
    `/api/user/all?page=${page}&query=${query}&pageSize=${pageSize}`,
  );

  const json = (await response.json()) as ApiResponse<Page<User[]>>;

  return json;
}
