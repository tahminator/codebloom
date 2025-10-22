import { ApiURL } from "@/lib/api/common/apiURL";
import { ApiUtils } from "@/lib/api/utils";
import {
  TagEnumToBooleanFilterObject,
  useFilters,
} from "@/lib/hooks/useFilters";
import { usePagination } from "@/lib/hooks/usePagination";
import { useURLState } from "@/lib/hooks/useUrlState";
import {
  keepPreviousData,
  useMutation,
  useQuery,
  useQueryClient,
} from "@tanstack/react-query";
import { useCallback, useEffect, useMemo } from "react";

/**
 * Fetch the users on the current leaderboard. This is a super query
 * that also exposes pagination.
 */
export const useCurrentLeaderboardUsersQuery = (
  {
    initialPage = 1,
    pageSize = 20,
    tieToUrl = true,
  }: {
    initialPage?: number;
    pageSize?: number;
    tieToUrl?: boolean;
    defaultGwc?: boolean;
  } = {
    initialPage: 1,
    pageSize: 20,
    tieToUrl: true,
    defaultGwc: false,
  },
) => {
  const { filters, toggleFilter } = useFilters();
  const { page, goBack, goForward, goTo } = usePagination({
    initialPage: initialPage,
    tieToUrl: tieToUrl,
  });

  // hacky impl to track filters changing.
  const stringifiedFilters = useMemo(() => JSON.stringify(filters), [filters]);

  useEffect(() => {
    goTo(1);
  }, [stringifiedFilters, goTo]);

  /**
   * We wrap _setSearchQuery with a setSearchQuery because we need to run a side effect anytime we update the query.
   */
  const [searchQuery, _setSearchQuery, debouncedQuery] = useURLState(
    "query",
    "",
    {
      enabled: tieToUrl,
      debounce: 100,
    },
  );
  const [globalIndex, setGlobalIndex] = useURLState("globalIndex", false, {
    enabled: tieToUrl,
    debounce: 100,
  });

  /**
   * Abstracted function so that we can also reset the page back to 1 whenever we update the query.
   * TODO - Move these side effects within the useURLState function, which will make it easier to deal with.
   */
  const setSearchQuery = useCallback(
    (query: string) => {
      _setSearchQuery(query);
      goTo(1);
    },
    [_setSearchQuery, goTo],
  );

  const toggleGlobalIndex = useCallback(() => {
    setGlobalIndex((prev) => !prev);
    goTo(1);
  }, [goTo, setGlobalIndex]);

  const query = useQuery({
    queryKey: [
      "leaderboard",
      "users",
      page,
      pageSize,
      debouncedQuery,
      filters,
      globalIndex,
    ],
    queryFn: () =>
      fetchLeaderboardUsers({
        page,
        pageSize,
        filters,
        globalIndex,
        query: debouncedQuery,
      }),
    placeholderData: keepPreviousData,
  });

  return {
    ...query,
    page,
    globalIndex,
    goBack,
    goForward,
    goTo,
    searchQuery,
    setSearchQuery,
    filters,
    toggleFilter,
    debouncedQuery,
    pageSize,
    toggleGlobalIndex,
  };
};

/**
 * Fetch a list of all leaderboards. This is a super query that
 * also exposes pagination.
 */
