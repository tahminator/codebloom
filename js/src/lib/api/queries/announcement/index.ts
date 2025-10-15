import { ApiURL } from "@/lib/api/common/apiURL";
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
  const { url, method, res } = ApiURL.create("/api/announcement", {
    method: "GET",
  });
  const response = await fetch(url, {
    method,
  });

  return res(await response.json());
}
