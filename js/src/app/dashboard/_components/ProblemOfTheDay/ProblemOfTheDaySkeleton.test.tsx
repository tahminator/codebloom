import ProblemOfTheDaySkeleton from "@/app/dashboard/_components/ProblemOfTheDay/ProblemOfTheDaySkeleton";
import { TestUtils, TestUtilTypes } from "@/lib/test";
import { screen } from "@testing-library/react";

describe("ProblemOfTheDaySkeleton", () => {
  let renderProviderFn: TestUtilTypes.RenderWithAllProvidersFn | null = null;

  beforeEach(() => {
    renderProviderFn = TestUtils.getRenderWithAllProvidersFn();
  });

  it("should render POTD title skeleton", () => {
    renderProviderFn?.(<ProblemOfTheDaySkeleton />);
    const element = screen.getByTestId("potd-skeleton-title");
    expect(element).toBeInTheDocument();
    expect(element).toBeVisible();
  });

  it("should render POTD reset time skeleton", () => {
    renderProviderFn?.(<ProblemOfTheDaySkeleton />);
    const element = screen.getByTestId("potd-skeleton-reset-time");
    expect(element).toBeInTheDocument();
    expect(element).toBeVisible();
  });

  it("should render problem title skeleton", () => {
    renderProviderFn?.(<ProblemOfTheDaySkeleton />);
    const element = screen.getByTestId("potd-skeleton-problem-title");
    expect(element).toBeInTheDocument();
    expect(element).toBeVisible();
  });

  it("should render problem multiplier skeleton", () => {
    renderProviderFn?.(<ProblemOfTheDaySkeleton />);
    const element = screen.getByTestId("potd-skeleton-problem-multiplier");
    expect(element).toBeInTheDocument();
    expect(element).toBeVisible();
  });

  it("should render problem link button skeleton", () => {
    renderProviderFn?.(<ProblemOfTheDaySkeleton />);
    const element = screen.getByTestId("potd-skeleton-problem-link-button");
    expect(element).toBeInTheDocument();
    expect(element).toBeVisible();
  });
});
