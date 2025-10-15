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
  return useQuery({
    queryKey: ["submission", submissionId],
    queryFn: () => fetchSubmissionDetails({ submissionId }),
  });
};

async function fetchSubmissionDetails({
  submissionId,
}: {
  submissionId: string;
}) {
  const { url, method, res } = ApiURL.create(
    "/api/leetcode/submission/{submissionId}",
    {
      method: "GET",
      params: {
        submissionId,
      },
    },
  );
  const response = await fetch(url, {
    method,
  });
  const json = res(await response.json());

  return json;
}