export const useAllLeaderboardsMetadataQuery = ({
  initialPage = 1,
  pageSize = 20,
  tieToUrl = true,
}: {
  initialPage?: number;
  pageSize?: number;
  tieToUrl?: boolean;
}) => {
  const { page, goBack, goForward, goTo } = usePagination({
    initialPage: initialPage,
    tieToUrl: tieToUrl,
  });
  /**
   * We wrap _setSearchQuery with a setSearchQuery because we need to run a side effect anytime we update the query.
   */
  const [searchQuery, _setSearchQuery, debouncedQuery] = useURLState(
    "query",
    "",
    {
      enabled: tieToUrl,
      debounce: 500,
    },
  );

  /**
   * Abstracted function so that we can also reset the page back to 1 whenever we update the query.
   * TODO - Move these side effects within the useURLState function, which will make it easier to deal with.
   */
  const setSearchQuery = useCallback(
    (query: string) => {
      _setSearchQuery(query);
      goTo(1);
    },
    [_setSearchQuery, goTo],
  );

  const query = useQuery({
    queryKey: [
      "leaderboard",
      "metadata",
      "all",
      page,
      pageSize,
      debouncedQuery,
    ],
    queryFn: () =>
      fetchAllLeaderboardsMetadata({ page, pageSize, query: debouncedQuery }),
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

/**
 * Fetch the users from the given leaderboard ID. This is a super query
 * that also exposes pagination.
 */
export const useLeaderboardUsersByIdQuery = ({
  initialPage = 1,
  pageSize = 20,
  tieToUrl = true,
  leaderboardId,
}: {
  initialPage?: number;
  pageSize?: number;
  tieToUrl?: boolean;
  leaderboardId: string;
}) => {
  const { filters, toggleFilter } = useFilters();
  const { page, goBack, goForward, goTo } = usePagination({
    initialPage: initialPage,
    tieToUrl: tieToUrl,
  });

  // hacky impl to track filters changing.
  const stringifiedFilters = useMemo(() => JSON.stringify(filters), [filters]);

  useEffect(() => {
    goTo(1);
  }, [stringifiedFilters, goTo]);

  /**
   * We wrap _setSearchQuery with a setSearchQuery because we need to run a side effect anytime we update the query.
   */
  const [searchQuery, _setSearchQuery, debouncedQuery] = useURLState(
    "query",
    "",
    {
      enabled: tieToUrl,
      debounce: 500,
    },
  );
  const [globalIndex, setGlobalIndex] = useURLState("globalIndex", false, {
    enabled: tieToUrl,
    debounce: 100,
  });

  /**
   * Abstracted function so that we can also reset the page back to 1 whenever we update the query.
   * TODO - Move these side effects within the useURLState function, which will make it easier to deal with.
   */
  const setSearchQuery = useCallback(
    (query: string) => {
      _setSearchQuery(query);
      goTo(1);
    },
    [_setSearchQuery, goTo],
  );

  const toggleGlobalIndex = useCallback(() => {
    setGlobalIndex((prev) => !prev);
    goTo(1);
  }, [goTo, setGlobalIndex]);

  const query = useQuery({
    queryKey: [
      "leaderboard",
      leaderboardId,
      "users",
      page,
      pageSize,
      debouncedQuery,
      filters,
      globalIndex,
    ],
    queryFn: () =>
      fetchLeaderboardUsersByLeaderboardId({
        leaderboardId,
        page,
        pageSize,
        filters,
        globalIndex,
        query: debouncedQuery,
      }),
    placeholderData: keepPreviousData,
  });

  return {
    ...query,
    page,
    filters,
    toggleFilter,
    globalIndex,
    goBack,
    goForward,
    goTo,
    searchQuery,
    setSearchQuery,
    debouncedQuery,
    pageSize,
    toggleGlobalIndex,
  };
};

/**
 * Fetch the details about a leaderboard (excluding users)
 */
export const useCurrentLeaderboardMetadataQuery = () => {
  return useQuery({
    queryKey: ["leaderboard", "metadata"],
    queryFn: useCurrentLeaderboardMetadata,
  });
};

/**
 * Fetch the details about a leaderboard by ID (excluding users)
 */
export const useLeaderboardMetadataByIdQuery = (leaderboardId: string) => {
  return useQuery({
    queryKey: ["leaderboard", leaderboardId, "metadata"],
    queryFn: () => getLeaderboardMetadataById(leaderboardId),
  });
};

/**
 * Query the backend to fetch points. If it returns a successful search,
 * we invalidate all the queries to see if anything has changed.
 */
export const useUsersTotalPoints = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: updateTotalPoints,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["submission"] });
      queryClient.invalidateQueries({ queryKey: ["leaderboard"] });
      queryClient.invalidateQueries({ queryKey: ["potd"] });
    },
  });
};

/**
 * To patch a rendering bug in the interim, prefetching is used to avoid any loading states.
 */
