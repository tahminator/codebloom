import UserSubmissions from "@/app/user/[userId]/submissions/_components/UserSubmissions/UserSubmissions.tsx";
import { TestUtils, TestUtilTypes } from "@/lib/test";
import { screen, cleanup } from "@testing-library/react";
import { v4 as uuid } from "uuid";

describe("UserSubmissions succeeded", () => {
  afterEach(() => {
    cleanup();
  });

  let renderProviderFn: TestUtilTypes.RenderWithAllProvidersFn | null = null;
  beforeEach(() => {
    renderProviderFn = TestUtils.getRenderWithAllProvidersFn();
  });

  it("should render skeleton stack of submissions initially", () => {
    const MOCK_USER_ID = uuid();
    renderProviderFn?.(<UserSubmissions userId={MOCK_USER_ID} />);
    const element = screen.getByTestId(
      "user-profile-skeleton-submissions-stack",
    );
    expect(element).toBeInTheDocument();
    expect(element).toBeVisible();
  });
});
