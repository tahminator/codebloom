import UserSubmissionsPage from "@/app/user/[userId]/submissions/UserSubmissions.page";
import { TestUtils, TestUtilTypes } from "@/lib/test";
import { screen } from "@testing-library/react";

// Mock ResizeObserver for Mantine ScrollArea
class ResizeObserverMock {
  observe() {}
  unobserve() {}
  disconnect() {}
}
window.ResizeObserver = ResizeObserverMock;

describe("UserSubmissionsPage", () => {
  describe("without date range", () => {
    const routeConfig: TestUtilTypes.RouteConfig = {
      initialPath: "/user/user-1/submissions",
      routePattern: "/user/:userId/submissions",
    };

    let renderProviderFn: TestUtilTypes.RenderWithAllProvidersFn | null = null;
    beforeEach(() => {
      renderProviderFn = TestUtils.getRenderWithRouteProvidersFn(routeConfig);
    });

    it("should render Go to profile button", () => {
      renderProviderFn?.(<UserSubmissionsPage />);

      expect(
        screen.getByRole("button", { name: "← Go to profile" }),
      ).toBeInTheDocument();
    });

    it("should render skeleton stack of submissions initially", () => {
      renderProviderFn?.(<UserSubmissionsPage />);

      const element = screen.getByTestId(
        "user-profile-skeleton-submissions-stack",
      );
      expect(element).toBeInTheDocument();
      expect(element).toBeVisible();
    });
  });

  describe("with date range", () => {
    const routeConfig: TestUtilTypes.RouteConfig = {
      initialPath:
        "/user/user-1/submissions?startDate=2025-01-01T00:00:00.000Z&endDate=2025-02-01T00:00:00.000Z",
      routePattern: "/user/:userId/submissions",
    };

    let renderProviderFn: TestUtilTypes.RenderWithAllProvidersFn | null = null;
    beforeEach(() => {
      renderProviderFn = TestUtils.getRenderWithRouteProvidersFn(routeConfig);
    });

    it("should render Go to profile button with date range", () => {
      renderProviderFn?.(<UserSubmissionsPage />);

      expect(
        screen.getByRole("button", { name: "← Go to profile" }),
      ).toBeInTheDocument();
    });
  });

  describe("with only startDate", () => {
    const routeConfig: TestUtilTypes.RouteConfig = {
      initialPath:
        "/user/user-1/submissions?startDate=2025-01-01T00:00:00.000Z",
      routePattern: "/user/:userId/submissions",
    };

    let renderProviderFn: TestUtilTypes.RenderWithAllProvidersFn | null = null;
    beforeEach(() => {
      renderProviderFn = TestUtils.getRenderWithRouteProvidersFn(routeConfig);
    });

    it("should render Go to profile button with startDate only", () => {
      renderProviderFn?.(<UserSubmissionsPage />);

      expect(
        screen.getByRole("button", { name: "← Go to profile" }),
      ).toBeInTheDocument();
    });
  });
});