export const useFixMyPointsPrefetch = ({ userId }: { userId: string }) => {
  const queryClient = useQueryClient();
  queryClient.prefetchQuery({
    queryKey: ["leaderboard", "user", "me"],
    queryFn: () => getMyRecentLeaderboardData({ userId }),
  });
  return;
};

/**
 * Fetch the specific submission data of the authenticated user.
 */
export const useMyRecentLeaderboardData = ({ userId }: { userId: string }) => {
  return useQuery({
    queryKey: ["leaderboard", "user", "me"],
    queryFn: () => getMyRecentLeaderboardData({ userId }),
  });
};

async function fetchLeaderboardUsers({
  page,
  query,
  pageSize,
  filters,
  globalIndex,
}: {
  page: number;
  query: string;
  pageSize: number;
  filters: TagEnumToBooleanFilterObject;
  globalIndex: boolean;
}) {
  const { url, method, res } = ApiURL.create(
    "/api/leaderboard/current/user/all",
    {
      method: "GET",
      queries: {
        page,
        pageSize,
        query,
        globalIndex,
        ...Object.typedFromEntries(
          Object.typedEntries(filters).map(([tagEnum, filterEnabled]) => {
            const metadata = ApiUtils.getMetadataByTagEnum(tagEnum);

            return [metadata.apiKey, filterEnabled];
          }),
        ),
      },
    },
  );

  const response = await fetch(url, {
    method,
  });

  const json = res(await response.json());

  return json;
}

async function fetchLeaderboardUsersByLeaderboardId({
  page,
  query,
  pageSize,
  filters,
  globalIndex,
  leaderboardId,
}: {
  page: number;
  query: string;
  pageSize: number;
  filters: TagEnumToBooleanFilterObject;
  globalIndex: boolean;
  leaderboardId: string;
}) {
  const { url, res, method } = ApiURL.create(
    "/api/leaderboard/{leaderboardId}/user/all",
    {
      method: "GET",
      params: {
        leaderboardId,
      },
      queries: {
        query,
        page,
        pageSize,
        globalIndex,
        ...Object.fromEntries(
          Object.typedEntries(filters).map(([tagEnum, filterEnabled]) => {
            const metadata = ApiUtils.getMetadataByTagEnum(tagEnum);

            return [metadata.apiKey, filterEnabled];
          }),
        ),
      },
    },
  );
  const response = await fetch(url, {
    method,
  });

  const json = res(await response.json());

  return json;
}

async function useCurrentLeaderboardMetadata() {
  const { url, method, res } = ApiURL.create(
    "/api/leaderboard/current/metadata",
    {
      method: "GET",
    },
  );
  const response = await fetch(url, {
    method,
  });

  const json = res(await response.json());

  return json;
}

async function getLeaderboardMetadataById(leaderboardId: string) {
  const { url, method, res } = ApiURL.create(
    "/api/leaderboard/{leaderboardId}/metadata",
    {
      method: "GET",
      params: {
        leaderboardId,
      },
    },
  );
  const response = await fetch(url, {
    method,
  });

  const json = res(await response.json());

  return json;
}

async function updateTotalPoints() {
  const { url, method, res } = ApiURL.create("/api/leetcode/check", {
    method: "POST",
  });
  const response = await fetch(url, {
    method,
  });

  const json = res(await response.json());

  // returns wait-until rate limit number.
  // TODO - Make it a header
  if (response.status === 429) {
    return { ...json, message: Number(json.message) };
  }

  return json;
}

export async function getMyRecentLeaderboardData({
  userId,
}: {
  userId: string;
}) {
  const { url, method, res } = ApiURL.create(
    "/api/leaderboard/current/user/{userId}",
    {
      method: "GET",
      params: {
        userId,
      },
    },
  );
  const response = await fetch(url, {
    method,
  });

  const json = res(await response.json());

  return json;
}

async function fetchAllLeaderboardsMetadata({
  page,
  pageSize,
  query,
}: {
  page: number;
  query: string;
  pageSize: number;
}) {
  const { url, method, res } = ApiURL.create("/api/leaderboard/all/metadata", {
    method: "GET",
    queries: {
      page,
      pageSize,
      query,
    },
  });
  const response = await fetch(url, {
    method,
  });

  const json = res(await response.json());

  return json;
}
