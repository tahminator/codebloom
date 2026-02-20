import UserProfilePage from "@/app/user/[userId]/UserProfile.page";
import { TestUtils, TestUtilTypes } from "@/lib/test";
import { screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";

class ResizeObserverMock {
  observe() {}
  unobserve() {}
  disconnect() {}
}
window.ResizeObserver = ResizeObserverMock;

describe("UserProfilePage", () => {
  describe("without date range", () => {
    const routeConfig: TestUtilTypes.RouteConfig = {
      initialPath: "/user/user-1",
      routePattern: "/user/:userId",
    };

    let renderProviderFn: TestUtilTypes.RenderWithAllProvidersFn | null = null;
    beforeEach(() => {
      renderProviderFn = TestUtils.getRenderWithRouteProvidersFn(routeConfig);
    });

    it("should render Recent Submissions heading", () => {
      renderProviderFn?.(<UserProfilePage />);

      expect(screen.getByText("Recent Submissions")).toBeInTheDocument();
    });

    it("should render View All button", () => {
      renderProviderFn?.(<UserProfilePage />);

      const viewAllButton = screen.getByRole("link", { name: "View All" });
      expect(viewAllButton).toBeInTheDocument();
    });

    it("should have View All link without date params", () => {
      renderProviderFn?.(<UserProfilePage />);

      const viewAllLink = screen.getByRole("link", { name: "View All" });
      expect(viewAllLink).toHaveAttribute("href", "/user/user-1/submissions");
    });

    it("should not render Clear Date Range button without date params", () => {
      renderProviderFn?.(<UserProfilePage />);

      expect(
        screen.queryByRole("button", { name: "Clear Date Range" }),
      ).not.toBeInTheDocument();
    });
  });

  describe("with date range", () => {
    const routeConfig: TestUtilTypes.RouteConfig = {
      initialPath:
        "/user/user-1?startDate=2025-01-01T00:00:00.000Z&endDate=2025-02-01T00:00:00.000Z",
      routePattern: "/user/:userId",
    };

    let renderProviderFn: TestUtilTypes.RenderWithAllProvidersFn | null = null;
    beforeEach(() => {
      renderProviderFn = TestUtils.getRenderWithRouteProvidersFn(routeConfig);
    });

    it("should render Clear Date Range button with date params", () => {
      renderProviderFn?.(<UserProfilePage />);

      expect(
        screen.getByRole("button", { name: "Clear Date Range" }),
      ).toBeInTheDocument();
    });

    it("should have View All link with date params", () => {
      renderProviderFn?.(<UserProfilePage />);

      const viewAllLink = screen.getByRole("link", { name: "View All" });
      expect(viewAllLink).toHaveAttribute(
        "href",
        expect.stringContaining("/user/user-1/submissions?startDate="),
      );
      expect(viewAllLink).toHaveAttribute(
        "href",
        expect.stringContaining("endDate="),
      );
    });

    it("should remove date params when Clear Date Range is clicked", async () => {
      const user = userEvent.setup();
      renderProviderFn?.(<UserProfilePage />);

      const clearButton = screen.getByRole("button", {
        name: "Clear Date Range",
      });
      await user.click(clearButton);

      await waitFor(() => {
        expect(
          screen.queryByRole("button", { name: "Clear Date Range" }),
        ).not.toBeInTheDocument();
      });
    });
  });

  describe("with only startDate", () => {
    const routeConfig: TestUtilTypes.RouteConfig = {
      initialPath: "/user/user-1?startDate=2025-01-01T00:00:00.000Z",
      routePattern: "/user/:userId",
    };

    let renderProviderFn: TestUtilTypes.RenderWithAllProvidersFn | null = null;
    beforeEach(() => {
      renderProviderFn = TestUtils.getRenderWithRouteProvidersFn(routeConfig);
    });

    it("should render Clear Date Range button with only startDate", () => {
      renderProviderFn?.(<UserProfilePage />);

      expect(
        screen.getByRole("button", { name: "Clear Date Range" }),
      ).toBeInTheDocument();
    });

    it("should have View All link with startDate only", () => {
      renderProviderFn?.(<UserProfilePage />);

      const viewAllLink = screen.getByRole("link", { name: "View All" });
      expect(viewAllLink).toHaveAttribute(
        "href",
        expect.stringContaining("/user/user-1/submissions?startDate="),
      );
      expect(viewAllLink).not.toHaveAttribute(
        "href",
        expect.stringContaining("endDate="),
      );
    });
  });
});
