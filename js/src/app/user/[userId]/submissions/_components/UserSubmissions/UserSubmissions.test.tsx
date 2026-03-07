import UserSubmissions from "@/app/user/[userId]/submissions/_components/UserSubmissions/UserSubmissions.tsx";
import { ApiURL } from "@/lib/api/common/apiURL";
import { LeetcodeTopicEnum, QuestionDifficulty } from "@/lib/api/types/schema";
import { TestUtils, TestUtilTypes } from "@/lib/test";
import { cleanup, screen, waitFor } from "@testing-library/react";
import { http, HttpResponse } from "msw";
import { setupServer } from "msw/node";
import { v4 as uuid } from "uuid";

const FIXED_USER_ID = "fixed-submissions-test-user";

const userSubmissionsUrl = ApiURL.create("/api/user/{userId}/submissions", {
  method: "GET",
  params: { userId: FIXED_USER_ID },
});

const MOCK_SUBMISSIONS = [
  {
    id: "sub-1",
    userId: FIXED_USER_ID,
    questionSlug: "two-sum",
    questionTitle: "Two Sum",
    questionNumber: 1,
    questionLink: "https://leetcode.com/problems/two-sum",
    questionDifficulty: QuestionDifficulty.Easy,
    description: null,
    pointsAwarded: 10,
    acceptanceRate: 0.8,
    createdAt: new Date().toISOString(),
    submittedAt: new Date().toISOString(),
    runtime: "50ms",
    memory: "16MB",
    code: "def solution(): pass",
    language: "python3",
    submissionId: null,
    topics: [],
  },
  {
    id: "sub-2",
    userId: FIXED_USER_ID,
    questionSlug: "add-two-numbers",
    questionTitle: "Add Two Numbers",
    questionNumber: 2,
    questionLink: "https://leetcode.com/problems/add-two-numbers",
    questionDifficulty: QuestionDifficulty.Medium,
    description: null,
    pointsAwarded: 15,
    acceptanceRate: 0.6,
    createdAt: new Date().toISOString(),
    submittedAt: new Date().toISOString(),
    runtime: null,
    memory: null,
    code: null,
    language: "java",
    submissionId: null,
    topics: [
      {
        id: "topic-1",
        questionId: "sub-2",
        topicSlug: "array",
        topic: LeetcodeTopicEnum.ARRAY,
        createdAt: new Date().toISOString(),
      },
    ],
  },
  {
    id: "sub-3",
    userId: FIXED_USER_ID,
    questionSlug: "median-of-two-sorted-arrays",
    questionTitle: "Median of Two Sorted Arrays",
    questionNumber: 4,
    questionLink: "https://leetcode.com/problems/median-of-two-sorted-arrays",
    questionDifficulty: QuestionDifficulty.Hard,
    description: null,
    pointsAwarded: 25,
    acceptanceRate: 0.38,
    createdAt: new Date().toISOString(),
    submittedAt: new Date().toISOString(),
    runtime: null,
    memory: null,
    code: null,
    language: "cpp",
    submissionId: null,
    topics: [],
  },
];

const userSubmissionsSuccessHandler = http.get(
  userSubmissionsUrl.url.toString(),
  () =>
    HttpResponse.json({
      success: true,
      message: "Submissions loaded!",
      payload: {
        hasNextPage: false,
        pages: 1,
        items: MOCK_SUBMISSIONS,
      },
    }),
);

const userSubmissionsEmptyHandler = http.get(
  userSubmissionsUrl.url.toString(),
  () =>
    HttpResponse.json({
      success: true,
      message: "Submissions loaded!",
      payload: {
        hasNextPage: false,
        pages: 1,
        items: [],
      },
    }),
);

const userSubmissionsErrorHandler = http.get(
  userSubmissionsUrl.url.toString(),
  () => HttpResponse.error(),
);

