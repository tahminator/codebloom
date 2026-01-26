import { ApiURL } from "@/lib/api/common/apiURL";
import { useQuery } from "@tanstack/react-query";

export const useIncompleteQuestionQuery = () => {
  const apiURL = ApiURL.create("/api/admin/questions/incomplete", {
    method: "GET",
  });
  const { queryKey } = apiURL;

  return useQuery({
    queryKey,
    queryFn: () => fetchIncompleteQuestions(apiURL),
  });
};

async function fetchIncompleteQuestions({
  url,
  method,
  res,
}: ApiURL<"/api/admin/questions/incomplete", "get">) {
  const response = await fetch(url, {
    method,
  });

  return res(await response.json());
}
