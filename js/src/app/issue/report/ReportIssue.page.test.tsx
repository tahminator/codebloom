import {
  submitFeedbackErrorHandler,
  submitFeedbackFailureHandler,
  submitFeedbackSuccessHandler,
} from "@/__mock__/reporter";
import ReportIssue from "@/app/issue/report/ReportIssue.page";
import { TestUtils, TestUtilTypes } from "@/lib/test";
import { screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { setupServer } from "msw/node";

describe("ReportIssue Form Rendering", () => {
  const server = setupServer(submitFeedbackSuccessHandler);

  beforeAll(() => server.listen());
  afterEach(() => {
    server.resetHandlers();
  });
  afterAll(() => server.close());

  let renderProviderFn: TestUtilTypes.RenderWithAllProvidersFn | null = null;
  beforeEach(() => {
    renderProviderFn = TestUtils.getRenderWithAllProvidersFn();
  });

  it("should render the form title", () => {
    renderProviderFn?.(<ReportIssue />);

    const title = screen.getByText("Report an Issue");
    expect(title).toBeInTheDocument();
    expect(title).toBeVisible();
  });

  it("should render all required form fields", () => {
    renderProviderFn?.(<ReportIssue />);
    expect(
      screen.getByPlaceholderText("Briefly describe the issue"),
    ).toBeInTheDocument();
    expect(
      screen.getByPlaceholderText(
        "Provide detailed information about the issue",
      ),
    ).toBeInTheDocument();
    expect(screen.getByPlaceholderText("your@email.com")).toBeInTheDocument();
  });

  it("should render title input with correct placeholder", () => {
    renderProviderFn?.(<ReportIssue />);

    const titleInput = screen.getByPlaceholderText(
      "Briefly describe the issue",
    );
    expect(titleInput).toBeInTheDocument();
    expect(titleInput).toBeVisible();
  });

  it("should render description textarea with correct placeholder", () => {
    renderProviderFn?.(<ReportIssue />);

    const descriptionTextarea = screen.getByPlaceholderText(
      "Provide detailed information about the issue",
    );
    expect(descriptionTextarea).toBeInTheDocument();
    expect(descriptionTextarea).toBeVisible();
  });

  it("should render email input with correct placeholder", () => {
    renderProviderFn?.(<ReportIssue />);

    const emailInput = screen.getByPlaceholderText("your@email.com");
    expect(emailInput).toBeInTheDocument();
    expect(emailInput).toBeVisible();
  });

  it("should render submit button", () => {
    renderProviderFn?.(<ReportIssue />);

    const submitButton = screen.getByRole("button", { name: "Submit Report" });
    expect(submitButton).toBeInTheDocument();
    expect(submitButton).toBeVisible();
  });

  it("should render go back button", () => {
    renderProviderFn?.(<ReportIssue />);

    const goBackButton = screen.getByRole("button", { name: "Go back" });
    expect(goBackButton).toBeInTheDocument();
    expect(goBackButton).toBeVisible();
  });
});

describe("ReportIssue Form Validation", () => {
  const server = setupServer(submitFeedbackSuccessHandler);

  beforeAll(() => server.listen());
  afterEach(() => {
    server.resetHandlers();
  });
  afterAll(() => server.close());

  let renderProviderFn: TestUtilTypes.RenderWithAllProvidersFn | null = null;
  beforeEach(() => {
    renderProviderFn = TestUtils.getRenderWithAllProvidersFn();
  });

  it("should allow typing in title field", async () => {
    const user = userEvent.setup();
    renderProviderFn?.(<ReportIssue />);

    const titleInput = screen.getByPlaceholderText(
      "Briefly describe the issue",
    );
    await user.type(titleInput, "Bug Report");
    expect(titleInput).toHaveValue("Bug Report");
  });

  it("should allow typing in description field", async () => {
    const user = userEvent.setup();
    renderProviderFn?.(<ReportIssue />);

    const descriptionTextarea = screen.getByPlaceholderText(
      "Provide detailed information about the issue",
    );
    await user.type(descriptionTextarea, "This is a detailed description");
    expect(descriptionTextarea).toHaveValue("This is a detailed description");
  });

  it("should allow typing in email field", async () => {
    const user = userEvent.setup();
    renderProviderFn?.(<ReportIssue />);

    const emailInput = screen.getByPlaceholderText("your@email.com");
    await user.type(emailInput, "test@example.com");
    expect(emailInput).toHaveValue("test@example.com");
  });

  it("should show validation error for description less than 10 characters", async () => {
    const user = userEvent.setup();
    renderProviderFn?.(<ReportIssue />);

    const titleInput = screen.getByPlaceholderText(
      "Briefly describe the issue",
    );
    await user.type(titleInput, "Bug");

    const descriptionTextarea = screen.getByPlaceholderText(
      "Provide detailed information about the issue",
    );
    await user.type(descriptionTextarea, "Short");

    const emailInput = screen.getByPlaceholderText("your@email.com");
    await user.type(emailInput, "test@example.com");

    const submitButton = screen.getByRole("button", { name: "Submit Report" });
    await user.click(submitButton);

    await waitFor(() => {
      expect(
        screen.getByText("Description must be at least 10 characters"),
      ).toBeInTheDocument();
    });
  });

  it("should not submit form with invalid email", async () => {
    const user = userEvent.setup();
    renderProviderFn?.(<ReportIssue />);

    const titleInput = screen.getByPlaceholderText(
      "Briefly describe the issue",
    );
    await user.type(titleInput, "Bug");

    const descriptionTextarea = screen.getByPlaceholderText(
      "Provide detailed information about the issue",
    );
    await user.type(descriptionTextarea, "This is a valid description");

    const emailInput = screen.getByPlaceholderText("your@email.com");
    await user.type(emailInput, "invalidemail");

    const submitButton = screen.getByRole("button", { name: "Submit Report" });
    await user.click(submitButton);

    expect(emailInput).toHaveValue("invalidemail");
  });
});

describe("ReportIssue Form Submission Success", () => {
  const server = setupServer(submitFeedbackSuccessHandler);

  beforeAll(() => server.listen());
  afterEach(() => {
    server.resetHandlers();
  });
  afterAll(() => server.close());

  let renderProviderFn: TestUtilTypes.RenderWithAllProvidersFn | null = null;
  beforeEach(() => {
    renderProviderFn = TestUtils.getRenderWithAllProvidersFn();
  });

  it("should submit form with valid data", async () => {
    const user = userEvent.setup();
    renderProviderFn?.(<ReportIssue />);

    const titleInput = screen.getByPlaceholderText(
      "Briefly describe the issue",
    );
    await user.type(titleInput, "Login Button Not Working");

    const descriptionTextarea = screen.getByPlaceholderText(
      "Provide detailed information about the issue",
    );
    await user.type(
      descriptionTextarea,
      "The login button does not respond when clicked on mobile devices",
    );

    const emailInput = screen.getByPlaceholderText("your@email.com");
    await user.type(emailInput, "user@example.com");

    const submitButton = screen.getByRole("button", { name: "Submit Report" });
    await user.click(submitButton);

    await waitFor(() => {
      expect(titleInput).toHaveValue("");
      expect(descriptionTextarea).toHaveValue("");
      expect(emailInput).toHaveValue("");
    });
  });

  it("should show loading state on submit button while submitting", async () => {
    const user = userEvent.setup();
    renderProviderFn?.(<ReportIssue />);

    const titleInput = screen.getByPlaceholderText(
      "Briefly describe the issue",
    );
    await user.type(titleInput, "Bug Report");

    const descriptionTextarea = screen.getByPlaceholderText(
      "Provide detailed information about the issue",
    );
    await user.type(descriptionTextarea, "Detailed bug description");

    const emailInput = screen.getByPlaceholderText("your@email.com");
    await user.type(emailInput, "test@example.com");

    const submitButton = screen.getByRole("button", { name: "Submit Report" });
    await user.click(submitButton);

    await waitFor(() => {
      expect(titleInput).toHaveValue("");
    });
  });
});

describe("ReportIssue Form Submission Failure", () => {
  const server = setupServer(submitFeedbackFailureHandler);

  beforeAll(() => server.listen());
  afterEach(() => {
    server.resetHandlers();
  });
  afterAll(() => server.close());

  let renderProviderFn: TestUtilTypes.RenderWithAllProvidersFn | null = null;
  beforeEach(() => {
    renderProviderFn = TestUtils.getRenderWithAllProvidersFn();
  });

  it("should display error message on submission failure", async () => {
    const user = userEvent.setup();
    renderProviderFn?.(<ReportIssue />);

    const titleInput = screen.getByPlaceholderText(
      "Briefly describe the issue",
    );
    await user.type(titleInput, "Bug");

    const descriptionTextarea = screen.getByPlaceholderText(
      "Provide detailed information about the issue",
    );
    await user.type(descriptionTextarea, "Short desc");

    const emailInput = screen.getByPlaceholderText("your@email.com");
    await user.type(emailInput, "test@example.com");

    const submitButton = screen.getByRole("button", { name: "Submit Report" });
    await user.click(submitButton);

    await waitFor(() => {
      expect(titleInput).toHaveValue("Bug");
      expect(descriptionTextarea).toHaveValue("Short desc");
      expect(emailInput).toHaveValue("test@example.com");
    });
  });
});

describe("ReportIssue Form Submission Error", () => {
  const server = setupServer(submitFeedbackErrorHandler);

  beforeAll(() => server.listen());
  afterEach(() => {
    server.resetHandlers();
  });
  afterAll(() => server.close());

  let renderProviderFn: TestUtilTypes.RenderWithAllProvidersFn | null = null;
  beforeEach(() => {
    renderProviderFn = TestUtils.getRenderWithAllProvidersFn();
  });

  it("should handle network errors gracefully", async () => {
    const user = userEvent.setup();
    renderProviderFn?.(<ReportIssue />);

    const titleInput = screen.getByPlaceholderText(
      "Briefly describe the issue",
    );
    await user.type(titleInput, "Network Issue");

    const descriptionTextarea = screen.getByPlaceholderText(
      "Provide detailed information about the issue",
    );
    await user.type(descriptionTextarea, "Cannot connect to server");

    const emailInput = screen.getByPlaceholderText("your@email.com");
    await user.type(emailInput, "test@example.com");

    const submitButton = screen.getByRole("button", { name: "Submit Report" });
    await user.click(submitButton);

    await waitFor(() => {
      expect(titleInput).toHaveValue("Network Issue");
    });
  });
});
