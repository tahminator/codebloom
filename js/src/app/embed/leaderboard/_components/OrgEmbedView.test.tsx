import {
  currentLeaderboardUsersErrorHandler,
  currentLeaderboardUsersFailedHandler,
  currentLeaderboardUsersHandler,
  failedLeaderboardHandlers,
  successfulLeaderboardHandlers,
} from "@/__mock__/leaderboard";
import OrgEmbedView from "@/app/embed/leaderboard/_components/OrgEmbedView";
import { TestUtils, TestUtilTypes } from "@/lib/test";
import { screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { setupServer } from "msw/node";

describe("OrgEmbedView API Succeeded", () => {
  const server = setupServer(
    currentLeaderboardUsersHandler,
    ...successfulLeaderboardHandlers,
  );

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
    renderProviderFn?.(<OrgEmbedView />);

    const element = screen.getByTestId("leaderboard-skeleton-name");
    expect(element).toBeInTheDocument();
    expect(element).toBeVisible();
  });

  it("should render Visit CodeBloom link after successful API call", async () => {
    renderProviderFn?.(<OrgEmbedView />);

    await waitFor(() => {
      const visitLink = screen.getByRole("link", {
        name: "Visit CodeBloom",
      });
      expect(visitLink).toBeInTheDocument();
      expect(visitLink).toHaveAttribute("href", "/");
      expect(visitLink).toHaveAttribute("target", "_blank");
    });
  });

  it("should render search box after successful API call", async () => {
    renderProviderFn?.(<OrgEmbedView />);

    await waitFor(() => {
      expect(
        screen.queryByTestId("leaderboard-skeleton-name"),
      ).not.toBeInTheDocument();
    });

    const searchBox = screen.getByPlaceholderText("Search for User");
    expect(searchBox).toBeInTheDocument();
    expect(searchBox).toBeVisible();
  });

  it("should render top 3 podium cards after successful API call", async () => {
    renderProviderFn?.(<OrgEmbedView />);

    await waitFor(() => {
      const firstPlace = screen.getByText(/1st/);
      const secondPlace = screen.getByText(/2nd/);
      const thirdPlace = screen.getByText(/3rd/);

      expect(firstPlace).toBeInTheDocument();
      expect(secondPlace).toBeInTheDocument();
      expect(thirdPlace).toBeInTheDocument();
    });
  });

  it("should render user scores in podium cards", async () => {
    renderProviderFn?.(<OrgEmbedView />);

    await waitFor(() => {
      expect(screen.getByText(/120 Points/)).toBeInTheDocument();
      expect(screen.getByText(/110 Points/)).toBeInTheDocument();
      expect(screen.getByText(/100 Points/)).toBeInTheDocument();
    });
  });

  it("should render user cards with discord names", async () => {
    renderProviderFn?.(<OrgEmbedView />);

    await waitFor(() => {
      const aphroditeEntries = screen.getAllByText(/aphrodite/);
      expect(aphroditeEntries.length).toBeGreaterThan(0);

      const poseidonEntries = screen.getAllByText(/poseidon/);
      expect(poseidonEntries.length).toBeGreaterThan(0);

      const hermesEntries = screen.getAllByText(/hermes/);
      expect(hermesEntries.length).toBeGreaterThan(0);
    });
  });

  it("should render user cards as links with correct attributes", async () => {
    renderProviderFn?.(<OrgEmbedView />);

    await waitFor(() => {
      expect(
        screen.queryByTestId("leaderboard-skeleton-name"),
      ).not.toBeInTheDocument();
    });

    const userLinks = screen
      .getAllByRole("link")
      .filter((link) => link.getAttribute("href")?.includes("/user/"));

    expect(userLinks.length).toBeGreaterThan(0);
    userLinks.forEach((link) => {
      expect(link).toHaveAttribute("target", "_blank");
      expect(link).toHaveAttribute("rel", "noopener noreferrer");
    });
  });

  it("should include startDate and endDate in links when metadata is available", async () => {
    renderProviderFn?.(<OrgEmbedView />);

    await waitFor(() => {
      const aphroditeEntries = screen.getAllByText("aphrodite");
      expect(aphroditeEntries.length).toBeGreaterThan(0);

      const aphroditeLink = aphroditeEntries[0].closest("a");
      expect(aphroditeLink).toHaveAttribute(
        "href",
        expect.stringContaining("/user/user-1/submissions?startDate="),
      );
      expect(aphroditeLink).toHaveAttribute(
        "href",
        expect.stringContaining("endDate="),
      );
    });
  });

  it("should omit date range in links when metadata is unavailable", async () => {
    server.use(...failedLeaderboardHandlers);

    renderProviderFn?.(<OrgEmbedView />);

    await waitFor(() => {
      const aphroditeEntries = screen.getAllByText("aphrodite");
      expect(aphroditeEntries.length).toBeGreaterThan(0);

      const aphroditeLink = aphroditeEntries[0].closest("a");
      expect(aphroditeLink).toHaveAttribute("href", "/user/user-1/submissions");
    });
  });

  it("should allow typing in search box", async () => {
    const user = userEvent.setup();
    renderProviderFn?.(<OrgEmbedView />);

    await waitFor(() => {
      expect(
        screen.queryByTestId("leaderboard-skeleton-name"),
      ).not.toBeInTheDocument();
    });

    const searchBox = screen.getByPlaceholderText("Search for User");
    await user.type(searchBox, "test");

    expect(searchBox).toHaveValue("test");
  });

  it("should filter users based on search input", async () => {
    const user = userEvent.setup();
    renderProviderFn?.(<OrgEmbedView />);

    await waitFor(() => {
      expect(
        screen.queryByTestId("leaderboard-skeleton-name"),
      ).not.toBeInTheDocument();
    });

    expect(screen.getAllByText(/aphrodite/)[0]).toBeInTheDocument();
    expect(screen.getAllByText(/poseidon/)[0]).toBeInTheDocument();
    expect(screen.getAllByText(/hermes/)[0]).toBeInTheDocument();

    const searchBox = screen.getByPlaceholderText("Search for User");
    await user.type(searchBox, "aphrodite");

    await waitFor(
      () => {
        expect(screen.getAllByText(/aphrodite/).length).toBeGreaterThan(0);
        expect(screen.queryAllByText(/poseidon/).length).toBe(0);
        expect(screen.queryAllByText(/hermes/).length).toBe(0);
      },
      { timeout: 1000 },
    );
  });
});

