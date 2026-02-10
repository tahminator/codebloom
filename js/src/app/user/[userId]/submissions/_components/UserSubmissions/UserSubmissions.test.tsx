import { MOCK_USER_ID } from "@/__mock__/user/index.ts";
import { screen, cleanup } from "@testing-library/react";
import { TestUtils, TestUtilTypes } from "@/lib/test";
import UserSubmissions from "./UserSubmissions.tsx";

describe("UserSubmissions succeeded", () => {
  afterEach(() => {
    cleanup();
  });

  let renderProviderFn: TestUtilTypes.RenderWithAllProvidersFn | null = null;
  beforeEach(() => {
    renderProviderFn = TestUtils.getRenderWithAllProvidersFn();
  });

  it("should render skeleton stack of submissions initially", () => {
    renderProviderFn?.(<UserSubmissions userId={MOCK_USER_ID} />);
    const element = screen.getByTestId(
      "user-profile-skeleton-submissions-stack",
    );
    expect(element).toBeInTheDocument();
    expect(element).toBeVisible();
  });
});
