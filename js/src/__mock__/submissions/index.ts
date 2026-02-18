import { ApiURL } from "@/lib/api/common/apiURL";
import { QuestionDifficulty } from "@/lib/api/types/schema";
import { http, HttpResponse } from "msw";

export const MOCK_SUBMISSION_ID = "submission-1";

const submissionDetails = ApiURL.create(
  "/api/leetcode/submission/{submissionId}",
  {
    method: "GET",
    params: {
      submissionId: MOCK_SUBMISSION_ID,
    },
  },
);

export const submissionDetailsHandler = http.get(
  submissionDetails.url.toString(),
  () => {
    return HttpResponse.json({
      success: true,
      message: "Submission details loaded!",
      payload: {
        id: MOCK_SUBMISSION_ID,
        userId: "user-1",
        questionSlug: "two-sum",
        questionNumber: 1,
        questionTitle: "Two Sum",
        questionLink: "https://leetcode.com/problems/two-sum",
        questionDifficulty: QuestionDifficulty.Easy,
        description: "<p>Given an array of integers, return indices.</p>",
        acceptanceRate: 0.49,
        pointsAwarded: 10,
        discordName: "aphrodite",
        nickname: null,
        code: "def twoSum(nums, target):\n    pass",
        runtime: "50ms",
        memory: "16MB",
        language: "python3",
        createdAt: "",
        submittedAt: "",
        submissionId: null,
        topics: [],
        leetcodeUsername: null,
      },
    } satisfies ReturnType<typeof submissionDetails.res>);
  },
);

export const submissionDetailsProcessingHandler = http.get(
  submissionDetails.url.toString(),
  () => {
    return HttpResponse.json({
      success: true,
      message: "Submission details loaded!",
      payload: {
        id: MOCK_SUBMISSION_ID,
        userId: "user-1",
        questionSlug: "two-sum",
        questionNumber: 1,
        questionTitle: "Two Sum",
        questionLink: "https://leetcode.com/problems/two-sum",
        questionDifficulty: QuestionDifficulty.Easy,
        description: "<p>Given an array of integers, return indices.</p>",
        acceptanceRate: 0.49,
        pointsAwarded: 10,
        discordName: "aphrodite",
        nickname: null,
        code: null,
        runtime: null,
        memory: null,
        language: null,
        createdAt: "",
        submittedAt: "",
        submissionId: null,
        topics: [],
        leetcodeUsername: null,
      },
    } satisfies ReturnType<typeof submissionDetails.res>);
  },
);

export const submissionDetailsFailedHandler = http.get(
  submissionDetails.url.toString(),
  () => {
    return HttpResponse.json({
      success: false,
      message: "Submission not found.",
    } satisfies ReturnType<typeof submissionDetails.res>);
  },
);

export const submissionDetailsErrorHandler = http.get(
  submissionDetails.url.toString(),
  () => {
    return HttpResponse.error();
  },
);
