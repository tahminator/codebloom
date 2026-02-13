import LeaderboardSkeleton from "@/app/leaderboard/_components/LeaderboardSkeleton.tsx";
import { TestUtils, TestUtilTypes } from "@/lib/test";
import { screen } from "@testing-library/react";

describe("LeaderboardSkeleton succeeded", () => {
  let renderProviderFn: TestUtilTypes.RenderWithAllProvidersFn | null = null;
  beforeEach(() => {
    renderProviderFn = TestUtils.getRenderWithAllProvidersFn();
  });

  it("should render org header when embedded", () => {
    renderProviderFn?.(<LeaderboardSkeleton embedded />);
    const element = screen.getByTestId("leaderboard-skeleton-org-header");
    expect(element).toBeInTheDocument();
    expect(element).toBeVisible();
  });

  it("should render visit codebloom button when embedded", () => {
    renderProviderFn?.(<LeaderboardSkeleton embedded />);
    const element = screen.getByTestId(
      "leaderboard-skeleton-visit-codebloom-button",
    );
    expect(element).toBeInTheDocument();
    expect(element).toBeVisible();
  });

  it("should not render org header when not embedded", () => {
    renderProviderFn?.(<LeaderboardSkeleton />);
    const element = screen.queryByTestId("leaderboard-skeleton-org-header");
    expect(element).not.toBeInTheDocument();
  });

  it("should not render visit codebloom button when not embedded", () => {
    renderProviderFn?.(<LeaderboardSkeleton />);
    const element = screen.queryByTestId(
      "leaderboard-skeleton-visit-codebloom-button",
    );
    expect(element).not.toBeInTheDocument();
  });

  it("should have 200px width podium cards when embedded", () => {
    renderProviderFn?.(<LeaderboardSkeleton embedded />);
    const elements = screen.queryAllByTestId("leaderboard-skeleton-podium");
    for (const element of elements) {
      expect(element.style.getPropertyValue("--skeleton-width")).toContain(
        "12.5rem",
      );
    }
  });

  it("should have 300px width podium cards when not embedded", () => {
    renderProviderFn?.(<LeaderboardSkeleton />);
    const elements = screen.queryAllByTestId("leaderboard-skeleton-podium");
    for (const element of elements) {
      expect(element.style.getPropertyValue("--skeleton-width")).toContain(
        "18.75rem",
      );
    }
  });

  it("should not have filter button skeleton when embedded", () => {
    renderProviderFn?.(<LeaderboardSkeleton embedded />);
    const element = screen.queryByTestId("leaderboard-skeleton-filter-button");
    expect(element).not.toBeInTheDocument();
  });

  it("should have filter button skeleton when not embedded", () => {
    renderProviderFn?.(<LeaderboardSkeleton />);
    const element = screen.getByTestId("leaderboard-skeleton-filter-button");
    expect(element).toBeInTheDocument();
    expect(element).toBeVisible();
  });
});
