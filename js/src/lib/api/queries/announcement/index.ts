import { ApiURL } from "@/lib/api/common/apiURL";
import { useQuery } from "@tanstack/react-query";

/**
 * Fetch the latest announcement, if available.
 */
export const useLatestAnnouncement = () => {
  const apiURL = ApiURL.create("/api/announcement", {
    method: "GET",
  });
  const { queryKey } = apiURL;

  return useQuery({
    queryKey,
    queryFn: () => getLatestAnnouncement(apiURL),
  });
};

async function getLatestAnnouncement({
  url,
  method,
  res,
}: ApiURL<"/api/announcement", "get">) {
  const response = await fetch(url, {
    method,
  });

  return res(await response.json());
}