describe("UserSubmissions succeeded", () => {
  afterEach(() => {
    cleanup();
  });

  let renderProviderFn: TestUtilTypes.RenderWithAllProvidersFn | null = null;
  beforeEach(() => {
    renderProviderFn = TestUtils.getRenderWithAllProvidersFn();
  });

  it("should render skeleton stack of submissions initially", () => {
    const MOCK_USER_ID = uuid();
    renderProviderFn?.(<UserSubmissions userId={MOCK_USER_ID} />);
    const element = screen.getByTestId(
      "user-profile-skeleton-submissions-stack",
    );
    expect(element).toBeInTheDocument();
    expect(element).toBeVisible();
  });
});

describe("UserSubmissions with successful API", () => {
  const server = setupServer(userSubmissionsSuccessHandler);

  beforeAll(() => server.listen());
  afterEach(() => {
    server.resetHandlers();
    cleanup();
  });
  afterAll(() => server.close());

  let renderProviderFn: TestUtilTypes.RenderWithAllProvidersFn | null = null;
  beforeEach(() => {
    renderProviderFn = TestUtils.getRenderWithAllProvidersFn();
  });

  it("should render submission titles after successful API call", async () => {
    renderProviderFn?.(<UserSubmissions userId={FIXED_USER_ID} />);

    await waitFor(() => {
      expect(screen.getByText("Two Sum")).toBeInTheDocument();
      expect(screen.getByText("Add Two Numbers")).toBeInTheDocument();
      expect(
        screen.getByText("Median of Two Sorted Arrays"),
      ).toBeInTheDocument();
    });
  });

  it("should render Easy badge for easy questions", async () => {
    renderProviderFn?.(<UserSubmissions userId={FIXED_USER_ID} />);

    await waitFor(() => {
      expect(screen.getByText("Easy")).toBeInTheDocument();
    });
  });

  it("should render Medium badge for medium questions", async () => {
    renderProviderFn?.(<UserSubmissions userId={FIXED_USER_ID} />);

    await waitFor(() => {
      expect(screen.getByText("Medium")).toBeInTheDocument();
    });
  });

  it("should render Hard badge for hard questions", async () => {
    renderProviderFn?.(<UserSubmissions userId={FIXED_USER_ID} />);

    await waitFor(() => {
      expect(screen.getByText("Hard")).toBeInTheDocument();
    });
  });

  it("should render acceptance rate badges", async () => {
    renderProviderFn?.(<UserSubmissions userId={FIXED_USER_ID} />);

    await waitFor(() => {
      expect(screen.getByText("80%")).toBeInTheDocument();
      expect(screen.getByText("60%")).toBeInTheDocument();
      expect(screen.getByText("38%")).toBeInTheDocument();
    });
  });

  it("should render search box", async () => {
    renderProviderFn?.(<UserSubmissions userId={FIXED_USER_ID} />);

    await waitFor(() => {
      expect(
        screen.getByPlaceholderText("Search for submission title"),
      ).toBeInTheDocument();
    });
  });

  it("should render points for submissions with topics", async () => {
    renderProviderFn?.(<UserSubmissions userId={FIXED_USER_ID} />);

    await waitFor(() => {
      expect(screen.getByText("15 Pts")).toBeInTheDocument();
    });
  });

  it("should show Nothing found when empty", async () => {
    server.use(userSubmissionsEmptyHandler);
    renderProviderFn?.(<UserSubmissions userId={FIXED_USER_ID} />);

    await waitFor(() => {
      expect(screen.getByText("Nothing found.")).toBeInTheDocument();
    });
  });
});

describe("UserSubmissions error state", () => {
  const server = setupServer(userSubmissionsErrorHandler);

  beforeAll(() => server.listen());
  afterEach(() => {
    server.resetHandlers();
    cleanup();
  });
  afterAll(() => server.close());

  let renderProviderFn: TestUtilTypes.RenderWithAllProvidersFn | null = null;
  beforeEach(() => {
    renderProviderFn = TestUtils.getRenderWithAllProvidersFn();
  });

  it("should show error toast when API errors", async () => {
    renderProviderFn?.(<UserSubmissions userId={FIXED_USER_ID} />);

    await waitFor(() => {
      expect(
        screen.getByText(
          /sorry, something went wrong when trying to fetch user's submissions/i,
        ),
      ).toBeInTheDocument();
    });
  });
});
