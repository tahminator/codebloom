import { UnknownApiResponse } from "@/lib/api/common/apiResponse";
import { Indexed, Page } from "@/lib/api/common/page";
import { Api } from "@/lib/api/types";
import { Leaderboard } from "@/lib/api/types/leaderboard";
import { usePagination } from "@/lib/hooks/usePagination";
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
    defaultGwc = false,
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
    tieToUrl,
    true,
    500,
  );
  const [patina, setPatina] = useURLState("patina", false, tieToUrl, true, 100);
  const [hunter, setHunter] = useURLState("hunter", false, tieToUrl, true, 100);
  const [nyu, setNyu] = useURLState("nyu", false, tieToUrl, true, 100);
  const [baruch, setBaruch] = useURLState("baruch", false, tieToUrl, true, 100);
  const [rpi, setRpi] = useURLState("rpi", false, tieToUrl, true, 100);
  const [gwc, setGwc] = useURLState("gwc", defaultGwc, tieToUrl, true, 100);
  const [sbu, setSbu] = useURLState("sbu", false, tieToUrl, true, 100);
  const [columbia, setColumbia] = useURLState(
    "columbia",
    false,
    tieToUrl,
    true,
    100,
  );
  const [ccny, setCcny] = useURLState("ccny", false, tieToUrl, true, 100);
  const [cornell, setCornell] = useURLState(
    "cornell",
    false,
    tieToUrl,
    true,
    100,
  );
  const [globalIndex, setGlobalIndex] = useURLState(
    "globalIndex",
    false,
    tieToUrl,
    true,
    100,
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
    setHunter((prev) => !prev);
    goTo(1);
  }, [setHunter, goTo]);

  const toggleNyu = useCallback(() => {
    setNyu((prev) => !prev);
    goTo(1);
  }, [setNyu, goTo]);

  const toggleBaruch = useCallback(() => {
    setBaruch((prev) => !prev);
    goTo(1);
  }, [setBaruch, goTo]);

  const toggleRpi = useCallback(() => {
    setRpi((prev) => !prev);
    goTo(1);
  }, [setRpi, goTo]);

  const toggleGwc = useCallback(() => {
    setGwc((prev) => !prev);
    goTo(1);
  }, [goTo, setGwc]);

  const toggleSbu = useCallback(() => {
    setSbu((prev) => !prev);
    goTo(1);
  }, [setSbu, goTo]);

  const toggleColumbia = useCallback(() => {
    setColumbia((prev) => !prev);
    goTo(1);
  }, [setColumbia, goTo]);

  const toggleCcny = useCallback(() => {
    setCcny((prev) => !prev);
    goTo(1);
  }, [setCcny, goTo]);

  const toggleCornell = useCallback(() => {
    setCornell((prev) => !prev);
    goTo(1);
  }, [setCornell, goTo]);

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
      patina,
      hunter,
      nyu,
      baruch,
      rpi,
      gwc,
      sbu,
      columbia,
      ccny,
      cornell,
      globalIndex,
    ],
    queryFn: () =>
      fetchLeaderboardUsers({
        page,
        pageSize,
        patina,
        hunter,
        nyu,
        baruch,
        rpi,
        gwc,
        sbu,
        columbia,
        ccny,
        cornell,
        globalIndex,
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
    baruch,
    rpi,
    gwc,
    sbu,
    columbia,
    ccny,
    cornell,
    globalIndex,
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
    toggleBaruch,
    toggleRpi,
    toggleGlobalIndex,
    toggleGwc,
    toggleSbu,
    toggleColumbia,
    toggleCcny,
    toggleCornell,
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
    tieToUrl,
    true,
    500,
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
    tieToUrl,
    true,
    500,
  );
  const [patina, setPatina] = useURLState("patina", false, tieToUrl, true, 100);
  const [hunter, setHunter] = useURLState("hunter", false, tieToUrl, true, 100);
  const [nyu, setNyu] = useURLState("nyu", false, tieToUrl, true, 100);
  const [baruch, setBaruch] = useURLState("baruch", false, tieToUrl, true, 100);
  const [rpi, setRpi] = useURLState("rpi", false, tieToUrl, true, 100);
  const [gwc, setGwc] = useURLState("gwc", false, tieToUrl, true, 100);
  const [sbu, setSbu] = useURLState("sbu", false, tieToUrl, true, 100);
  const [columbia, setColumbia] = useURLState(
    "columbia",
    false,
    tieToUrl,
    true,
    100,
  );
  const [ccny, setCcny] = useURLState("ccny", false, tieToUrl, true, 100);
  const [cornell, setCornell] = useURLState(
    "cornell",
    false,
    tieToUrl,
    true,
    100,
  );
  const [globalIndex, setGlobalIndex] = useURLState(
    "globalIndex",
    false,
    tieToUrl,
    true,
    100,
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
    setHunter((prev) => !prev);
    goTo(1);
  }, [setHunter, goTo]);

  const toggleNyu = useCallback(() => {
    setNyu((prev) => !prev);
    goTo(1);
  }, [setNyu, goTo]);

  const toggleBaruch = useCallback(() => {
    setBaruch((prev) => !prev);
    goTo(1);
  }, [setBaruch, goTo]);

  const toggleRpi = useCallback(() => {
    setRpi((prev) => !prev);
    goTo(1);
  }, [setRpi, goTo]);

  const toggleGwc = useCallback(() => {
    setGwc((prev) => !prev);
    goTo(1);
  }, [goTo, setGwc]);

  const toggleSbu = useCallback(() => {
    setSbu((prev) => !prev);
    goTo(1);
  }, [setSbu, goTo]);

  const toggleColumbia = useCallback(() => {
    setColumbia((prev) => !prev);
    goTo(1);
  }, [setColumbia, goTo]);

  const toggleCcny = useCallback(() => {
    setCcny((prev) => !prev);
    goTo(1);
  }, [setCcny, goTo]);

  const toggleCornell = useCallback(() => {
    setCornell((prev) => !prev);
    goTo(1);
  }, [setCornell, goTo]);

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
      patina,
      hunter,
      nyu,
      baruch,
      rpi,
      sbu,
      columbia,
      ccny,
      gwc,
      cornell,
      globalIndex,
    ],
    queryFn: () =>
      fetchLeaderboardUsersByLeaderboardId({
        leaderboardId,
        page,
        pageSize,
        patina,
        hunter,
        nyu,
        baruch,
        rpi,
        gwc,
        sbu,
        columbia,
        ccny,
        cornell,
        globalIndex,
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
    baruch,
    rpi,
    gwc,
    sbu,
    columbia,
    ccny,
    cornell,
    globalIndex,
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
    toggleBaruch,
    toggleRpi,
    toggleGlobalIndex,
    toggleGwc,
    toggleSbu,
    toggleColumbia,
    toggleCcny,
    toggleCornell,
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
  baruch,
  rpi,
  gwc,
  sbu,
  columbia,
  ccny,
  cornell,
  globalIndex,
}: {
  page: number;
  query: string;
  pageSize: number;
  patina: boolean;
  hunter: boolean;
  nyu: boolean;
  baruch: boolean;
  rpi: boolean;
  gwc: boolean;
  sbu: boolean;
  columbia: boolean;
  ccny: boolean;
  cornell: boolean;
  globalIndex: boolean;
}) {
  const response = await fetch(
    `/api/leaderboard/current/user/all?page=${page}&pageSize=${pageSize}&query=${query}&patina=${patina}&hunter=${hunter}&nyu=${nyu}&baruch=${baruch}&rpi=${rpi}&gwc=${gwc}&sbu=${sbu}&columbia=${columbia}&ccny=${ccny}&cornell=${cornell}&globalIndex=${globalIndex}`,
    {
      method: "GET",
    },
  );

  const json = (await response.json()) as UnknownApiResponse<
    Page<Indexed<Api<"UserWithScoreDto">>[]>
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
  baruch,
  rpi,
  gwc,
  sbu,
  columbia,
  ccny,
  cornell,
  globalIndex,
  leaderboardId,
}: {
  page: number;
  query: string;
  pageSize: number;
  patina: boolean;
  hunter: boolean;
  nyu: boolean;
  baruch: boolean;
  rpi: boolean;
  gwc: boolean;
  sbu: boolean;
  columbia: boolean;
  ccny: boolean;
  cornell: boolean;
  globalIndex: boolean;
  leaderboardId: string;
}) {
  const response = await fetch(
    `/api/leaderboard/${leaderboardId}/user/all?page=${page}&pageSize=${pageSize}&query=${query}&patina=${patina}&hunter=${hunter}&nyu=${nyu}&baruch=${baruch}&rpi=${rpi}&gwc=${gwc}&sbu=${sbu}&columbia=${columbia}&ccny=${ccny}&cornell=${cornell}&globalIndex=${globalIndex}`,
    {
      method: "GET",
    },
  );

  const json = (await response.json()) as UnknownApiResponse<
    Page<Indexed<Api<"UserWithScoreDto">>[]>
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
    Api<"UserDto"> & { totalScore: number }
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
