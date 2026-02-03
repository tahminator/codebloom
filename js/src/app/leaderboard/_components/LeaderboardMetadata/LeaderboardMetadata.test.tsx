import {
  catastrophicLeaderboardHandlers,
  currentMetadataNoSyntaxHighlightingHandler,
  failedLeaderboardHandlers,
  metadataByIdNoSyntaxHighlightingHandler,
  MOCK_LEADERBOARD_ID,
  successfulLeaderboardHandlers,
} from "@/__mock__/leaderboard";
import {
  CurrentLeaderboardMetadata,
  LeaderboardMetadataById,
} from "@/app/leaderboard/_components/LeaderboardMetadata/LeaderboardMetadata";
import { TestUtils, TestUtilTypes } from "@/lib/test";
import { screen, waitFor } from "@testing-library/react";
import { setupServer } from "msw/node";

describe("CurrentLeaderboardMetadata API Succeeded", () => {
  const server = setupServer(...successfulLeaderboardHandlers);

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
    renderProviderFn?.(<CurrentLeaderboardMetadata />);

    const element = screen.getByTestId("leaderboard-skeleton-name");
    expect(element).toBeInTheDocument();
    expect(element).toBeVisible();
  });

  it("should render leaderboard name after successful API call", async () => {
    renderProviderFn?.(<CurrentLeaderboardMetadata />);

    await waitFor(() => {
      const heading = screen.getByTestId("LeaderboardMetadata-title");
      expect(heading).toHaveTextContent('std::string november = "hello world"');
      expect(heading).toBeVisible();
    });
  });

  it("should render leaderboard title with syntax highlighting", async () => {
    renderProviderFn?.(<CurrentLeaderboardMetadata />);

    await waitFor(() => {
      const titleElement = screen.getByTestId("shiki-container");
      expect(titleElement).toBeInTheDocument();
      expect(titleElement).toBeVisible();
    });
  });

  it("should render leaderboard title without syntax highlighting", async () => {
    server.use(currentMetadataNoSyntaxHighlightingHandler);

    renderProviderFn?.(<CurrentLeaderboardMetadata />);

    await waitFor(() => {
      const titleElement = screen.queryByTestId("shiki-container");
      expect(titleElement).not.toBeInTheDocument();
    });

    await waitFor(() => {
      const titleElement = screen.queryByText("haiii");
      expect(titleElement).toBeInTheDocument();
      expect(titleElement).toBeVisible();
    });
  });

  it("should render clock in leaderboard if prop is enabled", async () => {
    renderProviderFn?.(<CurrentLeaderboardMetadata showClock />);

    await waitFor(() => {
      const counter = screen.getByTestId("PrettyCounter");
      expect(counter).toBeInTheDocument();
      expect(counter).toBeVisible();
    });
  });

  it("should not render clock in leaderboard if prop is disabled", async () => {
    renderProviderFn?.(<CurrentLeaderboardMetadata />);

    await waitFor(() => {
      expect(
        screen.queryByTestId("leaderboard-skeleton-name"),
      ).not.toBeInTheDocument();
    });

    const counter = screen.queryByTestId("PrettyCounter");
    expect(counter).not.toBeInTheDocument();
  });

  it("should render Show All Leaderboards button in leaderboard if prop is enabled", async () => {
    renderProviderFn?.(<CurrentLeaderboardMetadata showAllLeaderboardButton />);

    await waitFor(() => {
      const button = screen.getByTestId("ShowAllLeaderboardsButton");
      expect(button).toBeInTheDocument();
      expect(button).toBeVisible();
      expect(button).toHaveAttribute("href", "/leaderboard/all");
    });
  });

  it("should not render Show All Leaderboards button in leaderboard if prop is disabled", async () => {
    renderProviderFn?.(<CurrentLeaderboardMetadata />);

    await waitFor(() => {
      expect(
        screen.queryByTestId("leaderboard-skeleton-name"),
      ).not.toBeInTheDocument();
    });

    const button = screen.queryByTestId("ShowAllLeaderboardsButton");
    expect(button).not.toBeInTheDocument();
  });

  it("should set document title after successful API call", async () => {
    renderProviderFn?.(<CurrentLeaderboardMetadata />);

    await waitFor(() => {
      expect(document.title).toBe(
        'CodeBloom - std::string november = "hello world"',
      );
    });
  });

  it("should set document description after successful API call", async () => {
    renderProviderFn?.(<CurrentLeaderboardMetadata />);

    await waitFor(() => {
      const metaDescription = document.querySelector(
        'meta[name="description"]',
      );
      expect(metaDescription).toBeInTheDocument();
      expect(metaDescription).toHaveAttribute(
        "content",
        "CodeBloom - View your rank in the leaderboard",
      );
    });
  });
});

