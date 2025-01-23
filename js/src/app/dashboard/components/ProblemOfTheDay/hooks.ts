import { ApiResponse } from "@/lib/types/apiResponse";
import { POTD } from "@/lib/types/db/potd";
import { useQuery } from "@tanstack/react-query";

export const useFetchPotdQuery = () => {
  return useQuery({
    queryKey: ["potd", new Date().getDay()],
    queryFn: fetchPotd,
  });
};

async function fetchPotd() {
  const res = await fetch("/api/leetcode/potd");

  const json = (await res.json()) as ApiResponse<POTD>;

  if (!json.success) {
    return { ...json, data: null };
  }
  return { ...json, data: json.data };
}
