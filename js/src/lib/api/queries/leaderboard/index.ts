import { UnknownApiResponse } from "@/lib/api/common/apiResponse";
import { Page } from "@/lib/api/common/page";
import { Leaderboard } from "@/lib/api/types/leaderboard";
import { User, UserWithScore } from "@/lib/api/types/user";
import { useURLState } from "@/lib/hooks/useUrlState";
import {
  keepPreviousData,
  useMutation,
  useQuery,
  useQueryClient,
} from "@tanstack/react-query";
import { useCallback } from "react";

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
  } = {
    initialPage: 1,
    pageSize: 20,
    tieToUrl: true,
  },
) => {
  const [page, setPage] = useURLState("page", initialPage, tieToUrl);

  /**
   * We wrap _setSearchQuery with a setSearchQuery because we need to run a side effect anytime we update the query.
   */
  const [searchQuery, _setSearchQuery, debouncedQuery] = useURLState(
    "query",
    "",
    tieToUrl,
    true,
    500,
  );
  const [patina, setPatina] = useURLState("patina", false, tieToUrl, true, 100);
  const [hunter, setHunter] = useURLState("hunter", false, tieToUrl, true, 100);
  const [nyu, setNyu] = useURLState("nyu", false, tieToUrl, true, 100);

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

  const togglePatina = useCallback(() => {
    setPatina((prev) => !prev);
    goTo(1);
  }, [setPatina, goTo]);

  const toggleHunter = useCallback(() => {
    setHunter(prev => {
      const next = !prev;
      if (next) setNyu(false);
      return next;
    });
    goTo(1);
  }, [setHunter, setNyu, goTo]);

  const toggleNyu = useCallback(() => {
    setNyu(prev => {
      const next = !prev;
      if (next) setHunter(false);
      return next;
    });
    goTo(1);
  }, [setNyu, setHunter, goTo]);

  const query = useQuery({
    queryKey: ["leaderboard", "users", page, pageSize, debouncedQuery, patina, hunter, nyu],
    queryFn: () =>
      fetchLeaderboardUsers({ page, pageSize, patina, hunter, nyu, query: debouncedQuery }),
    placeholderData: keepPreviousData,
  });

  return {
    ...query,
    page,
    patina,
    hunter,
    nyu,
    goBack,
    goForward,
    goTo,
    searchQuery,
    setSearchQuery,
    debouncedQuery,
    pageSize,
    togglePatina,
    toggleHunter,
    toggleNyu,
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
  const [page, setPage] = useURLState("page", initialPage, tieToUrl);
  /**
   * We wrap _setSearchQuery with a setSearchQuery because we need to run a side effect anytime we update the query.
   */
  const [searchQuery, _setSearchQuery, debouncedQuery] = useURLState(
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
  const [page, setPage] = useURLState("page", initialPage, tieToUrl);

  /**
   * We wrap _setSearchQuery with a setSearchQuery because we need to run a side effect anytime we update the query.
   */
  const [searchQuery, _setSearchQuery, debouncedQuery] = useURLState(
    "query",
    "",
    tieToUrl,
    true,
    500,
  );
  const [patina, setPatina] = useURLState("patina", false, tieToUrl, true, 100);
  const [hunter, setHunter] = useURLState("hunter", false, tieToUrl, true, 100);
  const [nyu, setNyu] = useURLState("nyu", false, tieToUrl, true, 100);
  
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

  const togglePatina = useCallback(() => {
    setPatina((prev) => !prev);
    goTo(1);
  }, [setPatina, goTo]);

  const toggleHunter = useCallback(() => {
    setHunter(prev => {
      const next = !prev;
      if (next) setNyu(false);
      return next;
    });
    goTo(1);
  }, [setHunter, setNyu, goTo]);

  const toggleNyu = useCallback(() => {
    setNyu(prev => {
      const next = !prev;
      if (next) setHunter(false);
      return next;
    });
    goTo(1);
  }, [setNyu, setHunter, goTo]);

  const query = useQuery({
    queryKey: [
      "leaderboard",
      leaderboardId,
      "users",
      page,
      pageSize,
      debouncedQuery,
      patina,
      hunter,
      nyu,
    ],
    queryFn: () =>
      fetchLeaderboardUsersByLeaderboardId({
        leaderboardId,
        page,
        pageSize,
        patina,
        hunter,
        nyu,
        query: debouncedQuery,
      }),
    placeholderData: keepPreviousData,
  });

  return {
    ...query,
    page,
    patina,
    hunter,
    nyu,
    goBack,
    goForward,
    goTo,
    searchQuery,
    setSearchQuery,
    debouncedQuery,
    pageSize,
    togglePatina,
    toggleHunter,
    toggleNyu,
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
  patina,
  hunter,
  nyu,
}: {
  page: number;
  query: string;
  pageSize: number;
  patina: boolean;
  hunter: boolean;
  nyu: boolean;
}) {
  const response = await fetch(
    `/api/leaderboard/current/user/all?page=${page}&pageSize=${pageSize}&query=${query}&patina=${patina}&hunter=${hunter}&nyu=${nyu}`,
    {
      method: "GET",
    },
  );

  const json = (await response.json()) as UnknownApiResponse<
    Page<UserWithScore[]>
  >;

  return json;
}

async function fetchLeaderboardUsersByLeaderboardId({
  page,
  query,
  pageSize,
  patina,
  hunter,
  nyu,
  leaderboardId,
}: {
  page: number;
  query: string;
  pageSize: number;
  patina: boolean;
  hunter: boolean;
  nyu: boolean;
  leaderboardId: string;
}) {
  const response = await fetch(
    `/api/leaderboard/${leaderboardId}/user/all?page=${page}&pageSize=${pageSize}&query=${query}&patina=${patina}&hunter=${hunter}&nyu=${nyu}`,
    {
      method: "GET",
    },
  );

  const json = (await response.json()) as UnknownApiResponse<
    Page<UserWithScore[]>
  >;

  return json;
}

async function useCurrentLeaderboardMetadata() {
  const response = await fetch(`/api/leaderboard/current/metadata`, {
    method: "GET",
  });

  const json = (await response.json()) as UnknownApiResponse<Leaderboard>;

  return json;
}

async function getLeaderboardMetadataById(leaderboardId: string) {
  const response = await fetch(`/api/leaderboard/${leaderboardId}/metadata`, {
    method: "GET",
  });

  const json = (await response.json()) as UnknownApiResponse<Leaderboard>;

  return json;
}

async function updateTotalPoints() {
  const res = await fetch("/api/leetcode/check", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
  });

  const json = (await res.json()) as UnknownApiResponse<{
    acceptedSubmissions: { title: string; points: number }[];
  }>;

  if (res.status === 429) {
    return { ...json, message: Number(json.message) };
  }

  return json;
}

export async function getMyRecentLeaderboardData({
  userId,
}: {
  userId: string;
}) {
  const res = await fetch(`/api/leaderboard/current/user/${userId}`);

  const json = (await res.json()) as UnknownApiResponse<
    User & { totalScore: number }
  >;

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
  const response = await fetch(
    `/api/leaderboard/all/metadata?page=${page}&pageSize=${pageSize}&query=${query}`,
    {
      method: "GET",
    },
  );

  const json = (await response.json()) as UnknownApiResponse<
    Page<Leaderboard[]>
  >;

  return json;
}
