import { UnknownApiResponse } from "@/lib/api/common/apiResponse";
import { Announcement } from "@/lib/api/types/announcement";
import { useQuery } from "@tanstack/react-query";

/**
 * Fetch the latest announcement, if available.
 */
export const useLatestAnnouncement = () => {
  return useQuery({
    queryKey: ["announcement"],
    queryFn: getLatestAnnouncement,
  });
};

async function getLatestAnnouncement() {
  const response = await fetch("/api/announcement");

  return (await response.json()) as UnknownApiResponse<Announcement | null>;
}
