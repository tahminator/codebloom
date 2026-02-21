import {
  authErrorHandler,
  authSuccessHandler,
  authUnauthenticatedHandler,
} from "@/__mock__/auth";
import ReportIssuePage from "@/app/issue/report/ReportIssue.page";
import { TestUtils, TestUtilTypes } from "@/lib/test";
import { screen, waitFor } from "@testing-library/react";
import { setupServer } from "msw/node";

describe("ReportIssuePage Authentication", () => {
  let renderProviderFn: TestUtilTypes.RenderWithAllProvidersFn | null = null;
  beforeEach(() => {
    renderProviderFn = TestUtils.getRenderWithAllProvidersFn();
  });

  describe("Authenticated User", () => {
    const server = setupServer(authSuccessHandler);

    beforeAll(() => server.listen());
    afterEach(() => {
      server.resetHandlers();
    });
    afterAll(() => server.close());

    it("should render the report issue form when authenticated", async () => {
      renderProviderFn?.(<ReportIssuePage />);

      await waitFor(() => {
        const title = screen.getByText("Report an Issue");
        expect(title).toBeInTheDocument();
      });
    });

    it("should render all form fields when authenticated", async () => {
      renderProviderFn?.(<ReportIssuePage />);

      await waitFor(() => {
        expect(
          screen.getByPlaceholderText("Briefly describe the issue"),
        ).toBeInTheDocument();
        expect(
          screen.getByPlaceholderText(
            "Provide detailed information about the issue",
          ),
        ).toBeInTheDocument();
        expect(
          screen.getByPlaceholderText("your@email.com"),
        ).toBeInTheDocument();
      });
    });

    it("should render submit button when authenticated", async () => {
      renderProviderFn?.(<ReportIssuePage />);

      await waitFor(() => {
        const submitButton = screen.getByRole("button", {
          name: "Submit Report",
        });
        expect(submitButton).toBeInTheDocument();
      });
    });
  });

  describe("Unauthenticated User", () => {
    const server = setupServer(authUnauthenticatedHandler);

    beforeAll(() => server.listen());
    afterEach(() => {
      server.resetHandlers();
    });
    afterAll(() => server.close());

    it("should not render the form when not authenticated", async () => {
      renderProviderFn?.(<ReportIssuePage />);

      await waitFor(() => {
        expect(
          screen.queryByPlaceholderText("Briefly describe the issue"),
        ).not.toBeInTheDocument();
        expect(
          screen.queryByPlaceholderText(
            "Provide detailed information about the issue",
          ),
        ).not.toBeInTheDocument();
        expect(
          screen.queryByPlaceholderText("your@email.com"),
        ).not.toBeInTheDocument();
      });
    });

    it("should not render submit button when not authenticated", async () => {
      renderProviderFn?.(<ReportIssuePage />);

      await waitFor(() => {
        const submitButton = screen.queryByRole("button", {
          name: "Submit Report",
        });
        expect(submitButton).not.toBeInTheDocument();
      });
    });
  });

  describe("Auth Error", () => {
    const server = setupServer(authErrorHandler);

    beforeAll(() => server.listen());
    afterEach(() => {
      server.resetHandlers();
    });
    afterAll(() => server.close());

    it("should not render the form when auth check fails", async () => {
      renderProviderFn?.(<ReportIssuePage />);

      await waitFor(() => {
        expect(
          screen.queryByPlaceholderText("Briefly describe the issue"),
        ).not.toBeInTheDocument();
      });
    });

    it("should not render submit button when auth check fails", async () => {
      renderProviderFn?.(<ReportIssuePage />);

      await waitFor(() => {
        const submitButton = screen.queryByRole("button", {
          name: "Submit Report",
        });
        expect(submitButton).not.toBeInTheDocument();
      });
    });
  });

  describe("Loading State", () => {
    const server = setupServer(authSuccessHandler);

    beforeAll(() => server.listen());
    afterEach(() => {
      server.resetHandlers();
    });
    afterAll(() => server.close());

    it("should eventually render the form after auth check completes", async () => {
      renderProviderFn?.(<ReportIssuePage />);

      await waitFor(() => {
        expect(screen.getByText("Report an Issue")).toBeInTheDocument();
      });
    });
  });
});
