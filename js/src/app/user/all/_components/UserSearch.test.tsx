import {
  getAllUsersErrorHandler,
  getAllUsersHandler,
} from "@/__mock__/user";
import UserSearch from "@/app/user/all/_components/UserSearch";
import { TestUtils, TestUtilTypes } from "@/lib/test";
import { screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { setupServer } from "msw/node";

describe("UserSearch API Succeeded", () => {
  const server = setupServer(getAllUsersHandler);

  beforeAll(() => server.listen());
  afterEach(() => {
    server.resetHandlers();
  });
  afterAll(() => server.close());

  let renderProviderFn: TestUtilTypes.RenderWithAllProvidersFn | null = null;
  beforeEach(() => {
    renderProviderFn = TestUtils.getRenderWithAllProvidersFn();
  });

  it("should render search box on initial load", async () => {
    renderProviderFn?.(<UserSearch />);

    const searchBox = screen.getByPlaceholderText("Search for a user...");
    expect(searchBox).toBeInTheDocument();
    expect(searchBox).toBeVisible();
  });

  it("should show loading state when searching", async () => {
    const user = userEvent.setup();
    renderProviderFn?.(<UserSearch />);

    const searchBox = screen.getByPlaceholderText("Search for a user...");
    await user.type(searchBox, "test");

    await waitFor(() => {
      expect(screen.getByTestId("user-search-skeleton")).toBeInTheDocument();
    });
  });

  it("should render user cards with discord names after searching", async () => {
    const user = userEvent.setup();
    renderProviderFn?.(<UserSearch />);

    const searchBox = screen.getByPlaceholderText("Search for a user...");
    await user.type(searchBox, "a");

    await waitFor(
      () => {
        const aphroditeEntry = screen.getByText("aphrodite");
        expect(aphroditeEntry).toBeInTheDocument();
      },
      { timeout: 2000 },
    );
  });

  it("should render leetcode usernames when available after search", async () => {
    const user = userEvent.setup();
    renderProviderFn?.(<UserSearch />);

    const searchBox = screen.getByPlaceholderText("Search for a user...");
    await user.type(searchBox, "aphrodite");

    await waitFor(
      () => {
        const aphroditeLc = screen.getByText("aphrodite_lc");
        expect(aphroditeLc).toBeInTheDocument();
      },
      { timeout: 2000 },
    );
  });

  it("should render verified nickname when available after search", async () => {
    const user = userEvent.setup();
    renderProviderFn?.(<UserSearch />);

    const searchBox = screen.getByPlaceholderText("Search for a user...");
    await user.type(searchBox, "poseidon");

    await waitFor(
      () => {
        const verifiedNickname = screen.getByText("Verified Poseidon");
        expect(verifiedNickname).toBeInTheDocument();
      },
      { timeout: 2000 },
    );
  });

  it("should render user cards as links to profile pages", async () => {
    const user = userEvent.setup();
    renderProviderFn?.(<UserSearch />);

    const searchBox = screen.getByPlaceholderText("Search for a user...");
    await user.type(searchBox, "aphrodite");

    await waitFor(
      () => {
        const userLinks = screen
          .getAllByRole("link")
          .filter((link) => link.getAttribute("href")?.includes("/user/"));

        expect(userLinks.length).toBeGreaterThan(0);

        const aphroditeLink = userLinks.find(
          (l) => l.getAttribute("href") === "/user/user-1",
        );
        expect(aphroditeLink).toBeInTheDocument();
      },
      { timeout: 2000 },
    );
  });

  it("should allow typing in search box", async () => {
    const user = userEvent.setup();
    renderProviderFn?.(<UserSearch />);

    const searchBox = screen.getByPlaceholderText("Search for a user...");
    await user.type(searchBox, "test");

    expect(searchBox).toHaveValue("test");
  });

  it("should filter users when searching", async () => {
    const user = userEvent.setup();
    renderProviderFn?.(<UserSearch />);

    const searchBox = screen.getByPlaceholderText("Search for a user...");
    await user.type(searchBox, "aphrodite");

    await waitFor(
      () => {
        expect(screen.getByText("aphrodite")).toBeInTheDocument();
        expect(screen.queryByText("poseidon")).not.toBeInTheDocument();
        expect(screen.queryByText("hermes")).not.toBeInTheDocument();
      },
      { timeout: 2000 },
    );
  });

  it("should show no results message when search has no matches", async () => {
    const user = userEvent.setup();
    renderProviderFn?.(<UserSearch />);

    const searchBox = screen.getByPlaceholderText("Search for a user...");
    await user.type(searchBox, "nonexistentuser123");

    await waitFor(
      () => {
        const noResultsMessage = screen.getByText(
          /No users found matching "nonexistentuser123"/,
        );
        expect(noResultsMessage).toBeInTheDocument();
      },
      { timeout: 2000 },
    );
  });
});

describe("UserSearch API Error", () => {
  const server = setupServer(getAllUsersErrorHandler);

  beforeAll(() => server.listen());
  afterEach(() => {
    server.resetHandlers();
  });
  afterAll(() => server.close());

  let renderProviderFn: TestUtilTypes.RenderWithAllProvidersFn | null = null;
  beforeEach(() => {
    renderProviderFn = TestUtils.getRenderWithAllProvidersFn();
  });

  it("should show toast when API errors", async () => {
    const user = userEvent.setup();
    renderProviderFn?.(<UserSearch />);

    const searchBox = screen.getByPlaceholderText("Search for a user...");
    await user.type(searchBox, "test");

    await waitFor(
      () => {
        const toast = screen.getByText("Sorry, something went wrong.");
        expect(toast).toBeInTheDocument();
      },
      { timeout: 2000 },
    );
  });
});
