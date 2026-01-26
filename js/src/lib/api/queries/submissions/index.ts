import { ApiURL } from "@/lib/api/common/apiURL";
import { useQuery } from "@tanstack/react-query";

/**
 * Fetch the details of a submission
 */
export const useSubmissionDetailsQuery = ({
  submissionId,
}: {
  submissionId: string;
}) => {
  const apiURL = ApiURL.create("/api/leetcode/submission/{submissionId}", {
    method: "GET",
    params: {
      submissionId,
    },
  });

  const { queryKey } = apiURL;

  return useQuery({
    queryKey,
    queryFn: () => fetchSubmissionDetails(apiURL),
  });
};

async function fetchSubmissionDetails({
  url,
  method,
  res,
}: ApiURL<"/api/leetcode/submission/{submissionId}", "get">) {
  const response = await fetch(url, {
    method,
  });
  const json = res(await response.json());

  return json;
}
