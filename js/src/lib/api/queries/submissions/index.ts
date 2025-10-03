import { UnknownApiResponse } from "@/lib/api/common/apiResponse";
import { Api } from "@/lib/api/types";
import { Question } from "@/lib/api/types/question";
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
  const json = (await res.json()) as UnknownApiResponse<
    Question &
      Pick<Api<"UserDto">, "discordName" | "leetcodeUsername" | "nickname">
  >;

  return json;
}
