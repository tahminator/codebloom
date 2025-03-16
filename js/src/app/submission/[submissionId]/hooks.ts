import { ApiResponse } from "@/lib/types/apiResponse";
import { Question } from "@/lib/types/db/question";
import { User } from "@/lib/types/db/user";
import { useQuery } from "@tanstack/react-query";

export const useSubmissionDetailsQuery = ({
  submissionId,
}: {
  submissionId?: string;
}) => {
  return useQuery({
    queryKey: ["submission", "s", submissionId],
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

  if (json.success) {
    return { data: json.data, message: json.message };
  }

  return { data: null, message: json.message };
}
