import { useQuery } from "@tanstack/react-query";

import { ApiURL } from "../../common/apiURL";

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
