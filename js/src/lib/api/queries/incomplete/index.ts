import { ApiURL } from "@/lib/api/common/apiURL";
import { useQuery } from "@tanstack/react-query";

export const useIncompleteQuestionQuery = () => {
  return useQuery({
    queryKey: ["incomplete"],
    queryFn: fetchIncompleteQuestions,
  });
};

async function fetchIncompleteQuestions() {
  const { url, method, res } = ApiURL.create(
    "/api/admin/questions/incomplete",
    {
      method: "GET",
    },
  );

  const response = await fetch(url, {
    method,
  });

  return res(await response.json());
}
