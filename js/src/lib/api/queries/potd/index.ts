import { ApiURL } from "@/lib/api/common/apiURL";
import { useQuery } from "@tanstack/react-query";

export const useFetchPotdQuery = () => {
  return useQuery({
    queryKey: ["potd", new Date().getDay()],
    queryFn: fetchPotd,
  });
};

async function fetchPotd() {
  const { url, method, res } = ApiURL.create("/api/leetcode/potd", {
    method: "GET",
  });
  const response = await fetch(url, {
    method,
  });

  const json = res(await response.json());

  return json;
}
