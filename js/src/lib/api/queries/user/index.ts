import { ApiURL } from "@/lib/api/common/apiURL";
import { LeetcodeTopicEnum } from "@/lib/api/types/autogen/schema";
import { usePagination } from "@/lib/hooks/usePagination";
import useURLDateRange from "@/lib/hooks/useURLDateRange";
import { useURLState } from "@/lib/hooks/useUrlState";
import { notifications } from "@mantine/notifications";
import { keepPreviousData, useQuery } from "@tanstack/react-query";
import d from "dayjs";
import { useCallback, useEffect, useMemo } from "react";

/**
 * Fetch the metadata of the given user, such as Leetcode username, Discord name, and more.
 */
export const useUserProfileQuery = ({ userId }: { userId: string }) => {
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
  userId: string;
  initialPage?: number;
  tieToUrl?: boolean;
  pageSize?: number;
}) => {
  const { page, goBack, goForward, goTo } = usePagination({
    initialPage: initialPage,
    tieToUrl: tieToUrl,
  });
  const [pointFilter, setPointFilter] = useURLState("pointFilter", false, {
    enabled: tieToUrl,
    debounce: 100,
  });
  const [searchQuery, setSearchQuery, debouncedQuery] = useURLState(
    "query",
    "",
    {
      enabled: tieToUrl,
      debounce: 500,
    },
  );
  const {
    startDate,
    endDate,
    setStartDate,
    setEndDate,
    debouncedStartDate,
    debouncedEndDate,
  } = useURLDateRange(tieToUrl);

  const [_topics, _setTopics] = useURLState<string>("topics", "", {
    enabled: tieToUrl,
    debounce: 100,
  });

  useEffect(() => {
    goTo(1);
  }, [searchQuery, goTo]);

  const togglePointFilter = useCallback(() => {
    setPointFilter((prev) => !prev);
    goTo(1);
  }, [goTo, setPointFilter]);

  const topics = useMemo(
    () => _topics.split(",").filter(Boolean) as LeetcodeTopicEnum[],
    [_topics],
  );

  const setTopics = useCallback(
    (topics: LeetcodeTopicEnum[]) => {
      _setTopics(topics.join(","));
      goTo(1);
    },
    [goTo, _setTopics],
  );

  const clearTopics = useCallback(() => {
    _setTopics("");
    goTo(1);
  }, [goTo, _setTopics]);

  const query = useQuery({
    queryKey: [
      "submission",
      "user",
      userId,
      page,
      debouncedQuery,
      pageSize,
      pointFilter,
      topics,
      debouncedStartDate,
      debouncedEndDate,
    ],
    queryFn: () =>
      fetchUserSubmissions({
        page,
        userId,
        query: debouncedQuery,
        pageSize,
        pointFilter,
        topics,
        startDate: debouncedStartDate,
        endDate: debouncedEndDate,
      }),
    placeholderData: keepPreviousData,
  });

  useEffect(() => {
    if (query.status === "success" && !query.data.success) {
      notifications.show({
        message: query.data.message,
        color: "red",
      });
    }
  }, [query.data?.message, query.data?.success, query.status]);

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
    topics,
    setTopics,
    clearTopics,
    startDate,
    endDate,
    setStartDate,
    setEndDate,
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
    {
      enabled: tieToUrl,
      debounce: 500,
    },
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

async function fetchUserProfile({ userId }: { userId: string }) {
  const { url, method, res } = ApiURL.create("/api/user/{userId}/profile", {
    method: "GET",
    params: {
      userId,
    },
  });
  const response = await fetch(url, {
    method,
  });

  const json = res(await response.json());

  return json;
}

async function fetchUserSubmissions({
  page,
  userId,
  query,
  pageSize,
  pointFilter,
  topics,
  startDate,
  endDate,
}: {
  page: number;
  userId: string;
  query?: string;
  pageSize: number;
  pointFilter: boolean;
  topics?: string[];
  startDate?: string;
  endDate?: string;
}) {
  // if seconds are not included, use start of day and end of day
  const processedStartDate =
    startDate ?
      startDate.includes(":") ?
        d.utc(startDate).toISOString()
      : d(startDate).startOf("day").utc().toISOString()
    : undefined;
  const processedEndDate =
    endDate ?
      endDate.includes(":") ?
        d.utc(endDate).toISOString()
      : d(endDate).endOf("day").utc().toISOString()
    : undefined;

  const { url, method, res } = ApiURL.create("/api/user/{userId}/submissions", {
    method: "GET",
    params: {
      userId,
    },
    queries: {
      page,
      query,
      pageSize,
      pointFilter,
      topics,
      startDate: processedStartDate,
      endDate: processedEndDate,
    },
  });

  const response = await fetch(url, {
    method,
  });

  const json = res(await response.json());
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
  const { url, method, res } = ApiURL.create("/api/user/all", {
    method: "GET",
    queries: {
      page,
      query,
      pageSize,
    },
  });
  const response = await fetch(url, {
    method,
  });

  const json = res(await response.json());

  return json;
}
