export interface LeaderboardMetadata {
  createdAt: string;
  deletedAt: string | null;
  shouldExpireBy: string | null;
}

/**
 * Formats leaderboard metadata into a date range for URL query parameters.
 * Uses full ISO strings to preserve exact timestamps when navigating from leaderboards.
 * @param metadata The leaderboard metadata containing date information
 * @returns An object with startDate and endDate (endDate is undefined if leaderboard is ongoing)
 */
export function formatLeaderboardDateRange(metadata: LeaderboardMetadata) {
  const startDate = new Date(metadata.createdAt).toISOString();
  const endDate =
    metadata.deletedAt ? new Date(metadata.deletedAt).toISOString()
    : metadata.shouldExpireBy ? new Date(metadata.shouldExpireBy).toISOString()
    : undefined;
  return { startDate, endDate };
}

/**
 * Builds a URL to a user's submissions page with optional date range parameters.
 * @param userId The user's ID
 * @param dateRange Optional date range with startDate and optional endDate
 * @returns The URL string for the user's submissions page
 */
export function getUserSubmissionsUrl(
  userId: string,
  dateRange?: { startDate: string; endDate?: string },
) {
  if (dateRange?.startDate) {
    const params = new URLSearchParams({ startDate: dateRange.startDate });
    if (dateRange.endDate) {
      params.set("endDate", dateRange.endDate);
    }
    return `/user/${userId}/submissions?${params.toString()}`;
  }
  return `/user/${userId}/submissions`;
}
