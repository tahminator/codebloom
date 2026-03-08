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

const userSubmissionsErrorHandler = http.get(
  userSubmissionsUrl.url.toString(),
  () => HttpResponse.error(),
);

const mockUseUserSubmissionsQuery = vi.fn();

vi.mock("@/lib/api/queries/user", () => ({
  useUserSubmissionsQuery: (...args: unknown[]) =>
    mockUseUserSubmissionsQuery(...args),
}));

const BASE_QUERY_RESULT = {
  status: "pending",
  page: 1,
  goBack: vi.fn(),
  goForward: vi.fn(),
  isPlaceholderData: false,
  goTo: vi.fn(),
  searchQuery: "",
  setSearchQuery: vi.fn(),
  pointFilter: false,
  togglePointFilter: vi.fn(),
  topics: [],
  setTopics: vi.fn(),
  clearTopics: vi.fn(),
  startDate: undefined,
  endDate: undefined,
  setStartDate: vi.fn(),
  setEndDate: vi.fn(),
  data: undefined,
};

describe("UserSubmissions succeeded", () => {
  afterEach(() => {
    cleanup();
    vi.clearAllMocks();
  });

  let renderProviderFn: TestUtilTypes.RenderWithAllProvidersFn | null = null;
  beforeEach(() => {
    renderProviderFn = TestUtils.getRenderWithAllProvidersFn();
    mockUseUserSubmissionsQuery.mockReturnValue(BASE_QUERY_RESULT);
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

  it("should not render DateRangeIndicator when no date range is set", () => {
    mockUseUserSubmissionsQuery.mockReturnValue({
      ...BASE_QUERY_RESULT,
      startDate: undefined,
      endDate: undefined,
    });
    renderProviderFn?.(<UserSubmissions userId={uuid()} />);
    expect(
      screen.queryByTestId("date-range-indicator"),
    ).not.toBeInTheDocument();
  });

  it("should render DateRangeIndicator when startDate is set", () => {
    mockUseUserSubmissionsQuery.mockReturnValue({
      ...BASE_QUERY_RESULT,
      status: "success",
      data: { payload: { items: [], pages: 0, hasNextPage: false } },
      startDate: "2026-01-01",
      endDate: undefined,
    });
    renderProviderFn?.(<UserSubmissions userId={uuid()} />);
    expect(screen.getByTestId("date-range-indicator")).toBeInTheDocument();
  });

  it("should render DateRangeIndicator when endDate is set", () => {
    mockUseUserSubmissionsQuery.mockReturnValue({
      ...BASE_QUERY_RESULT,
      status: "success",
      data: { payload: { items: [], pages: 0, hasNextPage: false } },
      startDate: undefined,
      endDate: "2026-03-01",
    });
    renderProviderFn?.(<UserSubmissions userId={uuid()} />);
    expect(screen.getByTestId("date-range-indicator")).toBeInTheDocument();
  });

  it("should render DateRangeIndicator when both dates are set", () => {
    mockUseUserSubmissionsQuery.mockReturnValue({
      ...BASE_QUERY_RESULT,
      status: "success",
      data: { payload: { items: [], pages: 0, hasNextPage: false } },
      startDate: "2026-01-01",
      endDate: "2026-03-01",
    });
    renderProviderFn?.(<UserSubmissions userId={uuid()} />);
    expect(screen.getByTestId("date-range-indicator")).toBeInTheDocument();
  });
});

describe("UserSubmissions with successful API", () => {
  const server = setupServer(userSubmissionsSuccessHandler);

  beforeAll(() => server.listen());
  afterEach(() => {
    server.resetHandlers();
    cleanup();
    vi.clearAllMocks();
  });
  afterAll(() => server.close());

  let renderProviderFn: TestUtilTypes.RenderWithAllProvidersFn | null = null;
  beforeEach(() => {
    renderProviderFn = TestUtils.getRenderWithAllProvidersFn();

    mockUseUserSubmissionsQuery.mockReturnValue({
      ...BASE_QUERY_RESULT,
      status: "success",
      data: {
        payload: {
          items: MOCK_SUBMISSIONS,
          pages: 1,
          hasNextPage: false,
        },
      },
    });
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
    mockUseUserSubmissionsQuery.mockReturnValue({
      ...BASE_QUERY_RESULT,
      status: "success",
      data: { payload: { items: [], pages: 1, hasNextPage: false } },
    });
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
    vi.clearAllMocks();
  });
  afterAll(() => server.close());

  let renderProviderFn: TestUtilTypes.RenderWithAllProvidersFn | null = null;
  beforeEach(() => {
    renderProviderFn = TestUtils.getRenderWithAllProvidersFn();
    mockUseUserSubmissionsQuery.mockReturnValue({
      ...BASE_QUERY_RESULT,
      status: "error",
      data: undefined,
    });
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