describe("CurrentLeaderboardMetadata API Failed", () => {
  const server = setupServer(...failedLeaderboardHandlers);

  beforeAll(() => server.listen());
  afterEach(() => {
    server.resetHandlers();
  });
  afterAll(() => server.close());

  let renderProviderFn: TestUtilTypes.RenderWithAllProvidersFn | null = null;
  beforeEach(() => {
    renderProviderFn = TestUtils.getRenderWithAllProvidersFn();
  });

  it("should render loading state initially", async () => {
    renderProviderFn?.(<CurrentLeaderboardMetadata />);

    await waitFor(() => {
      const element = screen.getByText("Leaderboard metadata failed to load");
      expect(element).toBeInTheDocument();
      expect(element).toBeVisible();
    });
  });
});

describe("CurrentLeaderboardMetadata API Crashed", () => {
  const server = setupServer(...catastrophicLeaderboardHandlers);

  beforeAll(() => server.listen());
  afterEach(() => {
    server.resetHandlers();
  });
  afterAll(() => server.close());

  let renderProviderFn: TestUtilTypes.RenderWithAllProvidersFn | null = null;
  beforeEach(() => {
    renderProviderFn = TestUtils.getRenderWithAllProvidersFn();
  });

  it("should render loading state initially", async () => {
    renderProviderFn?.(<CurrentLeaderboardMetadata />);

    await waitFor(() => {
      const element = screen.getByText("Sorry, something went wrong.");
      expect(element).toBeInTheDocument();
      expect(element).toBeVisible();
    });
  });
});

