import { ApiResponse } from "@/lib/api/common/apiResponse";
import { Question } from "@/lib/api/types/question";
import { User } from "@/lib/api/types/user";
import { useQuery } from "@tanstack/react-query";

/**
 * Fetch the details of a submission
 */
export const useSubmissionDetailsQuery = ({
  submissionId,
}: {
  submissionId?: string;
}) => {
  return useQuery({
    queryKey: ["submission", submissionId],
    queryFn: () => fetchSubmissionDetails({ submissionId }),
  });
};

async function fetchSubmissionDetails({
  submissionId,
}: {
  submissionId?: string;
}) {
  const res = await fetch(`/api/leetcode/submission/${submissionId}`);
  const json = (await res.json()) as ApiResponse<
    Question & Pick<User, "discordName" | "leetcodeUsername">
  >;

  return json;
}
