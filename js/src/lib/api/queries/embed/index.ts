import { ApiURL } from "@/lib/api/common/apiURL";
import { useQuery } from "@tanstack/react-query";

export const useFetchPotdEmbedQuery = () => {
  return useQuery({
    queryKey: ["potd", new Date().toDateString()],
    queryFn: fetchPotdEmbed,
  });
};

async function fetchPotdEmbed() {
  const { url, method, res } = ApiURL.create("/api/leetcode/potd/embed", {
    method: "GET",
  });
  const response = await fetch(url, {
    method,
  });

  const json = res(await response.json());

  return json;
}
