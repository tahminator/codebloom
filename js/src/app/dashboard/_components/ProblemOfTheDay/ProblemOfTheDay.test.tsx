import {
  potdErrorHandler,
  potdFailedHandler,
  potdHandler,
} from "@/__mock__/potd";
import ProblemOfTheDay from "@/app/dashboard/_components/ProblemOfTheDay/ProblemOfTheDay";
import { TestUtils, TestUtilTypes } from "@/lib/test";
import { screen, waitFor } from "@testing-library/react";
import { setupServer } from "msw/node";

describe("ProblemOfTheDay API Succeeded", () => {
  const server = setupServer(potdHandler);

  beforeAll(() => server.listen());
  afterEach(() => {
    server.resetHandlers();
  });
  afterAll(() => server.close());

  let renderProviderFn: TestUtilTypes.RenderWithAllProvidersFn | null = null;
  beforeEach(() => {
    renderProviderFn = TestUtils.getRenderWithAllProvidersFn();
  });

  it("should render loading state initially", () => {
    renderProviderFn?.(<ProblemOfTheDay />);

    const element = screen.getByTestId("potd-skeleton-title");
    expect(element).toBeInTheDocument();
    expect(element).toBeVisible();
  });

  it("should render 'Problem of the day' heading after successful API call", async () => {
    renderProviderFn?.(<ProblemOfTheDay />);

    await waitFor(() => {
      expect(screen.getByText("Problem of the day")).toBeInTheDocument();
    });
  });

  it("should render reset time notice after successful API call", async () => {
    renderProviderFn?.(<ProblemOfTheDay />);

    await waitFor(() => {
      expect(
        screen.getByText("POTD resets at 8:00 EDT everyday"),
      ).toBeInTheDocument();
    });
  });

  it("should render problem title after successful API call", async () => {
    renderProviderFn?.(<ProblemOfTheDay />);

    await waitFor(() => {
      expect(screen.getByText("Two Sum")).toBeInTheDocument();
    });
  });

  it("should render multiplier badge after successful API call", async () => {
    renderProviderFn?.(<ProblemOfTheDay />);

    await waitFor(() => {
      expect(screen.getByText(/2x multiplier/i)).toBeInTheDocument();
    });
  });

  it("should render 'Go to question' link with correct href after successful API call", async () => {
    renderProviderFn?.(<ProblemOfTheDay />);

    await waitFor(() => {
      const link = screen.getByRole("link", { name: /go to question/i });
      expect(link).toBeInTheDocument();
      expect(link).toHaveAttribute(
        "href",
        "https://leetcode.com/problems/two-sum",
      );
    });
  });

  it("should open 'Go to question' link in a new tab", async () => {
    renderProviderFn?.(<ProblemOfTheDay />);

    await waitFor(() => {
      const link = screen.getByRole("link", { name: /go to question/i });
      expect(link).toHaveAttribute("target", "_blank");
      expect(link).toHaveAttribute("rel", "noopener noreferrer");
    });
  });

  it("should not render skeleton after successful API call", async () => {
    renderProviderFn?.(<ProblemOfTheDay />);

    await waitFor(() => {
      expect(
        screen.queryByTestId("potd-skeleton-title"),
      ).not.toBeInTheDocument();
    });
  });

  it("should render error message when API call fails", async () => {
    server.use(potdErrorHandler);
    renderProviderFn?.(<ProblemOfTheDay />);

    await waitFor(() => {
      expect(
        screen.getByText(/sorry, something went wrong/i),
      ).toBeInTheDocument();
    });
  });

  it("should render failed message when no POTD is available", async () => {
    server.use(potdFailedHandler);
    renderProviderFn?.(<ProblemOfTheDay />);

    await waitFor(() => {
      expect(
        screen.getByText("No problem of the day available."),
      ).toBeInTheDocument();
    });
  });
});
