import { UnknownApiResponse } from "@/lib/api/common/apiResponse";
import { Api } from "@/lib/api/types";
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

  return (await response.json()) as UnknownApiResponse<Api<"AnnouncementDto">>;
}