describe("OrgEmbedView API Failed", () => {
  const server = setupServer(
    currentLeaderboardUsersFailedHandler,
    ...successfulLeaderboardHandlers,
  );

  beforeAll(() => server.listen());
  afterEach(() => {
    server.resetHandlers();
  });
  afterAll(() => server.close());

  let renderProviderFn: TestUtilTypes.RenderWithAllProvidersFn | null = null;
  beforeEach(() => {
    renderProviderFn = TestUtils.getRenderWithAllProvidersFn();
  });

  it("should render empty state when users response is unsuccessful", async () => {
    renderProviderFn?.(<OrgEmbedView />);

    await waitFor(() => {
      const emptyMessage = screen.getByText(
        "Sorry, there are no users to display.",
      );
      expect(emptyMessage).toBeInTheDocument();
      expect(emptyMessage).toBeVisible();
    });
  });
});

describe("OrgEmbedView API Crashed", () => {
  const server = setupServer(
    currentLeaderboardUsersErrorHandler,
    ...successfulLeaderboardHandlers,
  );

  beforeAll(() => server.listen());
  afterEach(() => {
    server.resetHandlers();
  });
  afterAll(() => server.close());

  let renderProviderFn: TestUtilTypes.RenderWithAllProvidersFn | null = null;
  beforeEach(() => {
    renderProviderFn = TestUtils.getRenderWithAllProvidersFn();
  });

  it("should render error state when users request fails", async () => {
    renderProviderFn?.(<OrgEmbedView />);

    await waitFor(() => {
      const errorMessage = screen.getByText("Sorry, something went wrong.");
      expect(errorMessage).toBeInTheDocument();
      expect(errorMessage).toBeVisible();
    });
  });
});
