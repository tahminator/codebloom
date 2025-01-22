import { ApiResponse } from "@/lib/types/apiResponse";
import { Question } from "@/lib/types/db/question";
import { useQuery } from "@tanstack/react-query";

export const useRecentSubmissionsQuery = ({ start = 0, end = 5 }) => {
  return useQuery({
    queryKey: ["submission", "me", "start", start, "end", end],
    queryFn: () => getRecentSubmissions({ start, end }),
  });
};

async function getRecentSubmissions({
  start,
  end,
}: {
  start: number;
  end: number;
}) {
  const res = await fetch(`/api/leetcode/all?start=${start}&end=${end}`);

  const json = (await res.json()) as ApiResponse<Question[]>;

  if (json.success) {
    return { ...json, data: json.data };
  }

  return { ...json, data: null };
}