describe("LeaderboardMetadataById API Succeeded", () => {
  const server = setupServer(...successfulLeaderboardHandlers);

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
    renderProviderFn?.(
      <LeaderboardMetadataById leaderboardId={MOCK_LEADERBOARD_ID} />,
    );

    const element = screen.getByTestId("leaderboard-skeleton-name");
    expect(element).toBeInTheDocument();
    expect(element).toBeVisible();
  });

  it("should render leaderboard name after successful API call", async () => {
    renderProviderFn?.(
      <LeaderboardMetadataById leaderboardId={MOCK_LEADERBOARD_ID} />,
    );

    await waitFor(() => {
      const heading = screen.getByTestId("LeaderboardMetadata-title");
      expect(heading).toHaveTextContent('std::string november = "hello world"');
      expect(heading).toBeVisible();
    });
  });

  it("should render leaderboard title with syntax highlighting", async () => {
    renderProviderFn?.(
      <LeaderboardMetadataById leaderboardId={MOCK_LEADERBOARD_ID} />,
    );

    await waitFor(() => {
      const titleElement = screen.getByTestId("shiki-container");
      expect(titleElement).toBeInTheDocument();
      expect(titleElement).toBeVisible();
    });
  });

  it("should render leaderboard title without syntax highlighting", async () => {
    server.use(metadataByIdNoSyntaxHighlightingHandler);

    renderProviderFn?.(
      <LeaderboardMetadataById leaderboardId={MOCK_LEADERBOARD_ID} />,
    );

    await waitFor(() => {
      const titleElement = screen.queryByTestId("shiki-container");
      expect(titleElement).not.toBeInTheDocument();
    });

    await waitFor(() => {
      const titleElement = screen.queryByText("haiii");
      expect(titleElement).toBeInTheDocument();
      expect(titleElement).toBeVisible();
    });
  });

  it("should render clock in leaderboard if prop is enabled", async () => {
    renderProviderFn?.(
      <LeaderboardMetadataById leaderboardId={MOCK_LEADERBOARD_ID} showClock />,
    );

    await waitFor(() => {
      const counter = screen.getByTestId("PrettyCounter");
      expect(counter).toBeInTheDocument();
      expect(counter).toBeVisible();
    });
  });

  it("should not render clock in leaderboard if prop is disabled", async () => {
    renderProviderFn?.(
      <LeaderboardMetadataById leaderboardId={MOCK_LEADERBOARD_ID} />,
    );

    await waitFor(() => {
      expect(
        screen.queryByTestId("leaderboard-skeleton-name"),
      ).not.toBeInTheDocument();
    });

    const counter = screen.queryByTestId("PrettyCounter");
    expect(counter).not.toBeInTheDocument();
  });

  it("should render Show All Leaderboards button in leaderboard if prop is enabled", async () => {
    renderProviderFn?.(
      <LeaderboardMetadataById
        leaderboardId={MOCK_LEADERBOARD_ID}
        showAllLeaderboardButton
      />,
    );

    await waitFor(() => {
      const button = screen.getByTestId("ShowAllLeaderboardsButton");
      expect(button).toBeInTheDocument();
      expect(button).toBeVisible();
      expect(button).toHaveAttribute("href", "/leaderboard/all");
    });
  });

  it("should not render Show All Leaderboards button in leaderboard if prop is disabled", async () => {
    renderProviderFn?.(
      <LeaderboardMetadataById leaderboardId={MOCK_LEADERBOARD_ID} />,
    );

    await waitFor(() => {
      expect(
        screen.queryByTestId("leaderboard-skeleton-name"),
      ).not.toBeInTheDocument();
    });

    const button = screen.queryByTestId("ShowAllLeaderboardsButton");
    expect(button).not.toBeInTheDocument();
  });

  it("should set document title after successful API call", async () => {
    renderProviderFn?.(
      <LeaderboardMetadataById leaderboardId={MOCK_LEADERBOARD_ID} />,
    );

    await waitFor(() => {
      expect(document.title).toBe(
        'CodeBloom - std::string november = "hello world"',
      );
    });
  });

  it("should set document description after successful API call", async () => {
    renderProviderFn?.(
      <LeaderboardMetadataById leaderboardId={MOCK_LEADERBOARD_ID} />,
    );

    await waitFor(() => {
      const metaDescription = document.querySelector(
        'meta[name="description"]',
      );
      expect(metaDescription).toBeInTheDocument();
      expect(metaDescription).toHaveAttribute(
        "content",
        "CodeBloom - View your rank in the leaderboard",
      );
    });
  });
});

describe("LeaderboardMetadataById API Failed", () => {
  const server = setupServer(...failedLeaderboardHandlers);

  beforeAll(() => server.listen());
  afterEach(() => {
    server.resetHandlers();
  });
  afterAll(() => server.close());

  let renderProviderFn: TestUtilTypes.RenderWithAllProvidersFn | null = null;
  beforeEach(() => {
    renderProviderFn = TestUtils.getRenderWithAllProvidersFn();
  });

  it("should render loading state initially", async () => {
    renderProviderFn?.(
      <LeaderboardMetadataById leaderboardId={MOCK_LEADERBOARD_ID} />,
    );

    await waitFor(() => {
      const element = screen.getByText("Leaderboard metadata failed to load");
      expect(element).toBeInTheDocument();
      expect(element).toBeVisible();
    });
  });
});

describe("LeaderboardMetadataById API Crashed", () => {
  const server = setupServer(...catastrophicLeaderboardHandlers);

  beforeAll(() => server.listen());
  afterEach(() => {
    server.resetHandlers();
  });
  afterAll(() => server.close());

  let renderProviderFn: TestUtilTypes.RenderWithAllProvidersFn | null = null;
  beforeEach(() => {
    renderProviderFn = TestUtils.getRenderWithAllProvidersFn();
  });

  it("should render loading state initially", async () => {
    renderProviderFn?.(
      <LeaderboardMetadataById leaderboardId={MOCK_LEADERBOARD_ID} />,
    );

    await waitFor(() => {
      const element = screen.getByText("Sorry, something went wrong.");
      expect(element).toBeInTheDocument();
      expect(element).toBeVisible();
    });
  });
});
