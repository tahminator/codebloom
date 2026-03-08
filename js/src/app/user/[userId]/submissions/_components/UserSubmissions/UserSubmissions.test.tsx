import UserSubmissions from "@/app/user/[userId]/submissions/_components/UserSubmissions/UserSubmissions.tsx";
import { TestUtils, TestUtilTypes } from "@/lib/test";
import { screen, cleanup } from "@testing-library/react";
import { v4 as uuid } from "uuid";

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

describe("UserSubmissions", () => {
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
