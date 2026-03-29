import {
  MOCK_LEADERBOARD_NAME,
  MOCK_LEADERBOARD_LANGUAGE,
  MOCK_LEADERBOARD_EXPIRE,
  editLeaderboardSuccessHandler,
} from "@/__mock__/admin";
import EditLeaderboardForm from "@/app/admin/_components/leaderboards/pagination/new-leaderboard/EditLeaderboardFormBody.tsx";
import { TestUtils, TestUtilTypes } from "@/lib/test";
import { screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { setupServer } from "msw/node";

describe("Render EditLeaderboardForm correctly", () => {
  const server = setupServer(editLeaderboardSuccessHandler);

  beforeAll(() => server.listen());
  afterEach(() => {
    server.resetHandlers();
  });
  afterAll(() => server.close());

  let renderProviderFn: TestUtilTypes.RenderWithAllProvidersFn | null = null;
  beforeEach(() => {
    renderProviderFn = TestUtils.getRenderWithAllProvidersFn();
  });

  it("should render edit button", () => {
    renderProviderFn?.(
      <EditLeaderboardForm
        currentLeaderboardName={MOCK_LEADERBOARD_NAME}
        currentLeaderboardExpire={MOCK_LEADERBOARD_EXPIRE}
        currentLeaderboardLanguage={MOCK_LEADERBOARD_LANGUAGE}
        disableButton={false}
      />,
    );

    const button = screen.getByText("Edit");
    expect(button).toBeInTheDocument();
    expect(button).toBeVisible();
  });

  it("should render form title on button click", async () => {
    renderProviderFn?.(
      <EditLeaderboardForm
        currentLeaderboardName={MOCK_LEADERBOARD_NAME}
        currentLeaderboardExpire={MOCK_LEADERBOARD_EXPIRE}
        currentLeaderboardLanguage={MOCK_LEADERBOARD_LANGUAGE}
        disableButton={false}
      />,
    );

    const user = userEvent.setup();

    const button = screen.getByRole("button", { name: "Edit" });
    await user.click(button);

    const title = screen.getByText("Edit Leaderboard");
    expect(title).toBeInTheDocument();
    expect(title).toBeVisible();
  });

  it("should render correct default form fields", async () => {
    renderProviderFn?.(
      <EditLeaderboardForm
        currentLeaderboardName={MOCK_LEADERBOARD_NAME}
        currentLeaderboardExpire={MOCK_LEADERBOARD_EXPIRE}
        currentLeaderboardLanguage={MOCK_LEADERBOARD_LANGUAGE}
        disableButton={false}
      />,
    );

    const user = userEvent.setup();

    const button = screen.getByRole("button", { name: "Edit" });
    await user.click(button);

    const name = screen.getByTestId("Name field");
    expect(name).toBeInTheDocument();
    expect(name).toBeVisible();
    expect(name).toHaveValue(MOCK_LEADERBOARD_NAME);

    const language = screen.getByTestId("Language field");
    expect(language).toBeInTheDocument();
    expect(language).toBeVisible();
    expect(language).toHaveValue(MOCK_LEADERBOARD_LANGUAGE);
  });

  it("should render submit button", async () => {
    renderProviderFn?.(
      <EditLeaderboardForm
        currentLeaderboardName={MOCK_LEADERBOARD_NAME}
        currentLeaderboardExpire={MOCK_LEADERBOARD_EXPIRE}
        currentLeaderboardLanguage={MOCK_LEADERBOARD_LANGUAGE}
        disableButton={false}
      />,
    );

    const user = userEvent.setup();

    const button = screen.getByRole("button", { name: "Edit" });
    await user.click(button);

    const submit = screen.getByRole("button", { name: "Submit" });
    expect(submit).toBeInTheDocument();
    expect(submit).toBeVisible();
  });
});

describe("EditLeaderboardForm API succeeded", () => {
  const server = setupServer(editLeaderboardSuccessHandler);

  beforeAll(() => server.listen());
  afterEach(() => {
    server.resetHandlers();
  });
  afterAll(() => server.close());

  let renderProviderFn: TestUtilTypes.RenderWithAllProvidersFn | null = null;
  beforeEach(() => {
    renderProviderFn = TestUtils.getRenderWithAllProvidersFn();
  });

  it("should reset form values to new inputted fields when submitted", async () => {
    renderProviderFn?.(
      <EditLeaderboardForm
        currentLeaderboardName={MOCK_LEADERBOARD_NAME}
        currentLeaderboardExpire={MOCK_LEADERBOARD_EXPIRE}
        currentLeaderboardLanguage={MOCK_LEADERBOARD_LANGUAGE}
        disableButton={false}
      />,
    );

    const user = userEvent.setup();

    const button = screen.getByRole("button", { name: "Edit" });
    await user.click(button);

    const nameInput = screen.getByTestId("Name field");
    await user.clear(nameInput);
    await user.type(nameInput, "new lb name");

    const languageInput = screen.getByTestId("Language field");
    await user.clear(languageInput);
    await user.type(languageInput, "python");

    const confirmInput = screen.getByTestId("Confirmation field");
    await user.type(confirmInput, MOCK_LEADERBOARD_NAME);

    const submit = screen.getByRole("button", { name: "Submit" });
    await user.click(submit);

    await waitFor(() => {
      expect(nameInput).toHaveValue("new lb name");
      expect(languageInput).toHaveValue("python");
    });
  });
});
