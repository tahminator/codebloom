import {
  useCurrentLeaderboardMetadataQuery,
  useLeaderboardMetadataByIdQuery,
} from "@/lib/api/queries/leaderboard";
import { useCallback } from "react";

/**
 * React hook that provides a function to generate user submissions URLs
 * with leaderboard date range parameters.
 *
 * @param {Object} options - Configuration options for the hook.
 * @param {string} options.leaderboardId - (Optional) The leaderboard ID to fetch metadata for.
 *   If not provided, uses the current active leaderboard.
 *
 * @returns An object containing:
 * - `getUserSubmissionsUrl`: a function that takes a userId and returns the submissions URL with date params
 * - `startDate`: the leaderboard start date (createdAt), or undefined if not loaded
 * - `endDate`: the leaderboard end date (shouldExpireBy), or undefined if not loaded
 *
 * @example
 * ```tsx
 * // For current leaderboard
 * const { getUserSubmissionsUrl, startDate, endDate } = useLeaderboardSubmissionsUrl();
 *
 * // For specific leaderboard
 * const { getUserSubmissionsUrl, startDate, endDate } = useLeaderboardSubmissionsUrl({
 *   leaderboardId: "some-id"
 * });
 *
 * return (
 *   <Link to={getUserSubmissionsUrl(userId)}>View Submissions</Link>
 * );
 * ```
 */
export function useLeaderboardSubmissionsUrl(
  options: { leaderboardId?: string } = {},
) {
  const { leaderboardId } = options;

  const currentLeaderboardQuery = useCurrentLeaderboardMetadataQuery();
  const leaderboardByIdQuery = useLeaderboardMetadataByIdQuery(
    leaderboardId ?? "",
  );

  const metadataData = leaderboardId
    ? leaderboardByIdQuery.data
    : currentLeaderboardQuery.data;

  const startDate =
    metadataData?.success ? metadataData.payload.createdAt : undefined;
  const endDate =
    metadataData?.success
      ? (metadataData.payload.shouldExpireBy ?? undefined)
      : undefined;

  const getUserSubmissionsUrl = useCallback(
    (userId: string) => {
      const params = new URLSearchParams();
      if (startDate) {
        params.set("startDate", startDate);
      }
      if (endDate) {
        params.set("endDate", endDate);
      }
      const queryString = params.toString();
      return `/user/${userId}/submissions${queryString ? `?${queryString}` : ""}`;
    },
    [startDate, endDate],
  );

  return {
    getUserSubmissionsUrl,
    startDate,
    endDate,
  };
}
