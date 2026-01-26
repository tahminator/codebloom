import { ApiURL } from "@/lib/api/common/apiURL";
import { useQuery } from "@tanstack/react-query";

export const useFetchPotdEmbedQuery = () => {
  const apiURL = ApiURL.create("/api/leetcode/potd/embed", {
    method: "GET",
  });
  const { queryKey } = apiURL;

  return useQuery({
    queryKey,
    queryFn: async () => fetchPotdEmbed(apiURL),
  });
};

async function fetchPotdEmbed({
  url,
  method,
  res,
}: ApiURL<"/api/leetcode/potd/embed", "get">) {
  const response = await fetch(url, {
    method,
  });

  const json = res(await response.json());

  return json;
}
