import { UnknownApiResponse } from "@/lib/api/common/apiResponse";
import { Page } from "@/lib/api/common/page";
import { Question } from "@/lib/api/types/question";
import { User } from "@/lib/api/types/user";
import { usePagination } from "@/lib/hooks/usePagination";
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
  pageSize = 20,
}: {
  userId?: string;
  initialPage?: number;
  tieToUrl?: boolean;
  pageSize?: number;
}) => {
  const { page, goBack, goForward, goTo } = usePagination({
    initialPage: initialPage,
    tieToUrl: tieToUrl,
  });
  const [pointFilter, setPointFilter] = useURLState(
    "pointFilter",
    false,
    tieToUrl,
    true,
    100,
  );
  const [searchQuery, setSearchQuery, debouncedQuery] = useURLState(
    "query",
    "",
    tieToUrl,
    true,
    500,
  );

  useEffect(() => {
    goTo(1);
  }, [searchQuery, goTo]);

  const togglePointFilter = useCallback(() => {
    setPointFilter((prev) => !prev);
    goTo(1);
  }, [goTo, setPointFilter]);

  const query = useQuery({
    queryKey: [
      "submission",
      "user",
      userId,
      page,
      debouncedQuery,
      pageSize,
      pointFilter,
    ],
    queryFn: () =>
      fetchUserSubmissions({
        page,
        userId,
        query: debouncedQuery,
        pageSize,
        pointFilter,
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
    pointFilter,
    togglePointFilter,
  };
};

export const useMiniSubmissionsQuery = {};

export const useGetAllUsersQuery = (
  {
    tieToUrl = false,
  }: {
    tieToUrl?: boolean;
  } = {
    tieToUrl: false,
  },
) => {
  const initialPage = 1;
  const pageSize = 5;

  const { page, goBack, goForward, goTo } = usePagination({
    initialPage: initialPage,
    tieToUrl: tieToUrl,
  });
  const [searchQuery, setSearchQuery, debouncedQuery] = useURLState(
    "query",
    "",
    tieToUrl,
    true,
    500,
  );

  useEffect(() => {
    goTo(1);
  }, [searchQuery, goTo]);

  const query = useQuery({
    queryKey: ["user", "all", page, debouncedQuery],
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

  const json = (await response.json()) as UnknownApiResponse<User>;

  return json;
}

async function fetchUserSubmissions({
  page,
  userId,
  query,
  pageSize,
  pointFilter,
  topics,
}: {
  page: number;
  userId?: string;
  query?: string;
  pageSize: number;
  pointFilter: boolean;
  topics?: string;
}) {
  const response = await fetch(
    `/api/user/${userId ?? ""}/submissions?page=${page}&query=${query}&pageSize=${pageSize}&pointFilter=${pointFilter}&topics=${topics ?? ""}`,
  );

  const json = (await response.json()) as UnknownApiResponse<Page<Question[]>>;
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

  const json = (await response.json()) as UnknownApiResponse<Page<User[]>>;

  return json;
}
