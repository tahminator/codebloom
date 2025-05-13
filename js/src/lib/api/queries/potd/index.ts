import { UnknownApiResponse } from "@/lib/api/common/apiResponse";
import { POTD } from "@/lib/api/types/potd";
import { useQuery } from "@tanstack/react-query";

export const useFetchPotdQuery = () => {
  return useQuery({
    queryKey: ["potd", new Date().getDay()],
    queryFn: fetchPotd,
  });
};

async function fetchPotd() {
  const res = await fetch("/api/leetcode/potd");

  const json = (await res.json()) as UnknownApiResponse<POTD>;

  return json;
}
